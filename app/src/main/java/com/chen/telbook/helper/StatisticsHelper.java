package com.chen.telbook.helper;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 统计事件
 */
public class StatisticsHelper {
    public void publishEvent(Context context, String event) {
        MobclickAgent.onEvent(context, event);
    }

    public void publishEvent(Context context, String event, Map<String, String> params) {
        MobclickAgent.onEvent(context, event, params);
    }
}
