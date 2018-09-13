package com.chen.telbook.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by ChenHui on 2018/9/13.
 */
public class PermissionHelper {
    public static boolean check(Activity activity, String permission) {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            boolean needRequest = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (needRequest) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
            }
            return false;
        } else {
            return true;
        }
    }

    public void requestPermissions(Activity activity, String[] permissions) {
        //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
        ActivityCompat.requestPermissions(activity, permissions, 1);
    }
}
