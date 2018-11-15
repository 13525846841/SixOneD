package com.library.base.pay;

import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信支付
 */
public class WeChatPay {

    private static WeChatPay mInstance;
    private String mAppId;
    private boolean mIsRegister = false;

    public static WeChatPay getInstance() {
        if (mInstance == null) {
            mInstance = new WeChatPay();
        }
        return mInstance;
    }

    private WeChatPay() {
        // String appId = AppRuntimeWorker.getWx_app_key();
        // setAppId(appId);
    }

    public String getAppId() {
        return this.mAppId;
    }

    public void setAppId(String appId) {
        this.mAppId = appId;
        register();
    }

    public void register() {
        if (!mIsRegister && !TextUtils.isEmpty(mAppId)) {
            mIsRegister = getWXAPI().registerApp(mAppId);
        }
    }

    public void pay(PayReq request) {
        if (AppUtils.isAppInstalled("com.tencent.mm")) {
            if (request != null) {
                getWXAPI().sendReq(request);
            }
        }else{
            ToastUtils.showShort("微信未安装");
        }
    }

    public IWXAPI getWXAPI() {
        return WXAPIFactory.createWXAPI(ActivityUtils.getTopActivity(), mAppId);
    }

}
