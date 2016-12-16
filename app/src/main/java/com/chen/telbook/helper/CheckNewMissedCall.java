package com.chen.telbook.helper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.chen.libchen.Logger;
import com.chen.telbook.MyApplication;

/**
 * Created by ChenHui on 2016/12/16.
 */

public class CheckNewMissedCall {
    private static final String lastReadMissCallTime = "lastReadMissCallTime";

    public void setLastReadMissCallTime() {
        long currentTime = System.currentTimeMillis();
        SharedPerferencesHelper.save(lastReadMissCallTime, currentTime);
    }


    /**
     * 是否有新的未接电话
     *
     * @return
     */
    public boolean hasNewMissCall() {
        long lastReadTime = SharedPerferencesHelper.readLong(lastReadMissCallTime);
        Logger.d("lastReadTime=" + lastReadTime);
        Context context = MyApplication.getContext();
        CheckNewMissedCall checkNewMissedCall = new CheckNewMissedCall();
        checkNewMissedCall.setLastReadMissCallTime();
        ContentResolver cr = context.getContentResolver();
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        long threeDayAgo = System.currentTimeMillis() - 3 * 24 * 3600 * 1000;
        threeDayAgo = Math.max(threeDayAgo, lastReadTime);
        String[] projection = new String[]{
                android.provider.CallLog.Calls.CACHED_FORMATTED_NUMBER,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.DURATION,
                "INCOMING_CALL_TIME",
                android.provider.CallLog.Calls.GEOCODED_LOCATION,
                "yulore_page_tag"};
        if (ActivityCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
//            ToastUtil.show("没有读取通话记录的权限");
            return false;
        }
        String condition = android.provider.CallLog.Calls.DATE + " >? and " + android.provider.CallLog.Calls.TYPE + "=? ";
        Cursor cursor = cr.query(uri, projection, condition, new String[]{"" + threeDayAgo, "3"}, android.provider.CallLog.Calls.DATE + " desc limit 200 ");

        boolean hasNewMissCall = cursor.getCount() > 0;
        Logger.d("查询结果：" + cursor.getCount());

        cursor.close();
        return hasNewMissCall;
    }
}
