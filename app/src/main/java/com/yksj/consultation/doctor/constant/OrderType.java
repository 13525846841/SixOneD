package com.yksj.consultation.doctor.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 订单类型
 */
public class OrderType {
    /**
     * 已完成订单
     */
    public static final String FINISH = "1";

    /**
     * 待处理订单
     */
    public static final String PROCESS = "2";

    @StringDef({FINISH, PROCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
