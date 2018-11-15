package com.yksj.consultation.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class UnionIncidentBean implements MultiItemEntity{

    public int UNION_ID;
    public int EVENT_ID;
    public String EVENT_TITLE;
    public String EVENT_CONTENT;
    public String EVENT_IMAGE;
    public String EVENT_TIME;
    public String RECORD_TIME;
    public int RN;

    @Override
    public int getItemType() {
        return 0;
    }
}
