package com.yksj.consultation.bean;

import com.yksj.healthtalk.net.http.HttpResult;

public class ResponseBean<T> {
    public int code;
    public String message;
    public T result;
    public T orders;
    public T templates;

    public boolean isSuccess() {
        return code == Integer.valueOf(HttpResult.SUCCESS);
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", orders=" + orders + '\'' +
                '}';
    }
}
