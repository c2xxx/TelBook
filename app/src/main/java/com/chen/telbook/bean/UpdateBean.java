package com.chen.telbook.bean;

/**
 * Created by ChenHui on 2018/12/1.
 */
public class UpdateBean {

    /**
     * _describe : 这是用于app检查升级的文件，程序发布后，这个文件提交到github，应用可以检测到这个文件内容，然后根据提示进行升级
     * isUpdate : true
     * apkUrl : apk
     * versionName : V1.0.2
     * versionCode : 10
     * updateContent : 更新了xxxx
     */

    private boolean isUpdate;
    private String apkUrl;
    private String versionName;
    private int versionCode;
    private String updateContent;

    public boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }
}
