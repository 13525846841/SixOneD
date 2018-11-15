package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;

public class AgencyBean {
    @SerializedName(value = "UNIT_CODE")
    public String id;
    @SerializedName(value = "UNIT_NAME")
    public String name;
    @SerializedName(value = "UNIT_PIC1")
    public String avatar;
    @SerializedName(value = "CLASS_TYPE")
    public int type;
    @SerializedName(value = "UNIT_SPECIALTY_DESC")
    public String desc;
    @SerializedName(value = "UNIT_ADDRESS_DESC")
    public String detailAddredd;
    @SerializedName(value = "UNIT_TEL1")
    public String telephone;
    @SerializedName(value = "ADDRESS")
    public String address;
    @SerializedName(value = "ADDRESS_CODE")
    public String addressCode;

    public String typeToString(){
        switch (type){
            case 1:
                return "体验中心";
            case 2:
                return "拓展中心";
            case 3:
                return "康复中心";
            case 4:
                return "兴趣中心";
        }
        return "";
    }
}
