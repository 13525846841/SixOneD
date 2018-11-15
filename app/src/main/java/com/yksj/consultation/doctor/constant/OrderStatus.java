package com.yksj.consultation.doctor.constant;

/**
 * 订单状态
 */
public interface OrderStatus {
    int NORMAL = 0;//正常
    int REFUNDING = 1;//退款中
    int REFUNDSUCEESS = 2;//退款成功
    int REFUNDERROE = 3;//退款失败
}
