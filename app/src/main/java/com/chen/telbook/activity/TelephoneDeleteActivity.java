package com.chen.telbook.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.adapter.OnItemClick;
import com.chen.telbook.adapter.OnItemLongClick;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.helper.TelBookXmlHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hui on 2016/10/14.
 */

public class TelephoneDeleteActivity extends BaseActivity {

    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    private TelNumAdapter telAdapter;
    private List<TelNum> telList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    protected void initViews() {
        setTitle("删除联系人");
    }

    /**
     * @param telNum
     */
    private void doDelete(final TelNum telNum) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("删除对话框")
                .setCancelable(true)
                .setMessage("确认删除" + telNum.getName() + "吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("name", telNum.getName());
                        intent.putExtra("tel", telNum.getTel());
                        intent.putExtra("img", telNum.getImg());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
        dialog.show();
    }

    protected void initData() {
        telList = new ArrayList<>();
        telAdapter = new TelNumAdapter(this, telList);
        loadLocolData();
        loadRemoteXmlData();
        telAdapter = new TelNumAdapter(this, telList);

        telAdapter.setOnItemLongClick(new OnItemLongClick() {
            @Override
            public void onItemLongClick(int position) {
                TelNum telNum = telList.get(position);
                doDelete(telNum);
            }
        });
        //设置布局管理器
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        rvMain.setAdapter(telAdapter);
        //设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * 加载本地数据
     */
    private void loadLocolData() {
        String strBase64 = SharedPerferencesHelper.read(SharedPerferencesHelper.TEL_PHONE_BOOK);
        if (TextUtils.isEmpty(strBase64)) {
            List<TelNum> list = new ArrayList<>();
//            TelNum telNum = new TelNum();
//            telNum.setImg("");
//            telNum.setName("陈辉");
//            telNum.setTel("15659002326");
//            list.add(telNum);
            telAdapter.setData(list);
        } else {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            try {
                List<TelNum> list = TelBookXmlHelper.parse(strResult);
                telAdapter.setData(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载远程数据
     */
    private void loadRemoteXmlData() {
        String url = Constants.urlXml + "?t=" + System.currentTimeMillis();
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                Logger.d("   " + response);
                String strBase64 = Base64.encodeToString(response.getBytes(), Base64.DEFAULT);
                try {
                    List<TelNum> list = TelBookXmlHelper.parse(response);
                    if (list != null && !list.isEmpty()) {
                        SharedPerferencesHelper.save(SharedPerferencesHelper.TEL_PHONE_BOOK, strBase64);
                        telAdapter.setData(list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                ToastUtil.show("加载数据失败，请检查网络");
            }
        };
        BaseRequest.getInstance().get(url, null, callback);
    }
}
