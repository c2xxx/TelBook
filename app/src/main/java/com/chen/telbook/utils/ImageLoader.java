package com.chen.telbook.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by hui on 2016/10/8.
 */

public class ImageLoader {
    public static void show(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }
}
