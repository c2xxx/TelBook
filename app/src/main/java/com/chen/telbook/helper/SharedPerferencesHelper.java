package com.chen.telbook.helper;

/**
 * Created by hui on 2016/10/8.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.telbook.MyApplication;
import com.chen.telbook.constants.Constants;


/**
 * Created by hui on 2016/2/29.
 */
public class SharedPerferencesHelper {
    public static final String USER_NAME = "USER_NAME";
    private static final String TEL_PHONE_BOOK = "TEL_PHONE_BOOK";

    public static String getPhoneBookKey() {
        return TEL_PHONE_BOOK + Constants.USER_NAME;
    }

    private static SharedPreferences sp;

    public static void save(String key, String value) {
        sp = getSharedPreferences();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static void save(String key, long value) {
        sp = getSharedPreferences();
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(key, value);
        edit.commit();
    }

    public static String read(String key) {
        sp = getSharedPreferences();
        return sp.getString(key, "");
    }

    public static long readLong(String key) {
        sp = getSharedPreferences();
        return sp.getLong(key, 0);
    }

    private static SharedPreferences getSharedPreferences() {
        return MyApplication.getContext().getSharedPreferences("basedemo", Context.MODE_PRIVATE);
    }

}
