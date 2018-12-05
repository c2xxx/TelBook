package com.chen.telbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chen.libchen.ToastUtil;
import com.chen.telbook.BuildConfig;
import com.chen.telbook.R;
import com.chen.telbook.helper.UpdateHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ChenHui on 2018/12/5.
 */
public class MoreActivity extends BaseActivity {
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        setTitle("帮助/设置");
        String version = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        tvVersion.setText("当前版本：" + version + "(" + versionCode + ")");
    }

    //检查权限是否齐全
    /*
    1、拨打电话
    2、读取通讯录
    3、读取通话记录
    4、接收电话弹框
    5、开启开机自动启动
    //介绍特色功能
    //校验每项功能是否完整
    1、正常显示通讯录，以及图片
    2、正常拨打电话
    3、接收图片通讯录内的通话，是否显示联系人头像
    4、读取通话记录，检查通话记录图标是否正确（播出，已接，未接）
    5、添加和删除通讯录内容
    6、点击通话记录语音播报记录情况
    //设置
    //检查升级
    //     **/

    @OnClick(R.id.btn_login)
    public void doLogin() {
        Intent intent = new Intent(this, LoginByNameActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.btn_check_update)
    public void checkUpdate() {
        ToastUtil.show("开始检查升级");
        UpdateHelper.checkUpdate(this);
    }

    @OnClick(R.id.btn_check_permission)
    public void checkPermission() {
        ToastUtil.show("检查权限");
    }

    @OnClick(R.id.btn_setting)
    public void doSetting() {
        ToastUtil.show("设置");
    }

}
