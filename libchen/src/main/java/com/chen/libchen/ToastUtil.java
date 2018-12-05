package com.chen.libchen;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {
    private static Toast toast;

    public static void show(String msg) {
        showXX(LibChenInit.getContext(), msg);
    }

    public static void show(Context context, String msg) {
        showXX(context, msg);
    }

    public static void showXX(Context context, String msg) {
        if (toast != null) {
            try {
                toast.cancel();
            } catch (Exception e) {
            }
        }
        if (context == null) {
            return;
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        TextView view = new TextView(context);
        view.setText(msg);
        view.setTextColor(Color.WHITE);
        view.setTextSize(14);
        view.setBackgroundResource(R.drawable.toast_bg);
        toast.setView(view);
        toast.show();
    }

    public static void show2(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            TextView view = new TextView(context);
            view.setText(msg);
            toast.setView(view);
        }
        toast.show();
    }
}
