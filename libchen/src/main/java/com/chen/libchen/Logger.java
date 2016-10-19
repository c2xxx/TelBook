package com.chen.libchen;

import android.util.Log;

/**
 * 打印日志
 * Created by hui on 2016/10/6.
 */

public class Logger {
    private static boolean isDebug = BuildConfig.DEBUG;
    private static String TAG = "Logger";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(Throwable t) {
        Log.e(TAG, Log.getStackTraceString(t));
    }
}
