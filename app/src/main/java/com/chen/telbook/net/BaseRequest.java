package com.chen.telbook.net;

import android.os.AsyncTask;
import android.util.Log;

import com.chen.libchen.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    public void postFile(String url, Map<String, Object> params, final NetCallback callback) {

    /* form的分割线,自己定义 */
        String boundary = "xx--------------------------------------------------------------xx";
        MultipartBody.Builder builder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);
            /* 上传一个普通的String参数 , key 叫 "p" */
//                .addFormDataPart("p", "你大爷666")
            /* 底下是上传了两个文件 */
//                .addFormDataPart("file", file1Name, fileBody1);
//                .addFormDataPart("file" , file2Name , fileBody2)

//        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                Object obj = params.get(key);
                if (obj instanceof String) {
                    builder.addFormDataPart(key, String.valueOf(params.get(key)));
                } else if (obj instanceof File) {
                    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), (File) obj);
                    builder.addFormDataPart("file", key, fileBody);
                }
//                builder.add(key, params.get(key));
            }
        }
//        MultipartBody mBody = xx.build();
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
            String responseStr = null;
            Exception ex = null;

            @Override
            protected Void doInBackground(Call... params) {
                Logger.d("callBack1");
                if (response != null) {
                    Logger.d("callBack2");
                    try {
                        String str = response.body().string();
                        responseStr = str;
//                        callback.onResponse(str);
                        Logger.d("callBack3");
                    } catch (IOException e1) {
                        Logger.d("callBack4");
//                        callback.onFailure(e1);
                        ex = e1;
                    }
                } else if (e != null) {
//                    callback.onFailure(e);
                    ex = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (ex != null) {
                    callback.onFailure(ex);
                } else if (responseStr != null) {
                    callback.onResponse(responseStr);
                }
//                Logger.d("callBack1");
//                if (response != null) {
//                    Logger.d("callBack2");
//                    try {
//                        String str = response.body().string();
//                        callback.onResponse(str);
//                        Logger.d("callBack3");
//                    } catch (IOException e1) {
//                        Logger.d("callBack4");
//                        callback.onFailure(e1);
//                    }
//                } else if (e != null) {
//                    callback.onFailure(e);
//                }
            }
        }.execute();
    }

    private void log(String a, long time1) {
        Log.d("wangshu", a + " " + (System.currentTimeMillis() - time1));
    }
}
