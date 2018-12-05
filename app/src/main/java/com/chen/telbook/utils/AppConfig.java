package com.chen.telbook.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.chen.telbook.bean.AppConfigBean;
import com.chen.telbook.helper.SharedPerferencesHelper;


/**
 * Created by ChenHui on 2018/12/1.
 */

public class AppConfig {

    private static final String KEY = "AppConfig_Setting";
    private static AppConfig instance;

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private AppConfigBean config;

    public AppConfigBean getConfig() {
        if (config == null) {
            config = new AppConfigBean();
        }
        return config;
    }

    public void setConfig(AppConfigBean config) {
        this.config = config;
    }

    public AppConfig() {
        init();
    }

    private void save(AppConfigBean config) {
        SharedPerferencesHelper.save(KEY, JSON.toJSONString(config));
    }

    private void init() {
        String content = SharedPerferencesHelper.read(KEY);
        if (!TextUtils.isEmpty(content)) {
            try {
                config = JSON.parseObject(content, AppConfigBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
