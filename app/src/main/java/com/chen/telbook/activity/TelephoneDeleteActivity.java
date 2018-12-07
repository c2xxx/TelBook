package com.chen.telbook.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chen.telbook.R;
import com.chen.telbook.adapter.OnItemLongClick;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.helper.TelBookManager;

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

        telAdapter.setData(TelBookManager.getInstance().getList());
        TelBookManager.getInstance().addListener(new TelBookManager.OnDataChange() {
            @Override
            public void onChange() {
                telAdapter.setData(TelBookManager.getInstance().getList());
                telAdapter.notifyDataSetChanged();
            }
        });
    }
}
