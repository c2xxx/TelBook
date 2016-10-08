package com.chen.telbook.net;

import android.os.AsyncTask;
import android.util.Log;

import com.chen.libchen.Logger;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hui on 2016/10/7.
 */

public class BaseRequest {
    OkHttpClient mOkHttpClient = new OkHttpClient();
    private static BaseRequest instance;

    public BaseRequest() {
    }

    public static BaseRequest getInstance() {
        if (instance == null) {
            synchronized (BaseRequest.class) {
                if (instance == null) {
                    instance = new BaseRequest();
                }
            }
        }
        return instance;
    }

    public void get(String url, Map<String, String> params, final NetCallback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("onFailure");

                callBack(callback, call, null, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Logger.d("onResponse");
                callBack(callback, call, response, null);
            }
        });
    }

    public void post(String url, Map<String, String> params, final NetCallback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack(callback, call, null, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                callBack(callback, call, response, null);
            }
        });
    }

    private void callBack(final NetCallback callback, Call call, final Response response, final Exception e) {
        Logger.d("callBack");
        new AsyncTask<Call, Void, Void>() {

            @Override
            protected Void doInBackground(Call... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Logger.d("callBack1");
                if (response != null) {
                    Logger.d("callBack2");
                    try {
                        String str = response.body().string();
                        callback.onResponse(str);
                        Logger.d("callBack3");
                    } catch (IOException e1) {
                        Logger.d("callBack4");
                        callback.onFailure(e1);
                    }
                } else if (e != null) {
                    callback.onFailure(e);
                }
            }
        }.execute();
    }

    private void log(String a, long time1) {
        Log.d("wangshu", a + " " + (System.currentTimeMillis() - time1));
    }
}
