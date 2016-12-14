package com.chen.libchen;

import android.text.TextUtils;
import android.util.Log;

/**
 * 打印日志
 * Created by hui on 2016/10/6.
 */

public class Logger {
    private static boolean isDebug = true;
    private static String TAG = "Logger";
    private static final boolean DETAIL_ENABLE = true;

//    static {
//        com.orhanobut.logger.Logger.init(TAG);
//    }

    /**
     * 打印程序调用堆栈信息
     */
    public static void trackInfo() {
        if (DETAIL_ENABLE) {
            StackTraceElement[] tracks = Thread.currentThread().getStackTrace();
            Log.i(TAG, "tracks:-----------start---------------");
            for (StackTraceElement track : tracks) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(track.getClassName() + ":" + track.getMethodName());
                buffer.append("(");
                buffer.append(track.getFileName());
                buffer.append(":");
                buffer.append(track.getLineNumber());
                buffer.append(")");
                Log.i(TAG, buffer.toString());
            }
            Log.i(TAG, "tracks:-----------end---------------");
        }
    }

    public static void d(String tag, String msg) {
        if (!isDebug) {
            return;
        }

        Log.d(TAG, tag + "\n" + msg);
    }

    public static void d(String msg) {
        if (!isDebug) {
            return;
        }
        if (msg != null && msg.length() < 100) {
            msg = msg + " " + getPosition();
        } else {
            msg = getPosition() + "\n" + msg;
        }
        Log.d(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (!isDebug) {
            return;
        }
        Log.e(TAG, tag + "\n" + msg);
    }

    public static void e(String msg) {
        if (!isDebug) {
            return;
        }

        if (msg != null && msg.length() < 100) {
            msg = msg + " " + getPosition();
        } else {
            msg = getPosition() + "\n" + msg;
        }
        Log.e(TAG, msg);
    }

    public static void printWholeMsg(String msg) {
        if (!isDebug) {
            return;
        }
        d(msg);
//        com.orhanobut.logger.Logger.d(msg);
    }
//    /**
//     * 打印完整的信息
//     */
//    public static void printWholeMsg(String msg) {
//        if (isDebug) {
//            if (msg == null) {
//                return;
//            }
//            int len = msg.length();
//            int maxLogSize = 300;//每次打印字符
//            double count = Math.ceil(len * 1.0 / maxLogSize);//打印次数
//
//            Log.d(TAG, "================START==================");
//            for (int i = 0; i <= count; i++) {
//
//                int start = i * maxLogSize;
//
//                int end = (i + 1) * maxLogSize;
//
//                end = end > len ? len : end;
//
//                Log.d(TAG, String.format("Part%s    %s", i + 1, msg.substring(start, end)));
//
//            }
//            Log.d(TAG, "================END==================");
//        }
//    }

    /**
     * 打印异常信息
     *
     * @param e
     */
    public static void e(Throwable e) {
        if (!isDebug) {
            return;
        }
//        e(getPosition());

        e(Log.getStackTraceString(e));
        String msg = e.getMessage();
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (msg.indexOf("TimeoutException") != -1) {
            //超时不上报
            return;
        }
        if (msg.indexOf("UnknownHostException") != -1) {
            //超时不上报
            return;
        }
        //上传报错！！！
    }

    /**
     * 获取调用位置
     *
     * @return
     */
    private static String getPosition() {
        if (DETAIL_ENABLE) {
            StringBuilder buffer = new StringBuilder();

            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];

            if ("Logger.java".equals(stackTraceElement.getFileName())) {
                stackTraceElement = Thread.currentThread().getStackTrace()[5];
            }

            buffer.append("(");
            buffer.append(stackTraceElement.getFileName());
            buffer.append(":");
            buffer.append(stackTraceElement.getLineNumber());
            buffer.append(")");
            return buffer.toString();
        }
        return null;

    }

    public static void json(String s) {
        if (!isDebug) {
            return;
        }
//        msg = getPosition() + "\n" + msg;
        Log.d(TAG, s);
//        com.orhanobut.logger.Logger.json(s);
    }

    /**
     * 打印可以转换成json字符串的对象
     *
     * @param str
     * @param object
     */
    public static void object(String str, Object object) {
        if (!isDebug) {
            return;
        }
        try {
            Logger.d(str + object);
        } catch (Exception e) {
        }
    }
}
