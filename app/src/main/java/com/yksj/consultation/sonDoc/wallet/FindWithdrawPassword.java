package com.yksj.consultation.sonDoc.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.RegexUtils;
import com.yksj.consultation.app.AppContext;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.setting.SettingPassWordUI;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 找回提现密码
 * Created by lmk on 15/9/18.
 */
public class FindWithdrawPassword extends BaseTitleActivity implements View.OnClickListener {

    private String verify = "", phone;
    private EditText editPhone, editVerify;
    private Runnable runnable;
    private boolean Sendcode = false;//验证码是否发送
    Handler handler = new Handler();
    private Button btnVerifyCode;

    @Override
    public int createLayoutRes() {
        return R.layout.aty_find_withdraw_psw;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("找回密码");
        initView();
    }

    private void initView() {
        btnVerifyCode = (Button) findViewById(R.id.find_paswd_btn_verifycode);
        editPhone = (EditText) findViewById(R.id.find_paswd_phone);
        editVerify = (EditText) findViewById(R.id.find_paswd_input_verifycode);
        findViewById(R.id.find_paswd_btn_verifycode).setOnClickListener(this);
        findViewById(R.id.find_paswd_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.find_paswd_next:
                verify = editVerify.getText().toString().trim();
                if (!Sendcode) {
                    ToastUtil.showShort(this, "您还未获取验证码");
                    return;
                }
                if (TextUtils.isEmpty(verify)) {
                    ToastUtil.showShort(this, "请输入验证码");
                    return;
                }
                Intent intent = new Intent(FindWithdrawPassword.this, SettingPassWordUI.class);
                intent.putExtra("CODE", verify);
                intent.putExtra("PHONE", phone);
                startActivity(intent);
                break;
            case R.id.find_paswd_btn_verifycode:
                phone = editPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this, "请输入手机号码");
                    return;
                }
                if (!RegexUtils.isMobileExact(phone)) {
                    ToastUtil.showShort("请输入正确的手机号");
                    return;
                }
                getVerifyCode(phone);
                break;
        }
    }

    private void getVerifyCode(String phone) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "sendFindUpdatePasswordCode"));
        pairs.add(new BasicNameValuePair("PHONENUM", phone));
        pairs.add(new BasicNameValuePair("FLAG", "0"));//
        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (response == null || response.length() == 0)
                    return;
                try {
                    JSONObject object = new JSONObject(response);
                    if ("1".equals(object.optString("code"))) {
                        Sendcode = true;
                        timerTaskC();
                    }
                    ToastUtil.showShort(FindWithdrawPassword.this, object.optString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);

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

}
