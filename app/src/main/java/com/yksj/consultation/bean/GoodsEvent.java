package com.yksj.consultation.bean;

/**
 * 商品通知类
 */
public class GoodsEvent {

    public String what;
    public int code;//    code 1 选择推荐商品

    public GoodsEvent(String what, int code) {
        super();
        this.what = what;
        this.code = code;
    }
}
