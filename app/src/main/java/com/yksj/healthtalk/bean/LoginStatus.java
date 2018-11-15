package com.yksj.healthtalk.bean;

public enum LoginStatus {
    //空闲,未登录
    NONE(0),
    //登录中
    LOGINING(1),
    // 登录成功
    LOGIN_OK(2),
    // 登录失败
    LOGIN_ERROR(3);

    public int status;

    LoginStatus(int status) {
        this.status = status;
    }
}