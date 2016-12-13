package com.chen.telbook.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chen.libchen.Logger;
import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.constants.Constants;
import com.chen.telbook.helper.TokenHelper;
import com.chen.telbook.net.BaseRequest;
import com.chen.telbook.net.NetCallback;
import com.chen.telbook.utils.ImageGlide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by hui on 2016/10/14.
 */

public class AddTelphoneNumberActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_PICK_NUMBER = 1001;
    private static final int RESULT_LOAD_IMAGE = 1002;

    private ImageView iv_addnumber_selectpic;
    private EditText et_addnumber_phonenumber;
    private EditText et_addnumber_name;
    private String remoteImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_addphonenumber);
        super.onCreate(savedInstanceState);
        initViews();
    }

    protected void initViews() {
//        setTitleText("添加号码");
        setTitle("添加联系人");
        findViewById(R.id.btn_addnumber_selectnum).setOnClickListener(this);
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
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_NUMBER);
                break;
            case R.id.btn_addnumber_selectpic:
                //从相册选择照片，不用区分版本
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.btn_addnumber_save:
                doSave();
                break;
        }
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
        Logger.d("压缩前图片大小：" + file.length() / 1024 + " K " + file.getPath());

        final String targetName = "mini_" + file.getName();
        //获取缩略图后上传
        Glide.with(this).load(file)
                .asBitmap()
                .thumbnail(0.9f)//只看缩略图
                .into(new SimpleTarget<Bitmap>(500, 500) {

                    /**
                     * 第一次完成，是缩略图，第二次原图，只保存第一次就可以了
                     */
                    boolean hasSave = false;

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (!hasSave) {
                            hasSave = true;
                            File mini = saveBitmap(resource, targetName, Bitmap.CompressFormat.JPEG, 90);
                            Logger.d(mini.getPath());
                            Logger.d("压缩后图片大小：" + mini.length() / 1024 + " K " + mini.getPath());

                            String key = "pic_" + System.currentTimeMillis() + getExtension(targetName);
                            String token = getToken(key);
                            if (token == null) {
                                ToastUtil.show("获取token失败");
                                return;
                            }
                            uploadFile(key, token, mini);
                            ToastUtil.show("正在上传图片");
                        }
                    }
                });


//        String key = "pic_" + System.currentTimeMillis() + getExtension(imagePath);
//        String token = getToken(key);
//        if (token == null) {
//            ToastUtil.show("获取token失败");
//            return;
//        }
//        uploadFile(key, token, file);
//        ToastUtil.show("正在上传图片");
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * 将Bitmap以指定格式保存到指定路径
     *
     * @param bitmap
     * @param name
     * @param format
     */
    public File saveBitmap(Bitmap bitmap, String name, Bitmap.CompressFormat format, int quality) {
        // 创建一个位于SD卡上的文件
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "012", name);
        if (!file.getParentFile().exists()) {
            Logger.d("创建文件" + file.getParentFile());
            file.getParentFile().mkdirs();
        }
        FileOutputStream out = null;
        try {
            // 打开指定文件输出流
            out = new FileOutputStream(file);
            // 将位图输出到指定文件
            bitmap.compress(format, quality, out);
            out.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
            }

            @Override
            public void onFailure(Exception e) {
                ToastUtil.show("上传图片失败，请检查网络！");
            }
        });
    }

}
