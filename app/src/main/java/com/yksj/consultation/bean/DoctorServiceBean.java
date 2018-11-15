package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;
import com.yksj.consultation.doctor.constant.ServiceType;

public class DoctorServiceBean {
    public String SITE_ID;
    public String SERVICE_TYPE_ID;
    public float SERVICE_PRICE;
    public String SERVICE_TIME_LIMIT;
    public String SERVICE_CREATE_TIME;
    public int ORDER_ON_OFF;
    public String SERVICE_CREATOR;
    public String SITE_NAME;
    public String ORDER_NUM;
    public int FREE_MEDICAL_FLAG;
    public float FREE_MEDICAL_PRICE;

    @SerializedName(value = "FREE_MEDICAL_START", alternate = "FREE_MEDICAL_START_TIME")
    public String FREE_MEDICAL_START_TIME;

    @SerializedName(value = "FREE_MEDICAL_END", alternate = "FREE_MEDICAL_END_TIME")
    public String FREE_MEDICAL_END_TIME;
    public int MAX_SERVICE;
    public int CONSULTATION_FLAG;

    public static DoctorServiceBean valueOf(@ServiceType.Type String serviceType, String stationId, String doctorId, int on_off) {
        DoctorServiceBean serviceBean = new DoctorServiceBean();
        serviceBean.ORDER_ON_OFF = on_off;
        serviceBean.SERVICE_TYPE_ID = serviceType;
        serviceBean.SITE_ID = stationId;
        serviceBean.SERVICE_CREATOR = doctorId;
        return serviceBean;
    }
}