package com.yksj.consultation.bean;

public class ResponseDoctorServiceBean<T> {
    public String message;
    public T service;
    public int code;

    public boolean isSucees(){
        return code == 0;
    }
}
