package com.yksj.consultation.event;

/**
 * Created by jack_tang on 15/9/26.
 * 自己随便写的一个对象  方便简单的传递
 * Event使用_
 */
public class MyEvent {
    /**
     * what: refresh 订单详情的更新
     */
    public String what;
    public int code;

    public MyEvent(String what, int code) {
        super();
        this.what = what;
        this.code = code;
    }
}
