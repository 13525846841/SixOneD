package com.yksj.consultation.bean;

public class BarcodeBean {
    public String message;
    public String path;
    public int code;
    public String customer_account;
    public String introduction;

    public boolean isSuccess(){
        return code == 0;
    }
}
