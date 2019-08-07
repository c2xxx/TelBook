package com.chen.telbook;

import android.app.Application;
import android.content.Context;

import com.chen.libchen.LibChenInit;
import com.chen.telbook.constants.Constants;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.commonsdk.UMConfigure;

/**
 * Created by hui on 2016/10/8.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=565807f9");//讯飞语音注册

        Constants.initUserName();
        LibChenInit.init(context);

        initUmeng();
    }

    private void initUmeng() {
        String appKey = "5d4aaa860cafb2cd23000480";
        String channel = "telBook";
        if (BuildConfig.IS_DEBUG) {
            channel = "telBook_debug";
        }
        int deviceType = UMConfigure.DEVICE_TYPE_PHONE;
        String pushSecret = null;
        UMConfigure.init(this, appKey, channel, deviceType, pushSecret);
    }


    public static Context getContext() {
        return context;
    }
}
