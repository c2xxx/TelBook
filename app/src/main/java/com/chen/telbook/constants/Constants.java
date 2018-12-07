package com.chen.telbook.constants;

import android.text.TextUtils;

import com.chen.telbook.helper.SharedPerferencesHelper;

/**
 * Created by hui on 2016/10/8.
 */

public class Constants {
    public static final String spaceName = "telbook";
    public static final String accressKey = "VZtEbyKjgZSANKSfObSqXMeaRocby1zf5wseyF_V";
    public static final String secretKey = "_TaNRS6TEOhrSWV_tq00s1JJ_HlkhOfhB9gRb70Z";
    public static final String HOST = "http://telbook.qiniu.happy1day.com/";
    public static String USER_NAME = "";
    public static String USER_BOOK_FILE_NAME = "user_" + USER_NAME + ".xml";
    public static String urlXml = HOST + USER_BOOK_FILE_NAME;
    /**
     * 检查更新的地址
     */
    public static final String checkUpdata = "https://raw.githubusercontent.com/c2xxx/TelBook/develop/app/src/main/java/com/chen/telbook/UpdateConfig.json";

    public static void setUserName(String userName) {
        USER_NAME = userName;
        USER_BOOK_FILE_NAME = "user_" + USER_NAME + ".xml";
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
