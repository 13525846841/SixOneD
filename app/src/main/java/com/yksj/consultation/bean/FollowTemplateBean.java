package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 随访模版实体类
 */
public class FollowTemplateBean {

    @SerializedName("CREATOR_ID")
    public String creatorId;

    @SerializedName("DOCTOR_REAL_NAME")
    public String doctorName;

    @SerializedName("CUSTOMER_ID")
    public String sickId;

    @SerializedName("TEMPLATE_ID")
    public String id;

    @SerializedName("FOLLOW_ID")
    public String followId;

    @SerializedName("CREATE_TIME")
    public String time;

    @SerializedName(value = "TEMPLATE_NAME", alternate = "FOLLOW_UP_NAME")
    public String name;

    @SerializedName("start_time")
    public String startTime;

    @SerializedName("end_time")
    public String endTime;

    @SerializedName("isEnd")
    public String isEnd;

    @SerializedName("SICK_SEE_FLAG")
    public int sickSeeFlag;

    @SerializedName("ALERT_TIMETYPE")
    public int alertTimeType;

    @SerializedName("ALERT_TIMECOUNT")
    public int alertTimeCount;

    @SerializedName("ALERT_SICK")
    public int alertSick;

    @SerializedName("ALERT_ME")
    public int alertMe;

    @SerializedName("FOLLOW_DESC")
    public String followDesc;

    @SerializedName("DOCTOR_ID")
    public int doctorId;
}
