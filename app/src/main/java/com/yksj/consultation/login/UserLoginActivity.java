package com.yksj.consultation.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.BeanCacheHelper;
import com.library.base.widget.SuperButton;
import com.yksj.consultation.bean.LoginBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.main.MainActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.wallet.FindWithdrawPassword;

import java.util.concurrent.TimeoutException;

import butterknife.OnClick;
import butterknife.OnTextChanged;

public class UserLoginActivity extends BaseTitleActivity implements OnClickListener {
    private EditText mPhoneView;
    private EditText mPwdView;
    private SuperButton mLoginView;
    private WaitDialog mWaitDialog;
    private String mAccount;
    private String mPassword;
    private LoginBusiness.SimpleLoginCallback mLoginCallback = new LoginBusiness.SimpleLoginCallback() {
        @Override
        public void onLoginStart() {
            showWait();
        }

        @Override
        public void onLoginSucees() {
            ToastUtils.showShort("登录成功");
            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onLoginError(Exception e) {
            if (e instanceof TimeoutException) {
                DialogManager.getConfrimDialog(e.getMessage())
                        .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                            @Override
                            public void onPositiveClick(ConfirmDialog dialog, View v) {
                                super.onPositiveClick(dialog, v);
                                LoginBusiness.getInstance().loginOut();//防止 时间到了,正好登上了,那就退出
                            }
                        })
                        .show(getSupportFragmentManager());
            } else {
                ToastUtils.showShort(e.getMessage());
            }
            hideWait();
        }

        @Override
        public void onLoginFinish() {
            hideWait();
        }
    };

    @Override
    public int createLayoutRes() {
        return R.layout.user_login_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("登录");
        setLeft(R.drawable.icon_cancel_delete, null);
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_weixin).setOnClickListener(this);
        findViewById(R.id.rl_qq).setOnClickListener(this);
        findViewById(R.id.rl_sina).setOnClickListener(this);
        mPhoneView = findViewById(R.id.phone);
        mPwdView = findViewById(R.id.pswd);
        mLoginView = findViewById(R.id.login);
        LoginBean loginBean = BeanCacheHelper.load(this, LoginBean.class);
        if (loginBean != null) {
            mPhoneView.setText(loginBean.getAccount());
            mPwdView.setText(loginBean.getPassword());
        }
    }

    private void showWait() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogManager.getWaitDialog("登陆中...");
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show(getSupportFragmentManager());
        }
    }

    private void hideWait() {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 忘记密码
     * @param v
     */
    @OnClick(R.id.forget_pswd)
    public void onForgetPaswdClick(View v) {
        Intent intent;
        intent = new Intent(UserLoginActivity.this, FindWithdrawPassword.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    /**
     * 注册
     * @param v
     */
    @OnClick(R.id.registe)
    public void onRegisterClick(View v) {
        Intent intent = new Intent(UserLoginActivity.this, RegisterActivity.class);
        intent.putExtra("registerType", "0");
        startActivity(intent);
    }

    /**
     * 登陆
     * @param v
     */
    @OnClick(R.id.login)
    public void onLoginClick(View v) {
        mAccount = mPhoneView.getEditableText().toString();
        mPassword = mPwdView.getEditableText().toString();
        if (mAccount.length() == 0) {
            mPhoneView.setError("账号不能为空");
            return;
        }
        if (mPassword.length() == 0) {
            mPwdView.setError("密码不能为空");
            return;
        }
        LoginBusiness.getInstance().login(mAccount, mPassword, "0", mLoginCallback);
    }

    @OnTextChanged(value = {R.id.phone, R.id.pswd}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChange(CharSequence s, int start, int before, int count){
        final String phone = mPhoneView.getText().toString().trim();
        final String psw = mPwdView.getText().toString().trim();
        boolean enable = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(psw);
        mLoginView.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        hideWait();
        mLoginCallback = null;
        super.onDestroy();
    }
}
