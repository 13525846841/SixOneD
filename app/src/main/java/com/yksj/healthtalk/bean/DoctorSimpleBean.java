package com.yksj.healthtalk.bean;

import java.io.Serializable;

/**
 * 医生实体---简单数据
 * Created by lmk on 15/9/18.
 *
 * DOCTOR_REAL_NAME  	姓名
 DOCTOR_SPECIALLY  	专长
 ICON_DOCTOR_PICTURE 	小头像
 DOCTOR_PICTURE		大头像
 INTRODUCTION		简介
 SERVICE_PRICE		价格
 UNIT_NAME		医院名称
 OFFICE_NAME		科室名称
 TITLE_NAME		职称名称
 NUMS			剩余名额
 *
 */
public class DoctorSimpleBean implements Serializable{

    public int R;
    public String DOCTOR_REAL_NAME;
    public int CUSTOMER_ID;
    public String DOCTOR_SPECIALLY;
    public String ICON_DOCTOR_PICTURE;
    public String DOCTOR_PICTURE;
    public String INTRODUCTION;
    public String SERVICE_PRICE;
    public String UNIT_NAME;
    public String OFFICE_NAME;
    public String TITLE_NAME;
    public int DOCTOR_SERVICE_NUMBER;
    public int NUMS;



}
