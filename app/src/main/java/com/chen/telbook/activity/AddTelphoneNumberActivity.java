package com.chen.telbook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.TokenHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;
import com.chen.telbook.utils.ImageGlide;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISCameraConfig;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by hui on 2016/10/14.
 */

public class AddTelphoneNumberActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 选择号码
     */
    private static final int REQUEST_PICK_NUMBER = 1001;
    @Deprecated
    private static final int RESULT_LOAD_IMAGE = 1002;
    private static final int REQUEST_LIST_CODE = 1003;
    private static final int REQUEST_CAMERA_CODE = 1004;

    private ImageView iv_addnumber_selectpic;
    private EditText et_addnumber_phonenumber;
    private EditText et_addnumber_name;
    private String remoteImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_addphonenumber);
        super.onCreate(savedInstanceState);
        initViews();
        initImgLoader();
    }

    protected void initViews() {
//        setTitleText("添加号码");
        setTitle("添加联系人");
        findViewById(R.id.btn_addnumber_selectnum).setOnClickListener(this);
        findViewById(R.id.btn_addnumber_take_photo).setOnClickListener(this);
        findViewById(R.id.btn_addnumber_selectpic).setOnClickListener(this);
        findViewById(R.id.btn_addnumber_save).setOnClickListener(this);
        et_addnumber_phonenumber = (EditText) findViewById(R.id.et_addnumber_phonenumber);
        et_addnumber_name = (EditText) findViewById(R.id.et_addnumber_name);
        iv_addnumber_selectpic = (ImageView) findViewById(R.id.iv_addnumber_selectpic);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addnumber_selectnum:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_NUMBER);
                break;
            case R.id.btn_addnumber_selectpic:
                pickerImg();
//                //从相册选择照片，不用区分版本
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.btn_addnumber_take_photo:
                doTakePhoto();
                break;
            case R.id.btn_addnumber_save:
                doSave();
                break;
        }
    }

    private void doTakePhoto() {
        ISCameraConfig config = new ISCameraConfig.Builder()
                .needCrop(true) // 裁剪
                .cropSize(1, 1, 1000, 1000)
                .build();

        ISNav.getInstance().toCameraActivity(this, config, REQUEST_CAMERA_CODE);
    }


    private void initImgLoader() {
        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    private void pickerImg() {
        // 自由配置选项
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
//                .backResId(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                .backResId(R.mipmap.ic_launcher)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#3F51B5"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 1000, 1000)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(false)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();

        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, REQUEST_LIST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("onActivityResult===========================");
        if (requestCode == REQUEST_PICK_NUMBER && resultCode == RESULT_OK && null != data) {

            Uri contactData = data.getData();

            Cursor c = managedQuery(contactData, null, null, null, null);

            c.moveToFirst();
            paraserNumber(c);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().
                    query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String selectedPicturePath = cursor.getString(columnIndex);
            cursor.close();

            Logger.d("selectedImage=" + selectedImage);
            Logger.d("picturePath=" + selectedPicturePath);
            doUploadImage(selectedPicturePath);
        } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String path = data.getStringExtra("result"); // 图片地址
            Logger.d("选择的图片 " + path);
            doUploadImage(path);
        } else if (requestCode == REQUEST_LIST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> pathList = data.getStringArrayListExtra("result");
            if (pathList != null && pathList.size() > 0) {
                String path = pathList.get(0);
                Logger.d("选择的图片 " + path);
                doUploadImage(path);
            }
        }
    }


    /**
     * 解析选择返回的电话号码
     *
     * @param cursor
     */
    private void paraserNumber(Cursor cursor) {

        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String phoneResult = "";
        //System.out.print(phoneNum);
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null);
            //int phoneCount = phones.getCount();
            //allPhoneNum = new ArrayList<String>(phoneCount);
            if (phones.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !phones.isAfterLast(); phones.moveToNext()) {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int diaplayname = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phone_type = phones.getInt(typeindex);
                    String phoneNumber = phones.getString(index);
                    String diaplayname2 = phones.getString(diaplayname);
                    et_addnumber_phonenumber.setText(phoneNumber);
                    et_addnumber_name.setText(diaplayname2);
                    switch (phone_type) {
                        case 2:
                            phoneResult = phoneNumber;
                            break;
                    }
                    //allPhoneNum.add(phoneNumber);
                }
                if (!phones.isClosed()) {
                    phones.close();
                }
            }
        }
    }

    /**
     * @param imagePath 选择的图片路径
     */
    private void doUploadImage(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            ToastUtil.show("选择的图片不存在");
            return;
        }
        Logger.d(file.getPath());
        Logger.d("图片大小：" + file.length() / 1024 + " K " + file.getPath());

        String key = "pic_" + Constants.USER_NAME + "_" + System.currentTimeMillis() + getExtension(file.getName());
        String token = getToken(key);
        if (token == null) {
            ToastUtil.show("获取token失败");
            return;
        }
        uploadFile(key, token, file);
        ToastUtil.show("正在上传图片");
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.indexOf(".") == -1) {
            return "";
        }

        String prefix = fileName.substring(fileName.lastIndexOf("."));
        return prefix;
    }

    /**
     * 保存
     */
    private void doSave() {
        if (TextUtils.isEmpty(remoteImg)) {
            ToastUtil.show("选择照片并上传成功才能保存号码！");
            return;
        }
        String name = et_addnumber_name.getText().toString().trim();
        String number = et_addnumber_phonenumber.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show("请填写姓名！");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            ToastUtil.show("请填写号码！");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("tel", number);
        intent.putExtra("img", remoteImg);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * @param key 存放到空间的文件名
     * @return
     */
    public static String getToken(String key) {
        return TokenHelper.getToken(key);
    }

    /**
     * 上传图片
     *
     * @param key
     * @param token
     * @param file
     */
    private void uploadFile(final String key, String token, File file) {

        String url = "http://upload.qiniu.com/";
        Map<String, Object> params = new Hashtable<>();
        params.put("key", key);
        params.put("token", token);
        params.put("file", file);
        new BaseRequest().postFile(url, params, new NetCallback() {
            @Override
            public void onResponse(String response) {
                remoteImg = Constants.HOST + key;
                Logger.d(remoteImg);
                ImageGlide.show(AddTelphoneNumberActivity.this, remoteImg, iv_addnumber_selectpic);
                ToastUtil.show("图片上传成功！");
            }

            @Override
            public void onFailure(Exception e) {
                ToastUtil.show("上传图片失败，请检查网络！");
            }
        });
    }

}
