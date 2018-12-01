package com.chen.telbook.helper;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.chen.libchen.Logger;
import com.chen.telbook.BuildConfig;
import com.chen.telbook.bean.UpdateBean;
import com.chen.telbook.constants.Constants;
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
    public static void checkUpdate(Activity activity) {
        boolean isCheckUpdta = AppConfig.getInstance().isCheckUpdate();
        if (!isCheckUpdta) {
            return;
        }

        UpdateHelper updateHelper = new UpdateHelper();
        updateHelper.doCheckUpdate(activity, Constants.checkUpdata);
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
                        UpdateBean updateBean= JSON.parseObject(json,UpdateBean.class);
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        boolean isUpdate=updateBean.getIsUpdate();
                        if(isUpdate){
                            isUpdate=updateBean.getVersionCode()- BuildConfig.VERSION_CODE>0;
                        }
                        //（必须）是否更新Yes,No
                        updateAppBean.setUpdate(isUpdate ? "Yes" : "No")
                                //（必须）新版本号，
                                .setNewVersion(updateBean.getVersionName())
                                //（必须）下载地址
                                .setApkFileUrl(updateBean.getApkUrl())
                                //（必须）更新内容
                                .setUpdateLog(updateBean.getUpdateContent());
//                                //大小，不设置不显示大小，可以不设置
//                                .setTargetSize(fileSize)
//                                //是否强制更新，可以不设置
//                                .setConstraint(jsonObject.optBoolean("constraint"))
//                                //设置md5，可以不设置
//                                .setNewMd5(jsonObject.optString("new_md5"));
                        return updateAppBean;
                    }
                });
    }
}
