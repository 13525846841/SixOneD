package com.yksj.consultation.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 支付类型
 */
public interface PayType {
    /**
     * 余额支付
     */
    int OVERAGE = 0;
    /**
     * 银联支付
     */
    int UNION = 3;
    /**
     * 微信支付
     */
    int WECHAT = 2;
    /**
     * 支付宝支付
     */
    int ALI = 1;

    @IntDef({OVERAGE, UNION, WECHAT, ALI})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type{

    }
}
