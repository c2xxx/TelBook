package com.chen.telbook.utils;

import com.chen.telbook.bean.AppConfigBean;

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

    private AppConfigBean config;

    public AppConfigBean getConfig() {
        if (config == null) {
            config=new AppConfigBean();
        }
        return config;
    }

    public void setConfig(AppConfigBean config) {
        this.config = config;
    }

    public AppConfig(){
        init();
    }
    private void init(){

    }

}
