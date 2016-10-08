package com.chen.libchen;

import android.widget.Toast;

/**
 * Created by hui on 2016/10/6.
 */

public class ToastUtil {
    private static Toast toast;

    public static void show(String msg) {
        if (toast == null) {
            toast = Toast.makeText(LibChenInit.getContext(), msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
