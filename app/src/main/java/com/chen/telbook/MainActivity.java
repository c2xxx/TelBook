package com.chen.telbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "http://www.baidu.com";
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onFailure(Exception e) {
            }
        };
        BaseRequest.getInstance().get(url, null, callback);
    }
}
