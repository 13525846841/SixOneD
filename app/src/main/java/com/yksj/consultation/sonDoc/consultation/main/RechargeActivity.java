package com.yksj.consultation.sonDoc.consultation.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.pay.AliPayModel;
import com.library.base.pay.PayResultListner;
import com.library.base.pay.PaySdkManager;
import com.library.base.pay.WeChatPayModel.WeChatPayParams;
import com.library.base.widget.PriceInputFilter;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.PayActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.pay.Constants;
import com.yksj.consultation.sonDoc.pay.PayResult;
import com.yksj.consultation.sonDoc.pay.SignUtils;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.HashMap;

import butterknife.OnClick;
import okhttp3.Request;

import static com.yksj.consultation.sonDoc.consultation.PAtyConsultGoPaying.RSA_PRIVATE;

/**
 * 充值界面
 */
public class RechargeActivity extends BaseTitleActivity {

    private static final int PLUGIN_NOT_INSTALLED = -1;
    private static final int PLUGIN_NEED_UPGRADE = 2;

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private String mMode = "00";

    private EditText money;
    private IWXAPI api;
    private PayResultListner mPayListener = new PayResultListner() {
        @Override public void onSuccess() {

        }

        @Override public void onDealing() {

        }

        @Override public void onFail() {

        }

        @Override public void onCancel() {

        }

        @Override public void onNetWork() {

        }

        @Override public void onOther() {

        }
    };

    @Override public int createLayoutRes() {
        return R.layout.activity_recharge;
    }

    @Override public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("余额充值");
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        initView();
    }

    private void initView() {
        money = findViewById(R.id.et_money);
        money.setFilters(new InputFilter[]{new PriceInputFilter()});
    }

    /**
     * 支付宝支付
     * @param v
     */
    @OnClick(R.id.rl_zhifubao)
    public void onAliPay(View v) {
        if (HStringUtil.isEmpty(money.getText().toString())) {
            ToastUtil.showShort("请输入充值金额");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("OPTION", "2");
        map.put("CUSTID", DoctorHelper.getId());
        map.put("MONEY", money.getText().toString());
        ApiService.OKHttpFillMoney(map, new ApiCallbackWrapper<AliPayModel>() {
            @Override
            public void onResponse(AliPayModel model) {
                PaySdkManager.payAlipay(RechargeActivity.this, model, mPayListener);
            }
        }, this);
    }

    /**
     * 微信支付
     * @param v
     */
    @OnClick(R.id.rl_wechat)
    public void onWechatPay(View v) {
        if (HStringUtil.isEmpty(money.getText().toString())) {
            ToastUtil.showShort("请输入充值金额");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("OPTION", "4");
        map.put("CUSTID", DoctorHelper.getId());
        map.put("MONEY", money.getText().toString());

        ApiService.OKHttpFillMoney(map, new ApiCallbackWrapper<ResponseBean<WeChatPayParams>>() {
            @Override public void onResponse(ResponseBean<WeChatPayParams> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    WeChatPayParams payParams = response.result;
                    PaySdkManager.payWxPay(RechargeActivity.this, payParams, mPayListener);
                } else {
                    ToastUtils.showShort(response.message);
                }
            }
        }, this);
    }

    /**
     * 银行卡支付
     * @param v
     */
    @OnClick(R.id.rl_yinhangka)
    public void onBankCardPay(View v) {
        if (HStringUtil.isEmpty(money.getText().toString())) {
            ToastUtil.showShort("请输入充值金额");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("OPTION", "1");
        map.put("CUSTID", DoctorHelper.getId());
        map.put("MONEY", money.getText().toString());

        ApiService.OKHttpFillMoney(map, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject response = JSON.parseObject(content);
                    if (response.containsKey("error_message")) {
                        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),
                                response.getString("error_message"));
                    } else if (response.containsKey("tn")) {
                        // {"PAY_ID":"1140227007141885","tn":"201402271420460080192"}
                        /*************************************************
                         *
                         * 步骤2：通过银联工具类启动支付插件
                         *
                         ************************************************/
                        // mMode参数解释：
                        // 0 - 启动银联正式环境
                        // 1 - 连接银联测试环境
                        int ret = UPPayAssistEx.startPay(RechargeActivity.this, null,
                                null, response.getString("tn"), mMode);
                        if (ret == PLUGIN_NEED_UPGRADE
                                || ret == PLUGIN_NOT_INSTALLED) {
                            // 需要重新安装控件
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    RechargeActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

                            builder.setNegativeButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                            UPPayAssistEx
                                                    .installUPPayPlugin(RechargeActivity.this);
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
                    }
                } catch (Exception e) {
                    if (content.contains("支付宝")) {
                        Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                        intent.putExtra("summary", content);
                        startActivity(intent);
                    }
                }
            }
        }, this);
    }

    private static final int SDK_PAY_FLAG = 1;

    /**
     * 支付宝支付充值钱包
     * @param orderInfo
     * @param sign
     */
    private void payZFB(String orderInfo, String sign) {
        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
//        String sign2 = sign(orderInfo);
//        try {
//            /**
//             * 仅需对sign 做URL编码
//             */
//            sign = URLEncoder.encode(sign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
//        final String payInfo = "app_id=2017022405848913&method=alipay.trade.app.pay&format=JSON&charset=UTF-8&sign_type=RSA&timestamp=2017-03-02 13:04:51&version=1.0&notify_url=http://notify_url&biz_content=%7B%22subject%22%3A%22order%22%2C%22out_trade_no%22%3A%22123123123131%22%2C%22total_amount%22%3A%22100%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%7D&sign=n4mZyQlQf+aK0GysIUXHMS+tNxaEtikdaKCBVo30HxFz5PhmKsJbr8FjR2T4/F1NZkzLEQwAJOTeJvmaDDiEO0WDL2AVxBbhallrPSQMDnpLuwNZD6dvbvzMjiLsc/HQAeHHnPWQHb2d/GKXTuyb0xvhYAJQK/iHoiCH24pfjVc=";

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(RechargeActivity.this);
                // 调用支付接口，获取支付结果
//                String result = String.valueOf(alipay.pay(payInfo,true));
                String result = String.valueOf(alipay.payV2(payInfo, true));
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * sign the order info. 对订单信息进行签名
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MyEvent("refresh", 0));
                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 微信调起
     * @param bean
     */
    private void sendWXPay(BaseBean bean) {
        org.json.JSONObject json = null;
        try {
            json = new org.json.JSONObject(bean.result);
            PayReq req = new PayReq();
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 银联支付充值钱包 OPTION=1
     */
    public void onClickBankPay() {

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data) {
        /*************************************************
         *
         * 步骤3：处理银联手机支付控件返回的支付结果
         *
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
            ToastUtil.showShort(msg);
            finish();
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), msg);
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), msg);
        }
        super.onActivityResult(arg0, arg1, data);
    }
}
