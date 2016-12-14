package com.chen.telbook.bean;

import android.text.TextUtils;

/**
 * Created by ChenHui on 2016/12/14.
 */

public class CallLog extends TelNum {
    private int type;
    private long date;
    private int during;//时长
    private int ringTimes;//响铃次数
    private String geocodedLocation;//号码位置

    public CallLog() {
    }

    public CallLog(String name, String tel, long date, int type, int during, int ringTimes, String geocodedLocation) {
        this.name = name;
        this.tel = tel;
        this.date = date;
        this.type = type;
        this.during = during;
        this.ringTimes = ringTimes;
        this.geocodedLocation = geocodedLocation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuring() {
        return during;
    }

    public String getDuringString() {
        if (during == 0) {
            return "未接通";
        }
        int hour = during / 3600;
        int minute = (during % 3600) / 60;
        int seconds = during % 60;
        if (hour > 0) {
            return hour + "小时 " + minute + "分 " + seconds + "秒";
        } else if (minute > 0) {
            return minute + "分 " + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }

    public void setDuring(int during) {
        this.during = during;
    }

    public int getRingTimes() {
        return ringTimes;
    }

    public void setRingTimes(int ringTimes) {
        this.ringTimes = ringTimes;
    }

    public String getGeocodedLocation() {
        return geocodedLocation;
    }

    public String getGeocodedLocationString() {
        if (TextUtils.isEmpty(geocodedLocation)) {
            return null;
        }
        if (geocodedLocation.indexOf("广东") != -1) {
            return "广东";
        } else if (geocodedLocation.indexOf("福建") != -1) {
            return "福建";
        } else {
            return "外地";
        }
//        return geocodedLocation;
    }

    public void setGeocodedLocation(String geocodedLocation) {
        this.geocodedLocation = geocodedLocation;
    }
}
