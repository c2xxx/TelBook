package com.chen.telbook.bean;

import android.text.TextUtils;

/**
 * Created by hui on 2016/2/22.
 */
public class TelNum {
    protected String name;
    protected String tel;
    protected String img;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TelNum) {
            TelNum telNum = (TelNum) obj;
            return TextUtils.equals(telNum.name, name)
                    && TextUtils.equals(telNum.tel, tel);
        }
        return false;
    }
}
