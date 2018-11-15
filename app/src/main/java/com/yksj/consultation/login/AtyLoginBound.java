package com.yksj.consultation.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.NetworkUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.main.MainActivity;
import com.yksj.consultation.sonDoc.wallet.FindWithdrawPassword;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewFinder;
import com.yksj.healthtalk.utils.WeakHandler;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

;

public class AtyLoginBound extends BaseActivity implements View.OnClickListener {

    private EditText mPhone;
    private String phoneStr;
    private EditText mPwd;
    private Button btnLogin;
    Bundle bundle;
    String passwordMD5;
    AppContext mApplication;
    private WaitDialog mDialog;
    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1://登录超时
                    SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "登录超时,请稍后重试!", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                        @Override
                        public void onClickSureHander() {
                            LoginBusiness.getInstance().loginOut();//防止 时间到了,正好登上了,那就退出
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismissAllowingStateLoss();
                            }
                        }
                    });
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_login_bound);
        initView();
    }


    private void initView() {
        initializeTitle();
        titleLeftBtn.setImageResource(R.drawable.icon_cancel_delete);
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("绑定");
        if (getIntent().getExtras()!=null){
            bundle=this.getIntent().getExtras();
        }
        ViewFinder finder = new ViewFinder(this);
        finder.onClick(this, new int[]{R.id.login});
        findViewById(R.id.forget_pswd).setOnClickListener(this);
        btnLogin = finder.find(R.id.login);
        mPhone = finder.find(R.id.phone);
        mPwd = finder.find(R.id.pswd);
        mApplication = AppContext.getApplication();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.login:
                login();
                break;
            case R.id.forget_pswd:
                intent = new Intent(AtyLoginBound.this, FindWithdrawPassword.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        if (!NetworkUtils.isConnected()) {
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "网络不可用");
            return;
        }
        final String name = mPhone.getEditableText().toString();
        final String password = mPwd.getEditableText().toString();
        if (name.length() == 0) {
            mPhone.setError("手机号不能为空  ");
            return;
        }
        if (password.length() == 0) {
            mPwd.setError("密码不能为空");
            return;
        }
//        if (bundle!=null){
//            EventBus.getDefault().post(new String[]{name, password, bundle.getString("PLATFORM_NAME"), bundle.getString("USERID")});
//        }
        JSONObject object=new JSONObject();
        try {
            object.put("PLATFORM_NAME",bundle.getString("PLATFORM_NAME"));
            object.put("EXPIRESIN",String.valueOf(bundle.getLong("EXPIRESIN")));
            object.put("EXPIRESTIME",String.valueOf(bundle.getLong("EXPIRESTIME")));
            object.put("TOKEN",bundle.getString("TOKEN"));
            object.put("TOKENSECRET",bundle.getString("TOKENSECRET"));
            object.put("USERGENDER",bundle.getString("USERGENDER"));
            object.put("USERICON",bundle.getString("USERICON"));
            object.put("USERNAME",bundle.getString("USERNAME"));
            object.put("USERID",bundle.getString("USERID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("TYPE", "relatedOtherAccount"));
        params.add(new BasicNameValuePair("PHONENUM", name));
        params.add(new BasicNameValuePair("PASSWORD", password));
        params.add(new BasicNameValuePair("TERMINAL", "1"));
        params.add(new BasicNameValuePair("PARAM", object.toString()));
        ApiService.OKHttpConPhone(params, new MyApiCallback<String>(this) {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
            }

            @Override
            public void onAfter() {
                super.onAfter();
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("1".equals(obj.optString("code"))) {
                            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), obj.optString("result"), new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                                @Override
                                public void onClickSureHander() {
                                    mDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
                                    mHandler.sendEmptyMessageDelayed(1, SmartControlClient.CONNECTION_TIMEOUT);
                                    SharePreHelper.saveUserLoginCache(name, password, true);
                                    if (password.length() <= 16)
                                        passwordMD5 = MD5Utils.getMD5(password);
                                    SmartControlClient.getControlClient().setUserPassword(name, passwordMD5);
                                    EventBus.getDefault().post(new String[]{name, passwordMD5, "","","1"});
                                    btnLogin.setClickable(false);
                                }
                            });
                        }else {
                            btnLogin.setClickable(true);
                            SingleBtnFragmentDialog.show(getSupportFragmentManager(),"六一健康",obj.optString("message"),"知道了");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
            }
        }, this);
    }

    /**
     * 登录之后,会调用此方法
     *
     * @param log
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyEvent log) {
        mHandler.removeMessages(1);
        mDialog.dismissAllowingStateLoss();
        if (log.code == 1) {//登录成功
            ToastUtil.showShort("登录成功");
            Intent intent = new Intent(AtyLoginBound.this, MainActivity.class);
            startActivity(intent);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismissAllowingStateLoss();
                mDialog = null;
            }
            SharePreHelper.updateLoginState(true);
            EventBus.getDefault().post(new MyEvent("loginSuccess",12));
            finish();
        } else if (log.code == 0) {//登录失败
            btnLogin.setClickable(true);
            ToastUtil.showShort(log.what);
        } else if (log.code == 1111) {//第三方登录失败
            btnLogin.setClickable(true);
            ToastUtil.showShort(log.what);
        }
    }
}

