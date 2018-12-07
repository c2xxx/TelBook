package com.chen.telbook.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.chen.libchen.Logger;
import com.chen.telbook.window.FloatWindow;

/**
 * Created by ChenHui on 2016/12/21.
 */

public class CallInReceiver extends BroadcastReceiver {
    private static final String TAG = "Broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("onReceive  action=" + action);
        //如果是来电
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                String incoming_number = intent.getStringExtra("incoming_number");
                Logger.d("CALL_STATE_RINGING :" + incoming_number);
                showNumber(incoming_number);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                String incoming_number1 = intent.getStringExtra("incoming_number");
                showNumber(incoming_number1);
                Logger.d("CALL_STATE_OFFHOOK :" + incoming_number1);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                String incoming_number2 = intent.getStringExtra("incoming_number");
                Logger.d("CALL_STATE_IDLE :" + incoming_number2);
                dismiss();
                break;
        }
    }

    private void showNumber(String number) {
//        ToastMessage.getInstance().showNumber(number);
        FloatWindow.showNumber(number);
    }

    private void dismiss() {
//        ToastMessage.getInstance().dismiss();
        FloatWindow.dismiss();
    }
}
