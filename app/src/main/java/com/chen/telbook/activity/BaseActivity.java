package com.chen.telbook.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by hui on 2016/10/14.
 */

public class BaseActivity extends AppCompatActivity {
    int count = 0;

    @Override
    protected void onPause() {
        super.onPause();
        count--;
    }

    @Override
    protected void onResume() {
        super.onResume();
        count++;
    }

    protected boolean isForeground() {
        return count > 0;
    }
}
