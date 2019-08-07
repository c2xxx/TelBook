package com.chen.telbook.activity;

import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by hui on 2016/10/14.
 */

public class BaseActivity extends AppCompatActivity {
    int count = 0;

    @Override
    protected void onPause() {
        super.onPause();
        count--;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        count++;
    }


    protected boolean isForeground() {
        return count > 0;
    }
}
