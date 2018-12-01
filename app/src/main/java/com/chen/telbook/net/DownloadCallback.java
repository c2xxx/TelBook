package com.chen.telbook.net;

import java.io.File;

/**
 * Created by hui on 2016/10/7.
 */
public abstract class DownloadCallback {
    public void onFailure(Exception e) {

    }

    /**
     * 下载进度100
     *
     * @param progress 100
     */
    public void progress(int progress) {

    }

    public abstract void onResponse(File file);
}
