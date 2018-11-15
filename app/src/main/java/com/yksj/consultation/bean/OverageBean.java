package com.yksj.consultation.bean;

public class OverageBean {
    public String message;
    public float balance;
    public int code;

    public boolean isSucees(){
        return code == 0;
    }
}
