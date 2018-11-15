package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 随访模版实体类
 */
public class FollowTemplateBean {

    @SerializedName("CREATOR_ID")
    public String creatorId;

    @SerializedName("CUSTOMER_ID")
    public String doctorId;

    @SerializedName("TEMPLATE_ID")
    public String id;

    @SerializedName("OLD_TEMPLATE_ID")
    public String oldId;

    @SerializedName("CREATE_TIME")
    public String time;

    @SerializedName(value = "TEMPLATE_NAME")
    public String name;
}
