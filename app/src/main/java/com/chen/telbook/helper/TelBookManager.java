package com.chen.telbook.helper;

import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TelBookManager {
    private static TelBookManager instance = new TelBookManager();

    private List<TelNum> telList = new ArrayList<>();
    private Set<OnDataChange> listenerSet = new HashSet<>();

    private TelBookManager() {
        loadLocalData();
        loadRemoteData();
    }

    public static TelBookManager getInstance() {
        return instance;
    }

    /**
     * 加载本地数据
     */
    public void loadLocalData() {
        String key = SharedPerferencesHelper.getPhoneBookKey();
        String strBase64 = SharedPerferencesHelper.read(key);
        if (TextUtils.isEmpty(strBase64)) {
            List<TelNum> list = new ArrayList<>();
            if ("telbook".equals(Constants.USER_NAME) || "llx".equals(Constants.USER_NAME)) {
                TelNum telNum = new TelNum();
                telNum.setImg("");
                telNum.setName("陈辉");
                telNum.setTel("15659002326");
                list.add(telNum);
            }
            changeData(list, "空数据");
        } else {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            Logger.d("读取到的本地数据：" + strResult);
            try {
                List<TelNum> list = TelBookXmlHelper.parse(strResult);
                changeData(list, "本地数据");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void loadRemoteData() {
        String url = Constants.urlXml + "?t=" + System.currentTimeMillis();
        Logger.d("url=" + url);
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                Logger.d("   " + response);
                Logger.d("得到远程数据：" + response);
                String strBase64 = Base64.encodeToString(response.getBytes(), Base64.DEFAULT);
                try {
                    List<TelNum> list = TelBookXmlHelper.parse(response);
                    if (list != null && !list.isEmpty()) {
                        String key = SharedPerferencesHelper.getPhoneBookKey();
                        SharedPerferencesHelper.save(key, strBase64);
                        changeData(list, "远程数据");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        };
        BaseRequest.getInstance().get(url, null, callback);
    }

    /**
     * 数据变化
     *
     * @param list
     */
    private void changeData(List<TelNum> list, String resson) {
        Logger.d("更新数据，人数" + list.size() + " reason=" + resson);
        telList.clear();
        telList.addAll(list);
        notifyDateChanged();
    }

    private void notifyDateChanged() {
        for (OnDataChange listener : listenerSet) {
            try {
                listener.onChange();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addListener(OnDataChange listener) {
        listenerSet.add(listener);
    }

    public void removeListener(OnDataChange listener) {
        listenerSet.remove(listener);
    }

    public void clearListener() {
        listenerSet.clear();
    }

    public List<TelNum> getList() {
        List<TelNum> list = new ArrayList<>();
        for (TelNum telNum : telList) {
            String telStr = JSON.toJSONString(telNum);
            list.add(JSON.parseObject(telStr, TelNum.class));
        }
        Logger.d("获取的人数：" + list.size());
        return list;
    }

    public void addTel(TelNum telNum) {
        Logger.d("要增加的人：" + JSON.toJSONString(telNum));
        telList.add(telNum);
        notifyDateChanged();
        saveCurrentData();
    }

    public void removeTel(TelNum telNum) {
        Logger.d("要删除的人：" + JSON.toJSONString(telNum));
        Logger.d("删除前人数1：" + telList.size());
        telList.remove(telNum);
        Logger.d("删除后人数1：" + telList.size());
        notifyDateChanged();
        saveCurrentData();
    }

    /**
     * 清除本地数据,重新加载
     */
    public void refreshAllData() {
        SharedPerferencesHelper.save(SharedPerferencesHelper.getPhoneBookKey(), "");
        telList.clear();
        notifyDateChanged();
        loadRemoteData();
    }

    /**
     * 保存数据到网络
     */
    private void saveCurrentData() {
        Logger.d("保存时候的人数" + telList.size());
        try {
            String xmlContent = TelBookXmlHelper.writeToString(telList);
            File file = File.createTempFile("xx" + System.currentTimeMillis(), "xml");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(xmlContent);
            bw.close();

            String key = Constants.USER_BOOK_FILE_NAME;
            String token = TokenHelper.getToken(key);
            ToastUtil.show("正在保存");
            NetCallback callback = new NetCallback() {
                @Override
                public void onResponse(String response) {
                    ToastUtil.show("保存成功！");
                }

                @Override
                public void onFailure(Exception e) {
                    ToastUtil.show("保存到网络失败！");
                }
            };
            uploadFile(key, token, file, callback);
        } catch (Exception e) {
            ToastUtil.show("保存失败！");
            Logger.e(e);
        }
    }

    private void uploadFile(String key, String token, File file, NetCallback callback) {
        String url = "http://upload.qiniu.com/";
        Map<String, Object> params = new Hashtable<>();
        params.put("key", key);
        params.put("token", token);
        params.put("file", file);
        new BaseRequest().postFile(url, params, callback);
    }

    public abstract static class OnDataChange {
        private String uuid;

        public OnDataChange() {
            uuid = UUID.randomUUID().toString();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OnDataChange) {
                OnDataChange onDataChange = (OnDataChange) obj;
                return TextUtils.equals(onDataChange.uuid, uuid);
            }
            return false;
        }

        public abstract void onChange();
    }
}
