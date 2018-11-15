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
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.gridpasswordview.GridPasswordView;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 我的钱包-修改提现密码界面
 * Created by lmk on 15/9/17.
 */
public class WalletPswChangeActivity extends BaseActivity implements View.OnClickListener ,
        GridPasswordView.OnPasswordChangedListener{

    private GridPasswordView passwordView;
    private String password,oldPassword;
    private TextView tvTip;
    private boolean isChange=false;//是否是yao修改


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_setting_wallet_psw);

        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("修改提现密码");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText(R.string.forgot_password);
        titleRightBtn2.setOnClickListener(this);
        tvTip = (TextView) findViewById(R.id.setting_wallet_tip);
        tvTip.setText("请输入原提现密码");
        passwordView = (GridPasswordView) findViewById(R.id.setting_wallet_gpv);
        passwordView.setOnPasswordChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                Intent intent = new Intent(this, FindWithdrawPassword.class);
                startActivity(intent);
                break;

        }
    }

    //验证旧密码
    private void verifyPsw() {
        //SetWalletInfoServlet?TYPE=UPDATEPASS&CUSTOMERID=3783&OLDPASS=旧密码&PASS=提现密码
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("TYPE", "UPDATEPASS"));
        pairs.add(new BasicNameValuePair("OLDPASS", MD5Utils.getMD5(oldPassword)));
        pairs.add(new BasicNameValuePair("PASS", ""));//验证穿空
        ApiService.doGetSetWalletInfoServlet(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    Intent intent=new Intent(WalletPswChangeActivity.this,WalletPswSettingActivity.class);
                    startActivity(intent);
                } else {
                    oldPassword = null;
                    ToastUtil.showShort(WalletPswChangeActivity.this, bb.message);
                }
            }
        }, this);

    }

    @Override
    public void onChanged(String psw) {

    }

    @Override
    public void onMaxLength(String psw) {
        if (oldPassword == null) {
            oldPassword = passwordView.getPassWord();
            verifyPsw();
        }
    }
}
