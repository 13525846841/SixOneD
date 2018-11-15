package com.yksj.consultation.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class StationBean implements MultiItemEntity {
    public static final int JOIN_TYPE = 2;
    public static final int  RECOMMEND_TYPE = 3;
    public static final int CREATE_TYPE = 1;
    public String SITE_ID;
    public String SITE_NAME;
    public String OFFICE_ID;
    public String SITE_AREA;
    public String SITE_DESC;
    public String SITE_BIG_PIC;
    public String SITE_SMALL_PIC;
    public String SITE_CREATEOR;
    public String SITE_CR_TIME;
    public String SITE_STATUS;
    public String SITE_HOSPOTAL;
    public String VISIT_TIME;
    public String HOSPITAL_DESC;
    public String SITE_CREATEOR_DESC;
    public String ICON_DOCTOR_PICTURE;
    public String DOCTOR_NAME;
    public String OFFICE_NAME;
    public String MEMBER_NUM;
    public String ORDER_NUM;
    public String RN;
    public int stationType;
    public boolean hasHead;

    @Override
    public int getItemType() {
        return stationType;
    }
}
