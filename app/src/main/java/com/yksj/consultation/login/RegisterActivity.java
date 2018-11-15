package com.yksj.consultation.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.ResourceHelper;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.setting.SettingWebUIActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnTextChanged;

/**
 * 新六一健康注册界面
 * Created by lmk on 2015/9/15.
 */
public class RegisterActivity extends BaseTitleActivity implements View.OnClickListener {

    private EditText editPhone, editPsw, editVerifyCode;
    private Button btnVerifyCode, btnComplete;
    private CheckBox checkBox;
    private TextView tvProtocol;
    private Runnable runnable;
    private boolean Sendcode = false;//验证码是否发送
    Handler handler = new Handler();
    private String registerType;
    private Bundle bundle;

    @Override
    public int createLayoutRes() {
        return R.layout.aty_register;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle(R.string.register);
        initView();
    }

    private void initView() {
        if (getIntent().hasExtra("registerType")) {
            registerType = getIntent().getStringExtra("registerType");
        }
        bundle = getIntent().getExtras();
        editPhone = (EditText) findViewById(R.id.register_input_phone);
        editPsw = (EditText) findViewById(R.id.register_input_psw);
        editVerifyCode = (EditText) findViewById(R.id.register_input_verifycode);
        btnComplete = (Button) findViewById(R.id.register_btn_complete);
        btnVerifyCode = (Button) findViewById(R.id.register_btn_verifycode);
        checkBox = (CheckBox) findViewById(R.id.register_checkbox);
        btnComplete.setOnClickListener(this);
        btnVerifyCode.setOnClickListener(this);
        findViewById(R.id.register_ptotocol).setOnClickListener(this);
    }

    @OnTextChanged(value = {R.id.register_input_phone, R.id.register_input_psw, R.id.register_input_verifycode}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChange(CharSequence s, int start, int before, int count) {
        String phone = editPhone.getText().toString().trim();
        String psw = editPsw.getText().toString().trim();
        String code = editVerifyCode.getText().toString().trim();
        boolean enable = RegexUtils.isMobileSimple(phone) && !TextUtils.isEmpty(psw) && !TextUtils.isEmpty(code);
        btnComplete.setEnabled(enable);
    }

    @OnTextChanged(value = {R.id.register_input_phone, R.id.register_input_psw}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onPhoneAndPsw(CharSequence s, int start, int before, int count) {
        final String phone = editPhone.getText().toString().trim();
        final String psw = editPsw.getText().toString().trim();
        boolean enable = RegexUtils.isMobileSimple(phone) && !TextUtils.isEmpty(psw);
        btnVerifyCode.setEnabled(enable);
    }

    @Override
    public void onClick(View view) {
        closeKeyboard();
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.register_btn_complete:
                requestRegiste();
                break;
            case R.id.register_btn_verifycode:
                String phone = editPhone.getText().toString().trim();
                sendProveCode(phone);
                break;
            case R.id.register_ptotocol:
                Intent intent = new Intent(this, SettingWebUIActivity.class);
                intent.putExtra("url", ResourceHelper.getString(R.string.agent_path_2));
                intent.putExtra("title", "用户协议与隐私条款");
                startActivity(intent);
                break;
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(RegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 注册
     */
    private void requestRegiste() {
        if (checkProtocal()) return;
        final String phone = editPhone.getText().toString().trim();
        final String psw = editPsw.getText().toString().trim();
        String code = editVerifyCode.getText().toString().trim();
        ApiService.requestRegister(phone, code, new ApiCallbackWrapper<ResponseBean>() {
            @Override public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    Intent intent = RegisterDoctorActivity.getCallingIntent(RegisterActivity.this, phone, psw, registerType);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.showShort(response.message);
                }
            }
        });
    }

    /**
     * 验证协议
     * @return
     */
    private boolean checkProtocal() {
        if (!checkBox.isChecked()) {//未同意隐私条款
            ToastUtils.showShort("您必须同意隐私用户协议");
            editPhone.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * 发送验证码
     * @param phone
     */
    private void sendProveCode(String phone) {
        ApiService.sendProveCode(phone, AppContext.CLIENT_TYPE, new ApiCallbackWrapper<ResponseBean>() {
            @Override public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    ToastUtils.showShort(String.format("验证码已发送至%s", phone));
                    Sendcode = true;
                    timerTaskC();
                } else {
                    ToastUtils.showShort(response.message);
                }
            }
        });
    }

    /**
     * 设置六十秒
     */
    private void timerTaskC() {
        runnable = new Runnable() {
            int i = 60;

            @Override
            public void run() {
                if (i == 0) {
                    btnVerifyCode.setText("发送验证码");
                    btnVerifyCode.setEnabled(true);
                    Sendcode = false;
                    return;
                } else {
                    --i;
                    handler.postDelayed(this, 1000);
                    btnVerifyCode.setText(i + "");
                    btnVerifyCode.setEnabled(false);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    /**
     * 登录之后,会调用此方法
     * @param log
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyEvent log) {
        if (log.code == 12) {//绑定登录成功后
            finish();
        }
    }
}
