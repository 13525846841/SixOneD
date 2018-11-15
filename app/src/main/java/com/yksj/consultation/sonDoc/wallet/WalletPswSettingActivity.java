package com.yksj.consultation.sonDoc.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.gridpasswordview.GridPasswordView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 我的钱包-设置体现密码界面
 * Created by lmk on 15/9/17.
 */
public class WalletPswSettingActivity extends BaseActivity implements View.OnClickListener,
        GridPasswordView.OnPasswordChangedListener{

    private GridPasswordView passwordView,passwordView2;
    private String password,oldPassword;
    private TextView tvTip;
    private boolean isChange;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_setting_wallet_psw);
        if (getIntent().hasExtra("isChange")){
            isChange=true;
            oldPassword=getIntent().getStringExtra("oldPsw");
        }
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);

        titleTextV.setText("设置提现密码");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText(R.string.forgot_password);
        titleRightBtn2.setOnClickListener(this);
        tvTip= (TextView) findViewById(R.id.setting_wallet_tip);
        passwordView= (GridPasswordView) findViewById(R.id.setting_wallet_gpv);
        passwordView2= (GridPasswordView) findViewById(R.id.setting_wallet_gpv2);
        passwordView2.setOnPasswordChangedListener(this);
        passwordView.setOnPasswordChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                Intent intent=new Intent(this,FindWithdrawPassword.class);
                startActivity(intent);
                break;

        }
    }

    private void settingPsw(){
        //SetWalletInfoServlet?TYPE=SETPASS&CUSTOMERID=3783&PASS=提现密码
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("TYPE", "SETPASS"));
        pairs.add(new BasicNameValuePair("PASS", password));
        ApiService.doGetSetWalletInfoServlet(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    ToastUtil.showShort(WalletPswSettingActivity.this, bb.message);
                    onBackPressed();
                } else
                    ToastUtil.showShort(WalletPswSettingActivity.this, bb.message);
            }
        }, this);


    }

    @Override
    public void onChanged(String psw) {

    }

    @Override
    public void onMaxLength(String psw) {
        tvTip.setText("再次输入");
        if (password == null) {
            password = passwordView.getPassWord();
            passwordView.clearPassword();
        } else {
            if (password.equals(passwordView.getPassWord())) {
//                        ToastUtil.showShort(WalletPswSettingActivity.this,"密码保存成功,您的密码是"+password);
                settingPsw();

            } else {
                ToastUtil.showShort(WalletPswSettingActivity.this, "两次密码输入不一致" + password);
//                        passwordView.clearPassword();

            }
        }
    }
}
