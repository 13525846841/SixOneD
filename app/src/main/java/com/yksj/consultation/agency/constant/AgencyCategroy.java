package com.yksj.consultation.agency.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 机构首页类型
 */
public interface AgencyCategroy {
    /**
     * 体验中心
     */
    String EXPERIENCE = "experience";
    /**
     * 拓展中心
     */
    String EXPAND = "expand";
    /**
     * 康复中心
     */
    String REHABILITATION = "rehabilitation";
    /**
     * 兴趣中心
     */
    String INTEREST = "interest";

    /**
     * 我的机构
     */
    String SELF = "self";

    /**
     * 推荐
     */
    String RECOMMENT = "recomment";

    @StringDef({AgencyCategroy.EXPERIENCE, AgencyCategroy.EXPAND,
            AgencyCategroy.REHABILITATION, AgencyCategroy.INTEREST,
            AgencyCategroy.SELF, AgencyCategroy.RECOMMENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Categroy{
    }
}
