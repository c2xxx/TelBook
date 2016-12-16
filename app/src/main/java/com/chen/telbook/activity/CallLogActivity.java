package com.chen.telbook.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.CallLogAdapter;
import com.chen.telbook.adapter.OnItemClick;
import com.chen.telbook.adapter.OnItemLongClick;
import com.chen.telbook.bean.CallLog;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.helper.CheckNewMissedCall;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.helper.XunFeiVoiceReadHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ChenHui on 2016/12/14.
 */

public class CallLogActivity extends BaseActivity {
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.iv_call_log)
    ImageView ivCallLog;
    private CallLogAdapter telAdapter;
    private List<CallLog> telList = new ArrayList<>();

    XunFeiVoiceReadHelper readHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        ButterKnife.bind(this);
        ivCallLog.getBackground().setAlpha(200);
        initData();
        setTitle("通话记录");
        readHelper = new XunFeiVoiceReadHelper(this);
        readHelper.readText(" ");//目的是初始化一次
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.iv_call_log)
    public void gotoActivityCallLog() {
        finish();
    }

    private void initData() {
        telAdapter = new CallLogAdapter(this, telList);

        telAdapter.setOnItemClick(new OnItemClick() {
            @Override
            public void onItemClick(int position) {
                shortClick(position);
            }

        });
        telAdapter.setOnItemLongClick(new OnItemLongClick() {
            @Override
            public void onItemLongClick(int position) {
                longClick(position);
            }
        });
        //设置布局管理器
        rvMain.setLayoutManager(new GridLayoutManager(this, 1));
//设置adapter
        rvMain.setAdapter(telAdapter);
//设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCallLog();
    }

    /**
     * 读取未接电话
     */
    private void loadCallLog() {
        telList.clear();
        telList.addAll(getCallLog(this));
        telAdapter.notifyDataSetChanged();
    }

    private void shortClick(int position) {
//        ToastUtil.show("短按");
        CallLog callLog = telList.get(position);
        StringBuilder sb = new StringBuilder();
        String dateStr = getDateString(callLog.getDate());
        sb.append(dateStr);
        String name = callLog.getName();
        if (TextUtils.isEmpty(name)) {
            name = "陌生人";
        }
//        String format1 = "昨天下午3点 未接来电 对方是XXX 响铃19声";
//        String format2 = "昨天下午3点 XXX来电 通话时长3分4秒";
//        String format3 = "昨天下午3点 打电话给XXX 通话时长3分4秒";
//        String format4 = "昨天下午3点 挂断了XXX的电话";
        switch (callLog.getType()) {
            case CallLog.CALL_IN_FAIL:
                sb.append(" 未接来电");
                sb.append(" 对方是" + name);
                sb.append(" 响铃" + callLog.getRingTimes() + "声");
                break;
            case CallLog.CALL_IN:
                sb.append(" " + name + " 来电 ");
                sb.append(" 通话时长" + callLog.getDuringString());
                break;
            case CallLog.CALL_OUT:
                sb.append(" 打电话给" + name);
                if ("未接通".equals(callLog.getDuringString())) {
                    sb.append(" 没有接通");
                } else {
                    sb.append(" 通话时长" + callLog.getDuringString());
                }
                break;
            case CallLog.CALL_REFUSE:
                sb.append(" 挂断了" + name + "的电话");
                break;
        }
        Logger.d("播报>>>" + sb.toString());
        readHelper.readText(sb.toString()+"。");
    }

    /**
     * 获取时间读音
     *
     * @param date
     * @return
     */
    private String getDateString(long date) {
        SimpleDateFormat sdf_day = new SimpleDateFormat("MM月dd日");
        String today = sdf_day.format(System.currentTimeMillis());
        String yesterday = sdf_day.format(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        String theDayBeforeYesterday = sdf_day.format(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 ahh:mm");
        String dateStr = sdf.format(date);
        if (dateStr.indexOf(today) != -1) {
            dateStr = dateStr.replace(today, "今天");
        } else if (dateStr.indexOf(yesterday) != -1) {
            dateStr = dateStr.replace(yesterday, "昨天");
        } else if (dateStr.indexOf(theDayBeforeYesterday) != -1) {
            dateStr = dateStr.replace(theDayBeforeYesterday, "前天");
        }
        return dateStr;
    }

    private void longClick(int position) {
//        ToastUtil.show("长按");
        CallLog callLog = telList.get(position);
        doCall(callLog.getTel().replace(" ", "").replace("-", ""));
    }

    /**
     * 拨打电话
     *
     * @param tel
     */
    private void doCall(String tel) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有拨号权限！", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        readHelper.stop();
    }

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
        CheckNewMissedCall checkNewMissedCall=new CheckNewMissedCall();
        checkNewMissedCall.setLastReadMissCallTime();
        Map<String, TelNum> map = loadLocolData();
        List<CallLog> callLogs = new ArrayList();
        ContentResolver cr = context.getContentResolver();
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                android.provider.CallLog.Calls.CACHED_FORMATTED_NUMBER,
                android.provider.CallLog.Calls.NUMBER,
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
        Cursor cursor = cr.query(uri, projection, null, null, android.provider.CallLog.Calls.DATE + " desc limit 200 ");
        long threeDayAgo = System.currentTimeMillis() - 3 * 24 * 3600 * 1000;
        int index = 0;
        while (cursor.moveToNext()) {
            String number_format = cursor.getString(0);
            String number_unformat = cursor.getString(1);
            long date = cursor.getLong(2);
            int type = cursor.getInt(3);
            String name = cursor.getString(4);
            int duration = cursor.getInt(5);
            int incomingCallTime = cursor.getInt(6);
            String geocodedLocation = cursor.getString(7);
            String yulore_page_tag = cursor.getString(8);
//            Logger.d("index====" + index++);
            if (!TextUtils.isEmpty(yulore_page_tag)) {
                if (yulore_page_tag.indexOf("疑似") != -1) {//跳过疑似诈骗
                    continue;
                }
            }
            String number = TextUtils.isEmpty(number_format) ? number_unformat : number_format;
//            Logger.d(name + "  number=" + number + "  TYPE=" + type + "  DURATION= " + duration + "  响铃次数 = " + incomingCallTime + " 地点：" + geocodedLocation);
            if (type == 3 && incomingCallTime < 3) {//小于3秒的视为骚扰电话
                continue;
            }
            if (date < threeDayAgo) {
                break;
            }
            if (TextUtils.isEmpty(number)) {
                continue;
            }
            CallLog callLog = new CallLog(name, number, date, type, duration, incomingCallTime, geocodedLocation);
            String number1 = number.replace("-", "").replace(" ", "");
            TelNum telNum = map.get(number1);
//            Logger.d("telNum=" + telNum);
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
