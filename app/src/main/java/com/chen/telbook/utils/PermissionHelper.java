package com.chen.telbook.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.chen.libchen.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenHui on 2018/9/13.
 */
public class PermissionHelper {

    private static String[] permissions = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static String requestAll(Activity activity) {
        requestPermissions(activity, permissions);

        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String permission : permissions) {
            boolean has = check(activity, permission);
            if (!has) {
                sb.append(getPermissionName(permission) + "\n");
                list.add(permission);
            }
        }
        String error = sb.length() == 0 ? "" : "缺权限:\n" + sb.toString().trim();
        Logger.d(error);
        return error;
    }

    public static boolean checkAlertWindow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Toast.makeText(activity, "需要使用悬浮窗权限用于显示通话号码头像", Toast.LENGTH_SHORT).show();
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                activity.startActivity(intent);
                return false;
            }
        } else {
            return check(activity, Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
        return true;
    }

    public static String getPermissionName(String permission) {
        String name = null;
        switch (permission) {
            case Manifest.permission.CALL_PHONE:
                name = "拨打电话";
                break;
            case Manifest.permission.READ_CONTACTS:
                name = "拨打电话";
                break;
            case Manifest.permission.CAMERA:
                name = "照相";
                break;
            case Manifest.permission.READ_CALL_LOG:
                name = "读取通话记录";
                break;
            case Manifest.permission.SYSTEM_ALERT_WINDOW:
                name = "系统悬浮窗";
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                name = "定位";
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                name = "读写SD文件";
                break;
        }
        return name;
    }

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

    public static void requestPermissions(Activity activity, String[] permissions) {
        //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
        ActivityCompat.requestPermissions(activity, permissions, 1);
    }
}
