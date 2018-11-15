package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.AccountList;
import com.yksj.consultation.sonDoc.consultation.main.GetMoney;
import com.yksj.consultation.sonDoc.consultation.main.RechargeActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HEKL on 15/12/21.
 */

/**
 * 修改与16／11／7
 */
public class AtyAccountInfo extends BaseTitleActivity implements View.OnClickListener {

    private EditText mZFBNUMS, mZFBName, mBankNUMs, mBankName, mBankAccName, mPhone;
    private TextView number;
    private RelativeLayout rl_chongzhi;
    private RelativeLayout rl_tixian;
    private TextView money_income;//提现

    private String money;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, AtyAccountInfo.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_accountinfo;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("账户管理");
        setRight("账单明细", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AtyAccountInfo.this, AccountList.class);
                startActivity(intent);
            }
        });

        number = (TextView) findViewById(R.id.tv_number);
        rl_chongzhi = (RelativeLayout) findViewById(R.id.rl_chongzhi);
        rl_tixian = (RelativeLayout) findViewById(R.id.rl_tixian);
        money_income = (TextView) findViewById(R.id.income_money);

        rl_chongzhi.setOnClickListener(this);
        rl_tixian.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData2();
    }

    private void initData3() {
       Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());//DoctorHelper.getId()
        ApiService.OKHttpGetMoney(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("1".equals(jsonObject.optString("code"))) {
                            JSONObject obj = jsonObject.getJSONObject("result");
                            money = obj.optString("BALANCE_AFTER");
                            //  money_income.setText("其中"+money+"元可以结算，其余会诊结束1天后可结算，将计入下月结算。");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    private void initData2() {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());//customer_id／／116305
        ApiService.OKHttpACCOUNTBALANCE(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String content) {
                super.onResponse(content);
                try {
                    JSONObject object = new JSONObject(content);
                    money = object.optString("balance");
                    //  money_income.setText("其中"+money+"元可以结算，其余会诊结束1天后可结算，将计入下月结算。");
                    object.optString("balance");
                    number.setText(object.optString("balance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.title_right2://账单明细
                intent = new Intent(this, AccountList.class);
                startActivity(intent);
                break;
            case R.id.rl_chongzhi://充值
//              intent = new Intent(this,TopUpActivity.class);
                intent = new Intent(this, RechargeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_tixian://提现
                intent = new Intent(this, GetMoney.class);
                intent.putExtra("money", money);
                startActivity(intent);
                break;
        }
    }
    /**
     * 加载账户信息
     */
//    private void initData() {
//        List<BasicNameValuePair> valuePairs = new ArrayList<>();
//        valuePairs.add(new BasicNameValuePair("OPTION", "17"));
//        valuePairs.add(new BasicNameValuePair("CUSTID", LoginServiceManeger.getInstance().getLoginEntity().getId()));
//        ApiService.OKHttpgetAccountInfo(valuePairs, new MyApiCallback<String>(this) {
//            @Override
//            public void onResponse(String response) {
//                super.onResponse(response);
//                if (!TextUtils.isEmpty(response)) {
//                    try {
//                        JSONObject obj = new JSONObject(response);
//                        if (1 == obj.optInt("code")) {
//                            mZFBName.setText(obj.getJSONObject("result").optString("ALI_PAY_NAME"));
//                            mZFBNUMS.setText(obj.getJSONObject("result").optString("ALI_PAY_ACCOUNT"));
//                            mBankName.setText(obj.getJSONObject("result").optString("ACCOUNT_BANK"));
//                            mBankNUMs.setText(obj.getJSONObject("result").optString("UNIONPAY_ACCOUNT"));
//                            mBankAccName.setText(obj.getJSONObject("result").optString("UNIONPAY_NAME"));
//                            mPhone.setText(obj.getJSONObject("result").optString("TELPHONE"));
//                        } else {
//                            ToastUtil.showShort(obj.optString("message"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Request request, Exception e) {
//                super.onError(request, e);
//            }
//        }, this);
//    }

    /**
     * 提交账户修改
     */
//    private void doConfirm() {
//        List<BasicNameValuePair> valuePairs = new ArrayList<>();
//        valuePairs.add(new BasicNameValuePair("OPTION", "16"));
//        valuePairs.add(new BasicNameValuePair("CUSTID", LoginServiceManeger.getInstance().getLoginEntity().getId()));
//        valuePairs.add(new BasicNameValuePair("ALI_PAY_NAME", mZFBName.getText().toString().trim()));
//        valuePairs.add(new BasicNameValuePair("ALI_PAY_ACCOUNT", mZFBNUMS.getText().toString().trim()));
//        valuePairs.add(new BasicNameValuePair("ACCOUNT_BANK", mBankName.getText().toString().trim()));
//        valuePairs.add(new BasicNameValuePair("UNIONPAY_ACCOUNT", mBankNUMs.getText().toString().trim()));
//        valuePairs.add(new BasicNameValuePair("UNIONPAY_NAME", mBankAccName.getText().toString().trim()));
//        valuePairs.add(new BasicNameValuePair("TELPHONE", mPhone.getText().toString().trim()));
//        ApiService.OKHttpgetAccountInfo(valuePairs, new MyApiCallback<String>(this) {
//            @Override
//            public void onResponse(String response) {
//                super.onResponse(response);
//                if (!TextUtils.isEmpty(response)) {
//                    try {
//                        JSONObject obj = new JSONObject(response);
//                        if (1 == obj.optInt("code")) {
//                            ToastUtil.showShort(obj.optString("message"));
//                            initData();
//                        } else {
//                            ToastUtil.showShort(obj.optString("message"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Request request, Exception e) {
//                super.onError(request, e);
//            }
//        }, this);
//    }
}

