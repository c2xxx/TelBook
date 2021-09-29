package com.chen.telbook.helper;

import android.text.TextUtils;

import com.chen.telbook.constants.Constants;

public class QiNiuImageSize {
    public static String format(String imgUrl) {
        return imgUrl.replace("./", Constants.HOST);
    }

    public static String formatXX(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.indexOf("?") == -1) {
            if (imgUrl.indexOf("clouddn.com") != -1 || imgUrl.indexOf("qiniu.happy1day.com") != -1) {
                imgUrl = imgUrl.trim() + "?imageView2/2/w/800/h/800/q/100";
            }
        }
        return imgUrl;
    }
}
