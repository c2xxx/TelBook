package com.chen.telbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.adapter.TelNumAdapter;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.SharedPerferencesHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TELPHONEBOOK = "TELPHONEBOOK";
    @BindView(R.id.rv_main)
    RecyclerView rvMain;

    private TelNumAdapter telAdapter;
    private List<TelNum> telList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        telAdapter = new TelNumAdapter(this, telList);

        telAdapter.setOnItemClick(new TelNumAdapter.OnItemClick() {
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

        loadLocolData();
        loadRemoteData();
    }

    private void loadRemoteData() {
        String url = Constants.urlXml;
        NetCallback callback = new NetCallback() {
            @Override
            public void onResponse(String response) {
                Logger.d("   " + response);
                String strBase64 = Base64.encodeToString(response.getBytes(), Base64.DEFAULT);
                try {
                    List<TelNum> list = parse(response);
                    if (list != null && !list.isEmpty()) {
                        SharedPerferencesHelper.save(TELPHONEBOOK, strBase64);
                        telAdapter.setData(list);
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

    private void doSelectedPosition(int position) {
        if (telList != null && telList.size() > position) {
            TelNum tel = telList.get(position);
            doCall(tel.getTel());
//            ToastUtil.show(tel.getName());
        }
    }

    private void doCall(String tel) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有拨号权限！", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }

    /**
     * 加载本地数据
     */
    private void loadLocolData() {
        String strBase64 = SharedPerferencesHelper.read(TELPHONEBOOK);
        if (TextUtils.isEmpty(strBase64)) {
            List<TelNum> list = new ArrayList<>();
            TelNum telNum = new TelNum();
            telNum.setImg("");
            telNum.setName("陈辉");
            telNum.setTel("15659002326");
            list.add(telNum);
            telAdapter.setData(list);
        } else {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            try {
                List<TelNum> list = parse(strResult);
                telAdapter.setData(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public List<TelNum> parse(String xmlString) throws Exception {
        List<TelNum> list = null;
        TelNum telNum = null;
        XmlPullParser parser = Xml.newPullParser();    //由android.util.Xml创建一个XmlPullParser实例
        ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(xmlString.getBytes());
        parser.setInput(tInputStringStream, "UTF-8");                //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
//                    LogController.d("START_DOCUMENT");
                    list = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
//                    LogController.d("START_TAG");
                    if (parser.getName().equals("tel")) {
                        telNum = new TelNum();
                    } else if (parser.getName().equals("name")) {
                        parser.next();
                        telNum.setName(parser.getText());
                    } else if (parser.getName().equals("phonenumber")) {
                        parser.next();
                        String phonenumber = parser.getText();
                        if (phonenumber != null && phonenumber.length() == 11) {
                            phonenumber = phonenumber.substring(0, 3) + " " + phonenumber.substring(3, 7) + " " + phonenumber.substring(7, 11);
                        }

                        telNum.setTel(phonenumber);
                    } else if (parser.getName().equals("photo")) {
                        parser.next();
                        telNum.setImg(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
//                    LogController.d("END_TAG");
                    if (parser.getName().equals("tel")) {
                        list.add(telNum);
                        telNum = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         *
         * add()方法的四个参数，依次是：
         *
         * 1、组别，如果不分组的话就写Menu.NONE,
         *
         * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单
         *
         * 3、顺序，那个菜单现在在前面由这个参数的大小决定
         *
         * 4、文本，菜单的显示文本
         */

        menu.add(Menu.NONE, Menu.FIRST + 1, 5, "添加");
        menu.add(Menu.NONE, Menu.FIRST + 2, 5, "删除");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case Menu.FIRST + 2:
                ToastUtil.show("删除");
                break;
        }
        return false;
    }
}
