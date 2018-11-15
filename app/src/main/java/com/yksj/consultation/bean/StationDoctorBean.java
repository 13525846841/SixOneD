package com.yksj.consultation.bean;

import java.util.ArrayList;
import java.util.List;

public class StationDoctorBean {
    public String CUSTOMER_ID;
    public String APPLY_ID;
    public String DOCTOR_REAL_NAME;
    public String DOCTOR_SPECIALLY;
    public String DOCTOR_HOSPITAL;
    public String INTRODUCTION;
    public String ICON_DOCTOR_PICTURE;
    public String WORK_LOCATION;
    public String WORK_LOCATION_DESC;
    public String OFFICE_NAME;
    public String TITLE_NAME;
    public String MANAGE_DESC;
    public String MANAGE_STATUS;
    public String APPLY_DESC;
    public String BIG_ICON_BACKGROUND;
    public int MEMBER_TYPE;
    public int isFollow;
    public StationCommentBean evaluate;
    public List<StationBean> siteDesc;
    public List<DoctorServiceBean> siteService;
    public List<DoctorServiceBean> doctorService;
    public ArrayList<DoctorToolsBean> tools;
}
