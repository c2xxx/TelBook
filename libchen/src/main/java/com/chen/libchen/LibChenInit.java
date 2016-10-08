package com.chen.libchen;

import android.content.Context;

/**
 * Created by hui on 2016/10/6.
 */

public class LibChenInit {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
