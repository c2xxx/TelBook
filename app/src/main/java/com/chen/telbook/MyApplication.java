package com.chen.telbook;

import android.app.Application;
import android.content.Context;

import com.chen.libchen.LibChenInit;
import com.chen.telbook.constants.Constants;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

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
    }


    public static Context getContext() {
        return context;
    }
}
