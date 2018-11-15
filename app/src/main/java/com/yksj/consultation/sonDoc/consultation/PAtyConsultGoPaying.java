package com.yksj.consultation.sonDoc.consultation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unionpay.UPPayAssistEx;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.WalletPayFragmentDialog;
import com.yksj.consultation.comm.WalletPayFragmentDialog.OnClickSureBtnListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.pay.SignUtils;
import com.yksj.consultation.setting.SettingPhoneBound;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.wallet.PwdSettingActivity;

/**
 * @author HEKL
 *         会诊选择支付页面
 *         支付引导
 */
public class PAtyConsultGoPaying extends BaseActivity implements OnClickSureBtnListener,
        OnClickListener, OnDilaogClickListener {

    private TextView mWalletMoney;
    private String consultationId;//会诊id
    private static final int PLUGIN_NOT_INSTALLED = -1;
    private static final int PLUGIN_NEED_UPGRADE = 2;
    private static final int ACTIVITY_FINISH = 401;//销毁本界面

    /*****************************************************************
     * mMode参数解释：
     * "00" - 启动银联正式环境
     * "01" - 连接银联测试环境
     *****************************************************************/
    private String mMode = "00";
    private String payId;
    private String type = "";
    private static final String TN_URL_01 = "http://222.66.233.198:8080/sim/gettn";
    private boolean isBindPhone, isSetPsw;//是否绑定手机,是否设置支付密码
    private boolean isPaying = false;
    // 商户私钥，pkcs8格式
    public static String RSA_PRIVATE = "";

    public interface OnBuyTicketHandlerListener {
        /**
         * @param state  1 成功,0多美币不足,-1失败
         * @param entity
         */
        void onTicketHandler(String state, GroupInfoEntity entity);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_guidefor_paying);
        initWidget();
        initDate();
    }

    private void initDate() {
        consultationId = "" + getIntent().getIntExtra("conId", 0);
        if (getIntent().hasExtra("type")) {//没有订单
            type = getIntent().getExtras().getString("type");
        } else {
            payId = getIntent().getStringExtra("payId");
//				send_code = getIntent().getStringExtra("send_code");
        }
    }

    @Override
    protected void onStart() {
        ApiService.doHttpGetQianBaoInfo("1", new AyncHander("WalletBalance"));
        super.onStart();
    }

    private void initWidget() {
        initializeTitle();
        titleTextV.setText("选择支付方式");
        titleLeftBtn.setOnClickListener(this);
        mWalletMoney = (TextView) findViewById(R.id.tv_restofpurse);
        findViewById(R.id.rl_zfb).setOnClickListener(this);
        findViewById(R.id.rl_bank).setOnClickListener(this);
        findViewById(R.id.rl_purse).setOnClickListener(this);
        findViewById(R.id.rl_discount_coupon).setOnClickListener(this);


    }

    /**
     * 支付宝支付
     *
     * @param v
     */
    public void onClickAlipay(View v) {
        if (isPaying)//正在支付
            return;
        isPaying = true;
        ApiService.doHttpConsultationWalletPay(consultationId, "", 2, new AyncHander("Alipay"));
    }

    /**
     * 银联支付
     *
     * @param v
     */
    public void onClickUnionpay(View v) {
        ApiService.doHttpConsultationWalletPay(consultationId, "", 1, new AyncHander("Unionpay"));
    }

    /**
     * 钱包
     *
     * @param v
     */
    public void onClickWallet(View v) {
        if (!isBindPhone) {
            DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "使用钱包支付，需绑定手机并设置支付密码，您目前未绑定手机。", "稍后再说", "现在绑定", this);
        } else if (!isSetPsw) {//米有设置密码
            DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "使用钱包支付，需绑定手机并设置支付密码，您目前未设置支付密码。", "稍后再说", "现在设置", this);
        } else {
            WalletPayFragmentDialog.show(getSupportFragmentManager(), "输入支付密码", "");
        }
    }

    class AyncHander extends AsyncHttpResponseHandler {
        private String type;

        public AyncHander(String string) {
            super(PAtyConsultGoPaying.this);
            this.type = string;
        }

        @Override
        public void onFinish() {
            isPaying = false;
            super.onFinish();
        }

        @Override
        public void onFailure(Throwable error, String content) {
            isPaying = false;
            super.onFailure(error, content);
        }

        @Override
        public void onSuccess(int statusCode, String content) {
            if (type.equals("Alipay") && content.startsWith("<")) {//支付宝支付跳转
                Intent intent = new Intent(getApplicationContext(), ConsultationAliPayActivity.class);
                intent.putExtra("conId", consultationId);
                startActivityForResult(intent, ACTIVITY_FINISH);
                return;
            }
            try {
                JSONObject response = JSON.parseObject(content);
                if (response.containsKey("errormessage")) {
                    SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), response.getString("errormessage"));
                    return;
                } else if (type.equals("WalletBalance")) {

//						{"Balance":"0"}
                    if (response.containsKey("YELLOW_BOY")) {
                        mWalletMoney.setText("余额:" + response.getIntValue("YELLOW_BOY") + "元");
                        isSetPsw = (1 == response.getIntValue("PAYMENT_PASSWORD"));//是否设置密码
                        isBindPhone = (1 == response.getIntValue("PHONE_NUMBER"));//是否绑定手机
                    }
                } else if (response.containsKey("tn")) {
//						{"PAY_ID":"1140227007141885","tn":"201402271420460080192"}
                    /*************************************************
                     *
                     *  步骤2：通过银联工具类启动支付插件
                     *
                     ************************************************/
                    // mMode参数解释：
                    // 0 - 启动银联正式环境
                    // 1 - 连接银联测试环境
                    int ret = UPPayAssistEx.startPay(PAtyConsultGoPaying.this, null, null, response.getString("tn"), mMode);
                    if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
                        // 需要重新安装控件
                        AlertDialog.Builder builder = new AlertDialog.Builder(PAtyConsultGoPaying.this);
                        builder.setTitle("提示");
                        builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

                        builder.setNegativeButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        UPPayAssistEx.installUPPayPlugin(PAtyConsultGoPaying.this);
                                    }
                                });

                        builder.setPositiveButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();

                    }
                } else if (type.equals("Wallet")) {
                    if (!response.containsKey("errormessage")) {
                        ToastUtil.showShort("支付成功");
//						SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "支付成功");
//						Intent intent=new Intent(PAtyConsultGoPaying.this,AtyConsultServer.class);
//						startActivity(intent);
                        setResult(RESULT_OK);
                        PAtyConsultGoPaying.this.finish();
                    } else {
                        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), response.getString("errormessage"));
                    }
                }
            } catch (Exception e) {
            }
            super.onSuccess(statusCode, content);
        }
    }
    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            isSetPsw = true;
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data.hasExtra("phone_num")) {
                isBindPhone = true;
            } else {
                isBindPhone = false;
            }
        } else if (requestCode == ACTIVITY_FINISH && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            PAtyConsultGoPaying.this.finish();

        } else {
            /*************************************************
             *
             *  步骤3：处理银联手机支付控件返回的支付结果
             *
             ************************************************/
            if (data == null) {
                return;
            }

            String msg = "";
            /*
			 * 支付控件返回字符串:success、fail、cancel
			 *      分别代表支付成功，支付失败，支付取消
			 */
            String str = data.getExtras().getString("pay_result");
            if (str.equalsIgnoreCase("success")) {
                msg = "支付成功！";
//				Intent intent=new Intent(PAtyConsultGoPaying.this,AtyConsultServer.class);
//				startActivity(intent);
                setResult(RESULT_OK);
                PAtyConsultGoPaying.this.finish();
            } else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败！";
            } else if (str.equalsIgnoreCase("cancel")) {
                msg = "用户取消了支付";
            }
            ToastUtil.showShort(msg);
//			SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  msg);
        }
    }

    @Override
    public void onClick(DialogFragment fragment, View v) {
        if (!isBindPhone) {
            Intent intent = new Intent(this, SettingPhoneBound.class);
            startActivityForResult(intent, 2);
        } else if (!isSetPsw) {
            Intent intent = new Intent(getApplicationContext(), PwdSettingActivity.class);
            intent.putExtra("isPayPwd", isSetPsw);
            intent.putExtra("isBDPhoneNum", SmartFoxClient.getLoginUserInfo().getPoneNumber());
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onClickSureHander(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShort(getApplicationContext(), "密码不能为空");
        } else {
            ApiService.doHttpConsultationWalletPay(consultationId, MD5Utils.getMD5(pwd), 3, new AyncHander("Wallet"));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_zfb://支付宝
                onClickAlipay(v);
                break;
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.rl_bank://银联
                onClickUnionpay(v);
                break;
            case R.id.rl_purse://钱包
                onClickWallet(v);
                break;
            case R.id.rl_discount_coupon:
                Intent intent = new Intent(PAtyConsultGoPaying.this, PConsultCouponActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onDismiss(DialogFragment fragment) {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
