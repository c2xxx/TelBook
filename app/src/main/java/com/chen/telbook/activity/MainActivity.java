package com.chen.telbook.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.helper.TokenHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    public static final String TELPHONEBOOK = "TELPHONEBOOK";
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_DELETE = 2;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;

    private TelNumAdapter telAdapter;
    private List<TelNum> telList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        telAdapter = new TelNumAdapter(this, telList);

        telAdapter.setOnItemClick(new TelNumAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                doSelectedPosition(position);
            }
        });
        //设置布局管理器
        rvMain.setLayoutManager(new LinearLayoutManager(this));
//设置adapter
        rvMain.setAdapter(telAdapter);
//设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());

        loadLocolData();
        loadRemoteData();
    }

    private void loadRemoteData() {
        String url = Constants.urlXml + "?t=" + System.currentTimeMillis();
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                Logger.d("   " + response);
                String strBase64 = Base64.encodeToString(response.getBytes(), Base64.DEFAULT);
                try {
                    List<TelNum> list = TelBookXmlHelper.parse(response);
                    if (list != null && !list.isEmpty()) {
                        SharedPerferencesHelper.save(TELPHONEBOOK, strBase64);
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
//            ToastUtil.show(tel.getName());
        }
    }

    private void doCall(String tel) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有拨号权限！", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }

    /**
     * 加载本地数据
     */
    private void loadLocolData() {
        String strBase64 = SharedPerferencesHelper.read(TELPHONEBOOK);
        if (TextUtils.isEmpty(strBase64)) {
            List<TelNum> list = new ArrayList<>();
            TelNum telNum = new TelNum();
            telNum.setImg("");
            telNum.setName("陈辉");
            telNum.setTel("15659002326");
            list.add(telNum);
            telAdapter.setData(list);
        } else {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final EditText et = new EditText(this);
        boolean needPwd = false;
        if (needPwd) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("请输入123，进入管理")
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
                            switch (item.getItemId()) {
                                case Menu.FIRST + 1:
                                    Intent intent = new Intent(MainActivity.this, AddTelphoneNumberActivity.class);
                                    startActivityForResult(intent, REQUEST_ADD);
                                    break;
                                case Menu.FIRST + 2:
                                    Intent intent2 = new Intent(MainActivity.this, TelephoneDeleteActivity.class);
                                    startActivityForResult(intent2, REQUEST_DELETE);
                                    break;
                            }
                        }
                    }).create();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } else {
            switch (item.getItemId()) {
                case Menu.FIRST + 1:
                    Intent intent = new Intent(MainActivity.this, AddTelphoneNumberActivity.class);
                    startActivityForResult(intent, REQUEST_ADD);
                    break;
                case Menu.FIRST + 2:
                    Intent intent2 = new Intent(MainActivity.this, TelephoneDeleteActivity.class);
                    startActivityForResult(intent2, REQUEST_DELETE);
                    break;
            }
        }
        return false;
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
            }
        }
    }

    private void saveCurrentData() {
        try {
            String xmlContent = TelBookXmlHelper.writeToString(telList);
            File file = File.createTempFile("xx" + System.currentTimeMillis(), "xml");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(xmlContent);
            bw.close();

            String key = "telbook.xml";
            String token = TokenHelper.getToken(key);
            uploadFile(key, token, file);
        } catch (Exception e) {
            ToastUtil.show("保存失败！");
            Logger.e(e);
        }
    }

    private void uploadFile(String key, String token, File file) {

        String url = "http://upload.qiniu.com/";
        Map<String, Object> params = new Hashtable<>();
        params.put("key", key);
        params.put("token", token);
        params.put("file", file);
        new BaseRequest().postFile(url, params, new NetCallback() {
            @Override
            public void onResponse(String response) {
                ToastUtil.show("保存成功！");
            }

            @Override
            public void onFailure(Exception e) {
                ToastUtil.show("保存到网络失败！");
            }
        });
    }
}
