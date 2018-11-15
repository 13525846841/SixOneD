package com.yksj.consultation.bean;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 医生实体类
 */
public class DoctorInfoBean {
    public String customerId;
    public String account;
    public String get_travel_Info;
    public String phone;
    public String customerSex;
    public String bigIconBackground;
    @SerializedName(value = "TRANSFER_GETNAME")
    public String receipt;
    public String password;
    public String infoVersion;
    public String diseaseDesc;
    @SerializedName(value = "doctorTitleName")
    public String job;
    @SerializedName(value = "doctorTitle")
    public String jobCode;
    public String doctorBigIconbackground;
    public String age;
    public String customerNickname;
    @SerializedName(value = "TRANSFER_NAME")
    public String bankName;
    public String roleId;// 666第一次审核 777再次审核 888审核成功
    public String filteringType;
    public String refusal_reason;
    public String doctorClientBackground;
    @SerializedName(value = "TRANSFER_GETTELE")
    public String receiptPhone;
    @SerializedName(value = "TRANSFER_ADDR")
    public String bankBranch;
    @SerializedName(value = "TRANSFER_CODE")
    public String bankAcount;
    public String site_ID;
    public String customerLocus;
    public String doctorCertificate;
    public String email;
    public String redo;
    public String Get_Info_Flag;
    public String centerId;
    public String clientIconBackground;
    @SerializedName(value = "introduction")
    public String introduction;
    public String allergy;
    public String position;
    public String verify_Flag;
    public String banding_state;
    public String dwellingPlace;
    public String doctorRealName;
    public String customerAccounts;
    public String member_type;
    public String showTime;
    public String doctor_class;
    @SerializedName(value = "workLocation")
    public String hospitalAddressCode;
    @SerializedName(value = "workLocationName")
    public String hospitalAddress;
    @SerializedName(value = "doctorOffice2")
    public String officeCode;
    @SerializedName(value = "doctorOffice2Name")
    public String officeName;
    public boolean isSendIos;
    @SerializedName(value = "doctorSpecially")
    public String expertise;
    public String centerName;
    public String qrCode;
    public List<String> listconsultationRecord;
    public boolean isShowNotificationDetail;
    public String qrCodeIcon;
    @SerializedName(value = "unitCode")
    public String hospitalCode;
    @SerializedName(value = "unitName")
    public String hospital;
    public String realName;
    public String serviceFlag;
    public String isSetPsw;
    public String token;
    public String maxVideoTime;
    public String promptVideoTime;
}
