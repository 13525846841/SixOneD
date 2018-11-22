package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 随访模版实体类
 */
public class FollowTemplateSubBean {

    @SerializedName("TEMPLATE_ID")
    public String id;

    @SerializedName("FOLLOW_ID")
    public String followId;

    @SerializedName(value = "FOLLOW_SUB_ID", alternate = "TEMPLATE_SUB_ID")
    public String followSubId;

    @SerializedName(value = "FOLLOW_SUB_TIMETYPE", alternate = "TEMPLATE_SUB_TIMETYPE")
    public String followSubTimeType;

    @SerializedName("TIMETYPE_COUNT")
    public String timeTypeCount;

    @SerializedName(value = "FOLLOW_CONTENT", alternate = "TEMPLATE_SUB_CONTENT")
    public String followContent;

    @SerializedName("FOLLOW_TIME")
    public String followTime;

    @SerializedName(value = "FOLLOW_DESC", alternate = "TEMPLATE_SUB_DESC")
    public String desc;

    @SerializedName("CREATE_TIME")
    public String time;

    @SerializedName("FOLLOW_SEQ")
    public int followSeq;
}
