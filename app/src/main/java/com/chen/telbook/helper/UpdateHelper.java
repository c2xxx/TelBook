package com.chen.telbook.helper;

import android.app.Activity;

import com.chen.libchen.Logger;
import com.chen.telbook.utils.AppConfig;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

/**
 * Created by ChenHui on 2018/12/1.
 */

public class UpdateHelper {
    /**
     * 检查升级
     */
    public static void checkUpdate(Activity activity, String url) {
        boolean isCheckUpdta = AppConfig.getInstance().isCheckUpdate();
        if (!isCheckUpdta) {
            return;
        }
        UpdateHelper updateHelper = new UpdateHelper();
        updateHelper.doCheckUpdate(activity, url);
    }

    private void doCheckUpdate(Activity activity, String checkUrl) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(activity)
                //更新地址
                .setUpdateUrl(checkUrl)
                //实现httpManager接口的对象
                .setHttpManager(new HttpUtil())
                .build()
                .checkNewApp(new UpdateCallback() {

                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        Logger.d("返回的内容：" + json);
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        return updateAppBean;
                    }
                });
    }
}
