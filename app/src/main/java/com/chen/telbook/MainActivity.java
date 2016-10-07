package com.chen.telbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.iv_test)
    ImageView ivTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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

        loadImg();
    }

    private void loadImg() {
        Glide.with(this).load("http://7xsiih.com2.z0.glb.clouddn.com/pic_1474718315251.jpg?imageView2/2/w/300/h/300/q/100").into(ivTest);
//        Glide.with(this).load("http://7xsiih.com2.z0.glb.clouddn.com/pic_1474718315251.jpg").into(ivTest);
    }
}
