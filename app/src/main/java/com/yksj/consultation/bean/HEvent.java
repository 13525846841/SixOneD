package com.yksj.consultation.bean;

/**
 * Created by hww on 16/12/27.
 * Used for EventBus 传递实体
 */

public class HEvent {
    public String what;//标记
    public String content;//内容
    public int code;//类型 0 医生 1患者

    public HEvent(String what, String content, int code) {
        super();
        this.what = what;
        this.content = content;
        this.code = code;
    }
}
