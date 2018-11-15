package com.yksj.consultation.agency.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 机构首页类型分类
 */
public interface AgencyType {
    /**
     * 热门
     */
    String HOT = "hot";
    /**
     * 最新
     */
    String NEW = "new";
    /**
     * 附近
     */
    String NEAR = "near";

    @StringDef({HOT, NEW, NEAR})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type{
    }
}
