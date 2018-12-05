package com.chen.telbook.helper;

import android.support.annotation.NonNull;

import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.DownloadCallback;
import com.chen.telbook.net.NetCallback;
import com.vector.update_app.HttpManager;

import java.io.File;
import java.util.Map;

/**
 * Created by ChenHui on 2018/12/1.
 */

public class HttpUtil implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                callBack.onResponse(response);
            }

            @Override
            public void onFailure(Exception e) {
                callBack.onError(e.getMessage());
            }
        };
        BaseRequest.getInstance().get(url, params, callback);
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                callBack.onResponse(response);
            }

            @Override
            public void onFailure(Exception e) {
                callBack.onError(e.getMessage());
            }
        };
        BaseRequest.getInstance().post(url, params, callback);
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        DownloadCallback fileCallback = new DownloadCallback() {

            @Override
            public void onResponse(File file) {
                callback.onResponse(file);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onError(e.getMessage());
            }


            @Override
            public void progress(int progress) {
                callback.onProgress(progress, 100);
            }
        };
        try {
            BaseRequest.getInstance().downLoad(url, path, fileName, fileCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
