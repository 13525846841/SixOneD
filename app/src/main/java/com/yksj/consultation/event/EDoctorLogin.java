package com.yksj.consultation.event;

import com.yksj.healthtalk.bean.LoginStatus;

public class EDoctorLogin {
    public String what;
    public LoginStatus status;

    public EDoctorLogin(String what, LoginStatus status){
        this.what = what;
        this.status = status;
    }

    public boolean isSucees(){
        return status == LoginStatus.LOGIN_OK;
    }

    public boolean isError(){
        return status == LoginStatus.LOGIN_ERROR;
    }
}
