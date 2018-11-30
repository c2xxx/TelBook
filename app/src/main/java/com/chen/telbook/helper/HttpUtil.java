package com.chen.telbook.helper;

import android.support.annotation.NonNull;

import com.vector.update_app.HttpManager;

import java.util.Map;

/**
 * Created by ChenHui on 2018/12/1.
 */

public class HttpUtil implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull FileCallback callback) {

    }
}
