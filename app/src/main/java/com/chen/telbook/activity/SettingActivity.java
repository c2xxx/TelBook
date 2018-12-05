package com.chen.telbook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.chen.telbook.R;

/**
 * Created by ChenHui on 2018/12/5.
 */
public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("帮助/设置");
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
    4、读取通话记录，检查通话记录图标是否正确（拨出，已接，未接）
    5、添加和删除通讯录内容
    6、点击通话记录语音播报记录情况
    //设置
    //检查升级
    //     **/
}
