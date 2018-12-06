package com.yksj.consultation.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

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
import com.yksj.consultation.widget.ClearEditView;

import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;

public class UserLoginActivity extends BaseTitleActivity implements OnClickListener {
    @BindView(R.id.account_cev) ClearEditView mPhoneView;
    @BindView(R.id.password_cev) ClearEditView mPwdView;
    @BindView(R.id.login) SuperButton mLoginView;
    private WaitDialog mWaitDialog;
    private String mAccount;
    private String mPassword;
    private LoginBusiness.SimpleLoginCallback mLoginCallback = new LoginBusiness.SimpleLoginCallback() {
        @Override
        public void onLoginStart() {
            showLoginWait();
        }

        @Override
        public void onLoginSucees() {
            ToastUtils.showShort("登录成功");
            startActivity(MainActivity.getCallingIntent(UserLoginActivity.this));
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
            hideLoginWait();
        }

        @Override
        public void onLoginFinish() {
            hideLoginWait();
        }
    };

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, UserLoginActivity.class);
        LoginBean loginBean = BeanCacheHelper.load(context, LoginBean.class);
        if (loginBean != null) {
            intent.putExtra("account", loginBean.getAccount());
            intent.putExtra("password", loginBean.getPassword());
        }
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.user_login_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("登录");
        setLeft(R.drawable.icon_cancel_delete);
        setRight("忘记密码?", this::onForgetPaswdClick);
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_weixin).setOnClickListener(this);
        findViewById(R.id.rl_qq).setOnClickListener(this);
        findViewById(R.id.rl_sina).setOnClickListener(this);
        mPhoneView.setOnEditTextChangeListener(this::onTextChange);
        mPwdView.setOnEditTextChangeListener(this::onTextChange);
        String account = getIntent().getStringExtra("account");
        String password = getIntent().getStringExtra("password");
        mPhoneView.setEditText(account);
        mPwdView.setEditText(password);
        boolean enable = !TextUtils.isEmpty(account) && !TextUtils.isEmpty(password);
        mLoginView.setEnabled(enable);
    }

    /**
     * 显示登陆等待
     */
    private void showLoginWait() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogManager.getWaitDialog("登陆中...");
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show(getSupportFragmentManager());
        }
    }

    /**
     * 隐藏登陆等待
     */
    private void hideLoginWait() {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 忘记密码
     * @param v
     */
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
        mAccount = mPhoneView.getEditText();
        mPassword = mPwdView.getEditText();
        LoginBusiness.getInstance().login(mAccount, mPassword, "0", mLoginCallback);
    }

    /**
     * 账号、密码输入变化
     * @param s
     * @param start
     * @param before
     * @param count
     */
    public void onTextChange(CharSequence s, int start, int before, int count) {
        final String phone = mPhoneView.getEditText().trim();
        final String psw = mPwdView.getEditText().trim();
        boolean enable = !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(psw);
        mLoginView.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        hideLoginWait();
        mLoginCallback = null;
        super.onDestroy();
    }
}
