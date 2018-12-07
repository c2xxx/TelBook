package com.chen.telbook.helper;

import android.text.TextUtils;

public class QiNiuImageSize {
    public static String format(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl) && imgUrl.indexOf("?") == -1) {
            if (imgUrl.indexOf("clouddn.com") != -1 || imgUrl.indexOf("qiniu.happy1day.com") != -1) {
                imgUrl = imgUrl + "?imageView2/2/w/1000/h/1000/q/100";
            }
        }
        return imgUrl;
    }
}
