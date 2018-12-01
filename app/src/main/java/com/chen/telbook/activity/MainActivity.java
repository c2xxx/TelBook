package com.chen.telbook.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.OnItemClick;
import com.chen.telbook.adapter.OnItemLongClick;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.CheckNewMissedCall;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.helper.TokenHelper;
import com.chen.telbook.helper.UpdateHelper;
import com.chen.telbook.helper.VoicePlay;
import com.chen.telbook.helper.XunFeiVoiceReadHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;
import com.chen.telbook.utils.PermissionHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    //    public static final String TEL_PHONE_BOOK = "TEL_PHONE_BOOK";
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_DELETE = 2;
    private static final int REQUEST_RENAME = 3;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.iv_call_log)
    ImageView ivCallLog;

    private TelNumAdapter telAdapter;
    private List<TelNum> telList = new ArrayList<>();
    private long lastReadTime = System.currentTimeMillis();

    XunFeiVoiceReadHelper readHelper;
    private String permission_call_phone = Manifest.permission.CALL_PHONE;
    private String permission_call_log = Manifest.permission.READ_CALL_LOG;
    private String permission_read_contacts = Manifest.permission.READ_CONTACTS;
    private String permission_read_sd = Manifest.permission.READ_EXTERNAL_STORAGE;
    private AlertDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ivCallLog.getBackground().setAlpha(200);
        initData();
        readHelper = new XunFeiVoiceReadHelper(this);
        readHelper.readText(" ");//目的是初始化一次
        checkNewMissCall();
        UpdateHelper.checkUpdate(this);
    }

    /**
     * 如果有新的未接电话则提示
     */
    private void checkNewMissCall() {
        if (new CheckNewMissedCall().hasNewMissCall()) {
            VoicePlay.playNewMissedCall();
            gotoActivityCallLog();
        }
    }

    @OnClick(R.id.iv_call_log)
    public void gotoActivityCallLog() {
        boolean hasPermission = PermissionHelper.check(this, permission_call_log);
        if (hasPermission) {
            startActivity(new Intent(this, CallLogActivity.class));
        } else {
            ToastUtil.show("没有权限");
        }
    }

    private void initData() {
        telAdapter = new TelNumAdapter(this, telList);

        telAdapter.setOnItemLongClick(new OnItemLongClick() {
            @Override
            public void onItemLongClick(int position) {
                doSelectedPosition(position);
            }
        });
        telAdapter.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(int position) {
                shortClick(position);
            }
        });
        //设置布局管理器
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        rvMain.setAdapter(telAdapter);
        //设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());

        loadLocalData();
        loadRemoteData();
    }


    private void loadRemoteData() {
        String url = Constants.urlXml + "?t=" + System.currentTimeMillis();
        Logger.d("url=" + url);
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                lastReadTime = System.currentTimeMillis();
                Logger.d("   " + response);
                Logger.d("得到远程数据：" + response);
                String strBase64 = Base64.encodeToString(response.getBytes(), Base64.DEFAULT);
                try {
                    List<TelNum> list = TelBookXmlHelper.parse(response);
                    if (list != null && !list.isEmpty()) {
                        String key = SharedPerferencesHelper.getPhoneBookKey();
                        SharedPerferencesHelper.save(key, strBase64);
                        telAdapter.setData(list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        };
        BaseRequest.getInstance().get(url, null, callback);
    }

    private void doSelectedPosition(int position) {
        if (telList != null && telList.size() > position) {
            TelNum tel = telList.get(position);
            doCall(tel.getTel());
        }
    }

    private void shortClick(int position) {
//        TelNum tel = telList.get(position);
//        readHelper.readText(tel.getName() + "," + tel.getTel().replace(" ","").replace("-",""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        readHelper.stop();
    }

    /**
     * 拨打电话
     *
     * @param tel
     */
    private void doCall(String tel) {
        boolean hasPermission = PermissionHelper.check(this, permission_call_phone);
        if (hasPermission) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        } else {
            ToastUtil.show("没有拨号权限！");
        }
    }

    /**
     * 加载本地数据
     */
    private void loadLocalData() {
        String key = SharedPerferencesHelper.getPhoneBookKey();
        String strBase64 = SharedPerferencesHelper.read(key);
        if (TextUtils.isEmpty(strBase64)) {
            List<TelNum> list = new ArrayList<>();
            if ("telbook".equals(Constants.USER_NAME)) {
                TelNum telNum = new TelNum();
                telNum.setImg("");
                telNum.setName("陈辉");
                telNum.setTel("15659002326");
                list.add(telNum);
            }
            telAdapter.setData(list);
        } else {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            Logger.d("读取到的本地数据：" + strResult);
            try {
                List<TelNum> list = TelBookXmlHelper.parse(strResult);
                telAdapter.setData(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         *
         * add()方法的四个参数，依次是：
         *
         * 1、组别，如果不分组的话就写Menu.NONE,
         *
         * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
         *
         * 3、顺序，那个菜单现在在前面由这个参数的大小决定
         *
         * 4、文本，菜单的显示文本
         */

        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "添加");
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "删除");
        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "登录");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_PHONE);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (confirmDialog != null && confirmDialog.isShowing()) {
                    if ("123".equals(s.toString())) {
                        confirmDialog.dismiss();
                        doMenuSelect(item);
                    }
                }
            }
        });
        et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, 0);
            }
        }, 100);
        confirmDialog = new AlertDialog.Builder(this)
                .setTitle("为了防止误操作\n请输入123，进入管理")
//                .setIcon(R.drawable.ic_launcher)
                .setView(et)
                //相当于点击确认按钮
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!"123".equals(et.getText().toString().trim())) {
                            ToastUtil.show("输入错误！");
                            return;
                        }
                    }
                }).create();
        confirmDialog.setCancelable(true);
        confirmDialog.setCanceledOnTouchOutside(true);
        confirmDialog.show();
        return false;
    }

    public void doMenuSelect(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                boolean hasPermission1 = PermissionHelper.check(MainActivity.this, permission_read_contacts);
                boolean hasPermission2 = PermissionHelper.check(MainActivity.this, permission_read_sd);
                if (hasPermission1 && hasPermission2) {
                    Intent intent = new Intent(MainActivity.this, AddTelphoneNumberActivity.class);
                    startActivityForResult(intent, REQUEST_ADD);
                } else {
                    ToastUtil.show("没有权限");
                }
                break;
            case Menu.FIRST + 2:
                Intent intent2 = new Intent(MainActivity.this, TelephoneDeleteActivity.class);
                startActivityForResult(intent2, REQUEST_DELETE);
                break;
            case Menu.FIRST + 3:
                Intent intent3 = new Intent(MainActivity.this, LoginByNameActivity.class);
                startActivityForResult(intent3, REQUEST_RENAME);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.isUserNameEmpty()) {
            ToastUtil.show("请登录~~~");
            Intent intent3 = new Intent(MainActivity.this, LoginByNameActivity.class);
            startActivityForResult(intent3, REQUEST_RENAME);
        }

        if (System.currentTimeMillis() - lastReadTime > 60 * 1000 * 3) {
            loadRemoteData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD && data != null) {
                TelNum telNum = new TelNum();
                telNum.setImg(data.getStringExtra("img"));
                telNum.setName(data.getStringExtra("name"));
                telNum.setTel(data.getStringExtra("tel"));
                telList.add(telNum);
                telAdapter.notifyDataSetChanged();
                saveCurrentData();
            } else if (requestCode == REQUEST_DELETE && data != null) {
                TelNum telNum = new TelNum();
                telNum.setImg(data.getStringExtra("img"));
                telNum.setName(data.getStringExtra("name"));
                telNum.setTel(data.getStringExtra("tel"));
                int position = -1;
                for (int i = 0, len = telList.size(); i < len; i++) {
                    TelNum telNum1 = telList.get(i);
                    if (TextUtils.equals(telNum1.getTel(), telNum.getTel())) {
                        if (TextUtils.equals(telNum1.getName(), telNum.getName())) {
                            position = i;
                            break;
                        }
                    }
                }
                if (position != -1) {
                    telList.remove(position);
                    telAdapter.notifyDataSetChanged();
                    saveCurrentData();
                }
            } else if (requestCode == REQUEST_RENAME) {
                clearCurrentData();
                loadRemoteData();
            }
        }
    }

    private void clearCurrentData() {
        SharedPerferencesHelper.save(SharedPerferencesHelper.getPhoneBookKey(), "");
        telAdapter.setData(new ArrayList<TelNum>());
    }

    private void saveCurrentData() {
        try {
            String xmlContent = TelBookXmlHelper.writeToString(telList);
            File file = File.createTempFile("xx" + System.currentTimeMillis(), "xml");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(xmlContent);
            bw.close();

            String key = Constants.USER_BOOK_FILE_NAME;
            String token = TokenHelper.getToken(key);
            ToastUtil.show("正在保存");
            NetCallback callback = new NetCallback() {
                @Override
                public void onResponse(String response) {
                    ToastUtil.show("保存成功！");
                }

                @Override
                public void onFailure(Exception e) {
                    ToastUtil.show("保存到网络失败！");
                    loadRemoteData();
                }
            };
            uploadFile(key, token, file, callback);
        } catch (Exception e) {
            ToastUtil.show("保存失败！");
            loadRemoteData();
            Logger.e(e);
        }
    }

    private void uploadFile(String key, String token, File file, NetCallback callback) {

        String url = "http://upload.qiniu.com/";
        Map<String, Object> params = new Hashtable<>();
        params.put("key", key);
        params.put("token", token);
        params.put("file", file);
        new BaseRequest().postFile(url, params, callback);
    }
}
