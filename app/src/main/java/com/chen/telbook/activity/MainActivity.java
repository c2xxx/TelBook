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
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.OnItemClick;
import com.chen.telbook.adapter.OnItemLongClick;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.CheckNewMissedCall;
import com.chen.telbook.helper.StatisticsHelper;
import com.chen.telbook.helper.TelBookManager;
import com.chen.telbook.helper.UpdateHelper;
import com.chen.telbook.helper.VoicePlay;
import com.chen.telbook.helper.XunFeiVoiceReadHelper;
import com.chen.telbook.utils.PermissionHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
        UpdateHelper.checkUpdateHomePage(this);
        checkPermission();
    }

    private void checkPermission() {
        rvMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isForeground()) {
                    PermissionHelper.requestAll(MainActivity.this);
                }
            }
        }, 1000);
        rvMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isForeground()) {
                    PermissionHelper.checkAlertWindow(MainActivity.this);
                }
            }
        }, 5000);
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

        telAdapter.setData(TelBookManager.getInstance().getList());
        loadRemoteData();

        TelBookManager.getInstance().addListener(new TelBookManager.OnDataChange() {
            @Override
            public void onChange() {
                telAdapter.setData(TelBookManager.getInstance().getList());
                telAdapter.notifyDataSetChanged();
            }
        });

        onStatistics();
    }

    /**
     * 统计
     */
    private void onStatistics() {
        StatisticsHelper helper = new StatisticsHelper();
        helper.publishEvent(this, "HOME_ACTIVITY_OPEN");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> map = new HashMap<>();
        map.put("userName", Constants.USER_NAME);
        map.put("time", "" + sdf.format(System.currentTimeMillis()));
        map.put("event", "openHomeActivity");
        helper.publishEvent(this, "HOME_ACTIVITY_OPEN_DETAIL", map);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TelBookManager.getInstance().clearListener();
    }

    private void loadRemoteData() {
        TelBookManager.getInstance().loadRemoteData();
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
        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "更多");
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
                Intent intent3 = new Intent(MainActivity.this, MoreActivity.class);
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
        loadRemoteData();
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
                TelBookManager.getInstance().addTel(telNum);
            } else if (requestCode == REQUEST_DELETE && data != null) {
                TelNum telNum = new TelNum();
                telNum.setImg(data.getStringExtra("img"));
                telNum.setName(data.getStringExtra("name"));
                telNum.setTel(data.getStringExtra("tel"));
                TelBookManager.getInstance().removeTel(telNum);
            }
        }
    }


}
