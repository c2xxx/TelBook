package com.chen.telbook.utils;

/**
 * Created by ChenHui on 2018/12/1.
 */

public class AppConfig {

    private static   AppConfig instance;

    public static AppConfig getInstance() {
        if (instance == null) {
            instance=new AppConfig();
        }
        return instance;
    }

    public AppConfig(){
        init();
    }
    private void init(){

    }


    private boolean isCheckUpdate=true;

    public boolean isCheckUpdate() {
        return isCheckUpdate;
    }

    public void setCheckUpdate(boolean checkUpdate) {
        isCheckUpdate = checkUpdate;
    }
}
