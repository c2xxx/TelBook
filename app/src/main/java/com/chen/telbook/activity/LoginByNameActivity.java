package com.chen.telbook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.chen.libchen.ToastUtil;
import com.chen.telbook.R;
import com.chen.telbook.constants.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ChenHui on 2016/12/13.
 */

public class LoginByNameActivity extends BaseActivity {
    @BindView(R.id.et_rename_name)
    EditText etRenameName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("用户登录");
        setContentView(R.layout.activity_rename);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        if (!Constants.isUserNameEmpty()) {
            etRenameName.setText(Constants.USER_NAME);
        }
    }

    /**
     * 提交
     */
    @OnClick(R.id.btn_submit)
    public void submit() {
        String userName = etRenameName.getText().toString().trim();
        if (checkUserNameOk(userName)) {
            Constants.setUserName(userName);
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean checkUserNameOk(String userName) {
        boolean isEmpty = TextUtils.isEmpty(userName);
        if (isEmpty) {
            ToastUtil.show("用户名不能为空");
            return false;
        }
        String regex = "([0-9]|[A-Za-z])+";
        boolean isZiMu = userName.matches(regex);
        if (!isZiMu) {
            ToastUtil.show("用户名不合法，仅支持三位以上的数字和字母");
            return false;
        }
        if (userName.length() < 3) {
            ToastUtil.show("用户名不合法，仅支持三位以上的数字和字母");
            return false;
        }
        return true;
    }
}
