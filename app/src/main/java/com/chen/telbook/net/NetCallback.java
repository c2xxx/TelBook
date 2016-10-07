package com.chen.telbook.net;

/**
 * Created by hui on 2016/10/7.
 */
public abstract class NetCallback {
    public void onFailure(Exception e) {

    }

    public abstract void onResponse(String response);
}
