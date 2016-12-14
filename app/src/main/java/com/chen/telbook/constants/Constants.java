package com.chen.telbook.constants;

import android.text.TextUtils;

import com.chen.telbook.helper.SharedPerferencesHelper;

/**
 * Created by hui on 2016/10/8.
 */

public class Constants {
    public static final String HOST = "http://7xsiih.com2.z0.glb.clouddn.com/";
    public static String USER_NAME = "";
    public static String USER_BOOK_FILE_NAME = USER_NAME + ".xml";
    public static String urlXml = HOST + USER_BOOK_FILE_NAME;

    public static void setUserName(String userName) {
        USER_NAME = userName;
        USER_BOOK_FILE_NAME = USER_NAME + ".xml";
        urlXml = HOST + USER_BOOK_FILE_NAME;
        SharedPerferencesHelper.save(SharedPerferencesHelper.USER_NAME, userName);
    }


    public static void initUserName() {
        String userName = SharedPerferencesHelper.read(SharedPerferencesHelper.USER_NAME);
        if (!TextUtils.isEmpty(userName)) {
            setUserName(userName);
        }
    }

    public static boolean isUserNameEmpty() {
        return "default".equalsIgnoreCase(USER_NAME) || TextUtils.isEmpty(USER_NAME);
    }
}
