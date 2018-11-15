package com.yksj.consultation.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yksj.consultation.sonDoc.R;

/**
 * 工作站成员
 */
public class StationMemberBean implements MultiItemEntity {

    private static final int FOUNDER_TYPE = 10;
    private static final int AIDE_TYPE = 20;
    private static final int MEM_TYPE = 30;

    public String SITE_ID;
    public String CUSTOMER_ID;
    public int MEMBER_TYPE;
    public String MEMBER_TYPE_NAME;
    public String MEMBER_STATUS;
    public String MEMBER_STATUS_TIME;
    public String CLIENT_ICON_BACKGROUND;
    public String ICON_DOCTOR_PICTURE;
    public String DOCTOR_PICTURE;
    public String BIG_ICON_BACKGROUND;
    public String DOCTOR_REAL_NAME;
    public String INTRODUCTION;
    public String WORK_LOCATION_DESC;
    public String DOCTOR_OFFICE2;
    public String OFFICE_NAME;
    public String DOCTOR_TITLE;
    public String TITLE_NAME;
    public int itemType;

    @Override
    public String toString() {
        return "StationMemberBean{" +
                "SITE_ID='" + SITE_ID + '\'' +
                ", CUSTOMER_ID='" + CUSTOMER_ID + '\'' +
                ", MEMBER_TYPE='" + MEMBER_TYPE + '\'' +
                ", MEMBER_STATUS='" + MEMBER_STATUS + '\'' +
                ", MEMBER_STATUS_TIME='" + MEMBER_STATUS_TIME + '\'' +
                ", CLIENT_ICON_BACKGROUND='" + CLIENT_ICON_BACKGROUND + '\'' +
                ", CLIENT_ICON_BACKGROUND='" + ICON_DOCTOR_PICTURE + '\'' +
                ", BIG_ICON_BACKGROUND='" + BIG_ICON_BACKGROUND + '\'' +
                ", DOCTOR_REAL_NAME='" + DOCTOR_REAL_NAME + '\'' +
                ", INTRODUCTION='" + INTRODUCTION + '\'' +
                ", WORK_LOCATION_DESC='" + WORK_LOCATION_DESC + '\'' +
                ", DOCTOR_OFFICE2='" + DOCTOR_OFFICE2 + '\'' +
                ", OFFICE_NAME='" + OFFICE_NAME + '\'' +
                ", DOCTOR_TITLE='" + DOCTOR_TITLE + '\'' +
                ", TITLE_NAME='" + TITLE_NAME + '\'' +
                '}';
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    /**
     * 获取工作站职位图标
     * @return
     */
    public int getTypeIcon() {
        switch (MEMBER_TYPE) {
            case FOUNDER_TYPE:
                return R.drawable.ic_station_founder;
            case AIDE_TYPE:
                return R.drawable.ic_station_aide;
            case MEM_TYPE:
                return 0;
            default:
                return 0;
        }
    }
}
