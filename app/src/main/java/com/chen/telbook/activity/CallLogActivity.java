package com.chen.telbook.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.CallLogAdapter;
import com.chen.telbook.bean.CallLog;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.helper.TelBookXmlHelper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ChenHui on 2016/12/14.
 */

public class CallLogActivity extends BaseActivity {
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    private CallLogAdapter telAdapter;
    private List<CallLog> telList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        ButterKnife.bind(this);
        initData();
        setTitle("通话记录");
    }

    private void initData() {
        telAdapter = new CallLogAdapter(this, telList);

        telAdapter.setOnItemClick(new CallLogAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                doSelectedPosition(position);
            }
        });
        //设置布局管理器
        rvMain.setLayoutManager(new LinearLayoutManager(this));
//设置adapter
        rvMain.setAdapter(telAdapter);
//设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());

        loadCallLog();
    }

    /**
     * 读取未接电话
     */
    private void loadCallLog() {
        telList.addAll(getCallLog(this));
        telAdapter.notifyDataSetChanged();
    }

    private void doSelectedPosition(int position) {

    }

//    Uri uri = Calls.CONTENT_URI;
//    String[] projects = new String[] { Calls._ID, Calls.NEW, Calls.DATE };
//    String selections = Calls.NEW + " = ? AND " + Calls.TYPE + " = ? AND " + Calls.IS_READ + " = ? ";
//    String[] args = { "1", Integer.toString(Calls.MISSED_TYPE), Integer.toString(0) };
//    Cursor cursor = contentResolver.query(uri, projects, selections, args, null);

    /**
     * 获取所有的通话记录
     *
     * @param context
     * @return
     */
    public List<CallLog> getCallLogx(Context context) {
        List<CallLog> callLogs = new ArrayList();
        ContentResolver cr = context.getContentResolver();
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.show("没有读取通话记录的权限");
            return null;
        }
        Cursor cursor = cr.query(uri, null, null, null, android.provider.CallLog.Calls.DATE + " desc ");
        while (cursor.moveToNext()) {
            int count = cursor.getColumnCount();
            Logger.d("---------------------------------------" + count);
            for (int i = 0; i < count; i++) {
                String column = cursor.getColumnName(i);
                String text = cursor.getString(i);
                Logger.d(column + " = " + text);
            }
//            String number = cursor.getString(0);
//            long date = cursor.getLong(1);
//            int type = cursor.getInt(2);
//            String name = cursor.getString(3);
//            String DURATION = cursor.getString(4);
//            String NEW = cursor.getString(5);
//            String IS_READ = cursor.getString(6);
//            Logger.d(name + "  number=" + number + "  TYPE=" + type + "  DURATION= " + DURATION + "  NEW = " + NEW + "  IS_READ = " + IS_READ);
//            callLogs.add(new CallLog(name, number, date, type));
//            if (callLogs.size() >= 100) {//最多读取一百条数据
//                break;
//            }
        }
        cursor.close();
        return callLogs;
    }

    /**
     * 获取所有的通话记录
     *
     * @param context
     * @return
     */
    public List<CallLog> getCallLog(Context context) {
        Map<String, TelNum> map = loadLocolData();
        List<CallLog> callLogs = new ArrayList();
        ContentResolver cr = context.getContentResolver();
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                android.provider.CallLog.Calls.CACHED_FORMATTED_NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.DURATION,
                "INCOMING_CALL_TIME",
                android.provider.CallLog.Calls.GEOCODED_LOCATION,
                "yulore_page_tag"};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.show("没有读取通话记录的权限");
            return null;
        }
        Cursor cursor = cr.query(uri, projection, null, null, android.provider.CallLog.Calls.DATE + " desc ");
        long threeDayAgo = System.currentTimeMillis() - 3 * 24 * 3600 * 1000;
        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            long date = cursor.getLong(1);
            int type = cursor.getInt(2);
            String name = cursor.getString(3);
            int duration = cursor.getInt(4);
            int incomingCallTime = cursor.getInt(5);
            String geocodedLocation = cursor.getString(6);
            String yulore_page_tag = cursor.getString(7);
            if (!TextUtils.isEmpty(yulore_page_tag)) {
                if (yulore_page_tag.indexOf("疑似") != -1) {//跳过疑似诈骗
                    continue;
                }
            }
            Logger.d(name + "  number=" + number + "  TYPE=" + type + "  DURATION= " + duration + "  响铃次数 = " + incomingCallTime + " 地点：" + geocodedLocation);
            if (type == 3 && incomingCallTime < 3) {//小于3秒的视为骚扰电话
                continue;
            }
            if (date < threeDayAgo) {
                break;
            }
            CallLog callLog = new CallLog(name, number, date, type, duration, incomingCallTime, geocodedLocation);
            String number1 = number.replace("-", "").replace(" ", "");
            TelNum telNum = map.get(number1);
            Logger.d("telNum=" + telNum);
            if (telNum != null) {
                callLog.setName(telNum.getName());
                callLog.setImg(telNum.getImg());
            } else {
                callLog.setImg("file:///android_asset/img/moshengren.jpg");//默认头像
            }
            callLogs.add(callLog);
            if (callLogs.size() >= 100) {//最多读取一百条数据
                break;
            }
        }
        cursor.close();
        return callLogs;
    }

    /**
     * 加载本地数据
     */
    private Map<String, TelNum> loadLocolData() {
        Map<String, TelNum> hashMap = new Hashtable<>();
        String strBase64 = SharedPerferencesHelper.read(SharedPerferencesHelper.TEL_PHONE_BOOK);
        if (!TextUtils.isEmpty(strBase64)) {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            try {
                List<TelNum> list = TelBookXmlHelper.parse(strResult);
                for (TelNum telNum : list) {
                    hashMap.put(telNum.getTel().replace(" ", "").replace("-", ""), telNum);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.d("hashMap.size=" + hashMap.size());
        return hashMap;
    }
}
