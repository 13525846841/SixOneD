package com.library.base.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.pay.WeChatPayModel.WeChatPayParams;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 第三方支付管理
 */
public class PaySdkManager {

    /**
     * 支付宝sdk支付(新)
     */
    @SuppressLint("CheckResult")
    public static void payAlipay(final Activity activity, AliPayModel mainpayModel, final PayResultListner listner) {
        if (mainpayModel == null) {
            ToastUtils.showShort("获取支付参数失败");
            listner.onOther();
            return;
        }

        final String orderSpec = mainpayModel.getOrder_spec();
        String sign = mainpayModel.getSign();
        String signType = mainpayModel.getSign_type();

        if (TextUtils.isEmpty(orderSpec)) {
            ToastUtils.showShort("order_spec为空");
            listner.onOther();
            return;
        }

        if (TextUtils.isEmpty(sign)) {
            ToastUtils.showShort("sign为空");
            listner.onOther();
            return;
        }

        if (TextUtils.isEmpty(signType)) {
            ToastUtils.showShort("signType为空");
            listner.onOther();
            return;
        }

        Observable.create(new ObservableOnSubscribe<Map<String, String>>() {
                    @Override
                    public void subscribe(ObservableEmitter<Map<String, String>> e) throws Exception {
                        PayTask payTask = new PayTask(activity);
                        Map<String, String> result = payTask.payV2(orderSpec, true);
                        e.onNext(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, String>>() {
                    @Override
                    public void accept(Map<String, String> result) throws Exception {
                        // 同步返回需要验证的信息
                        String resultInfo = result.get("result");
                        String resultStatus = result.get("resultStatus");
                        LogUtils.e(resultStatus);
                        LogUtils.e(resultInfo);
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            ToastUtils.showShort("支付成功");
                            listner.onSuccess();
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            ToastUtils.showShort("支付失败");
                            listner.onFail();
                        }
                    }
                });
    }

    /**
     * 微信SDK支付
     */
    public static void payWxPay(Activity activity, WeChatPayParams params, final PayResultListner listner) {
        if (params == null) {
            ToastUtils.showShort("获取支付参数失败");
            listner.onOther();
            return;
        }

        String appId = params.appid;
        if (TextUtils.isEmpty(appId)) {
            ToastUtils.showShort("appId为空");
            listner.onOther();
            return;
        }

        String partnerId = params.partnerid;
        if (TextUtils.isEmpty(partnerId)) {
            ToastUtils.showShort("partnerId为空");
            listner.onOther();
            return;
        }

        String prepayId = params.prepayid;
        if (TextUtils.isEmpty(prepayId)) {
            ToastUtils.showShort("prepayId为空");
            listner.onOther();
            return;
        }

        String nonceStr = params.noncestr;
        if (TextUtils.isEmpty(nonceStr)) {
            ToastUtils.showShort("nonceStr为空");
            listner.onOther();
            return;
        }

        String timeStamp = params.timestamp;
        if (TextUtils.isEmpty(timeStamp)) {
            ToastUtils.showShort("timeStamp为空");
            listner.onOther();
            return;
        }

        String packageValue = params.packagevalue;
        if (TextUtils.isEmpty(packageValue)) {
            ToastUtils.showShort("packageValue为空");
            listner.onOther();
            return;
        }

        String sign = params.sign;
        if (TextUtils.isEmpty(sign)) {
            ToastUtils.showShort("sign为空");
            listner.onOther();
            return;
        }

        WeChatPay.getInstance().setAppId(appId);

        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = packageValue;
        req.sign = sign;
        req.appId = "app data";

        WeChatPay.getInstance().pay(req);
    }

    /**
     * 银联SDK
     */
    public static void payUpApp(Activity activity, UnionPayModel model, final PayResultListner listner) {
        if (model == null) {
            ToastUtils.showShort("支付参数获取失败");
            listner.onOther();
            return;
        }

        String tradeNo = model.getTn();
        if (TextUtils.isEmpty(tradeNo)) {
            ToastUtils.showShort("tn为空");
            listner.onOther();
            return;
        }
        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, null, null, tradeNo, "01");
    }
}
