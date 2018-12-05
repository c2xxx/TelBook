package com.chen.telbook.bean;

/**
 * Created by ChenHui on 2018/12/5.
 */
public class AppConfigBean {

    private boolean isCheckUpdateOnStart =false;

    public boolean isCheckUpdateOnStart() {
        return isCheckUpdateOnStart;
    }

    public void setCheckUpdateOnStart(boolean checkUpdateOnStart) {
        isCheckUpdateOnStart = checkUpdateOnStart;
    }
}
