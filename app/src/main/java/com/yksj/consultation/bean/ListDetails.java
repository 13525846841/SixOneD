package com.yksj.consultation.bean;

/**
 * Created by HEKL on 15/9/24.
 * Used for 会诊列表项内内容_
 */
public class ListDetails {
    private int R;
    private int CONSULTATION_ID;
    private int CONSULTATION_CENTER_ID;
    private String CONSULTATION_NAME;
    private String CONSULTATION_DESC;
    private int INVITATION_FLAG;
    private String STATUS_TIME;
    private int PROMOTER_TYPE;
    private int EXPERT_ID;
    private String CREATE_TIME;
    private int CONSULTATION_STATUS;
    private String BIG_ICON_BACKGROUND;
    private String CLIENT_ICON_BACKGROUND;
    private String ISTALK;
    private String SERVICE_STATUS_NAME;
    private String SERVICE_OPERATION;
    private int NEW_CHANGE_PATIENT;
    private int NEW_CHANGE_DOCTOR;
    private int NEW_CHANGE_EXPERT;

    public int getNEW_CHANGE_PATIENT() {
        return NEW_CHANGE_PATIENT;
    }

    public void setNEW_CHANGE_PATIENT(int NEW_CHANGE_PATIENT) {
        this.NEW_CHANGE_PATIENT = NEW_CHANGE_PATIENT;
    }

    public int getNEW_CHANGE_DOCTOR() {
        return NEW_CHANGE_DOCTOR;
    }

    public void setNEW_CHANGE_DOCTOR(int NEW_CHANGE_DOCTOR) {
        this.NEW_CHANGE_DOCTOR = NEW_CHANGE_DOCTOR;
    }

    public int getNEW_CHANGE_EXPERT() {
        return NEW_CHANGE_EXPERT;
    }

    public void setNEW_CHANGE_EXPERT(int NEW_CHANGE_EXPERT) {
        this.NEW_CHANGE_EXPERT = NEW_CHANGE_EXPERT;
    }

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }

    public int getCONSULTATION_ID() {
        return CONSULTATION_ID;
    }

    public void setCONSULTATION_ID(int CONSULTATION_ID) {
        this.CONSULTATION_ID = CONSULTATION_ID;
    }

    public int getCONSULTATION_CENTER_ID() {
        return CONSULTATION_CENTER_ID;
    }

    public void setCONSULTATION_CENTER_ID(int CONSULTATION_CENTER_ID) {
        this.CONSULTATION_CENTER_ID = CONSULTATION_CENTER_ID;
    }

    public String getCONSULTATION_NAME() {
        return CONSULTATION_NAME;
    }

    public void setCONSULTATION_NAME(String CONSULTATION_NAME) {
        this.CONSULTATION_NAME = CONSULTATION_NAME;
    }

    public String getCONSULTATION_DESC() {
        return CONSULTATION_DESC;
    }

    public void setCONSULTATION_DESC(String CONSULTATION_DESC) {
        this.CONSULTATION_DESC = CONSULTATION_DESC;
    }

    public int getINVITATION_FLAG() {
        return INVITATION_FLAG;
    }

    public void setINVITATION_FLAG(int INVITATION_FLAG) {
        this.INVITATION_FLAG = INVITATION_FLAG;
    }

    public String getSTATUS_TIME() {
        return STATUS_TIME;
    }

    public void setSTATUS_TIME(String STATUS_TIME) {
        this.STATUS_TIME = STATUS_TIME;
    }

    public int getPROMOTER_TYPE() {
        return PROMOTER_TYPE;
    }

    public void setPROMOTER_TYPE(int PROMOTER_TYPE) {
        this.PROMOTER_TYPE = PROMOTER_TYPE;
    }

    public int getEXPERT_ID() {
        return EXPERT_ID;
    }

    public void setEXPERT_ID(int EXPERT_ID) {
        this.EXPERT_ID = EXPERT_ID;
    }

    public String getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(String CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public int getCONSULTATION_STATUS() {
        return CONSULTATION_STATUS;
    }

    public void setCONSULTATION_STATUS(int CONSULTATION_STATUS) {
        this.CONSULTATION_STATUS = CONSULTATION_STATUS;
    }

    public String getBIG_ICON_BACKGROUND() {
        return BIG_ICON_BACKGROUND;
    }

    public void setBIG_ICON_BACKGROUND(String BIG_ICON_BACKGROUND) {
        this.BIG_ICON_BACKGROUND = BIG_ICON_BACKGROUND;
    }

    public String getCLIENT_ICON_BACKGROUND() {
        return CLIENT_ICON_BACKGROUND;
    }

    public void setCLIENT_ICON_BACKGROUND(String CLIENT_ICON_BACKGROUND) {
        this.CLIENT_ICON_BACKGROUND = CLIENT_ICON_BACKGROUND;
    }

    public String getISTALK() {
        return ISTALK;
    }

    public void setISTALK(String ISTALK) {
        this.ISTALK = ISTALK;
    }

    public String getSERVICE_STATUS_NAME() {
        return SERVICE_STATUS_NAME;
    }

    public void setSERVICE_STATUS_NAME(String SERVICE_STATUS_NAME) {
        this.SERVICE_STATUS_NAME = SERVICE_STATUS_NAME;
    }

    public String getSERVICE_OPERATION() {
        return SERVICE_OPERATION;
    }

    public void setSERVICE_OPERATION(String SERVICE_OPERATION) {
        this.SERVICE_OPERATION = SERVICE_OPERATION;
    }

    @Override
    public String toString() {
        return "ListDetails{" +
                "R=" + R +
                ", CONSULTATION_ID=" + CONSULTATION_ID +
                ", CONSULTATION_CENTER_ID=" + CONSULTATION_CENTER_ID +
                ", CONSULTATION_NAME='" + CONSULTATION_NAME + '\'' +
                ", CONSULTATION_DESC='" + CONSULTATION_DESC + '\'' +
                ", INVITATION_FLAG=" + INVITATION_FLAG +
                ", STATUS_TIME='" + STATUS_TIME + '\'' +
                ", PROMOTER_TYPE=" + PROMOTER_TYPE +
                ", EXPERT_ID=" + EXPERT_ID +
                ", CREATE_TIME='" + CREATE_TIME + '\'' +
                ", CONSULTATION_STATUS=" + CONSULTATION_STATUS +
                ", BIG_ICON_BACKGROUND='" + BIG_ICON_BACKGROUND + '\'' +
                ", CLIENT_ICON_BACKGROUND='" + CLIENT_ICON_BACKGROUND + '\'' +
                ", ISTALK='" + ISTALK + '\'' +
                ", SERVICE_STATUS_NAME='" + SERVICE_STATUS_NAME + '\'' +
                ", SERVICE_OPERATION='" + SERVICE_OPERATION + '\'' +
                '}';
    }
}