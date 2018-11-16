package com.yksj.consultation.bean;

import com.yksj.consultation.doctor.constant.OrderStatus;

public class ServiceOrderBean {
    public long DIFFERENCE_TIME;
    public String SERVICE_START;
    public String SERVICE_END;
    public int ORDER_STATUS;
    public String CUSTOMER_ID;
    public String CUSTOMER_NICKNAME;
    public String CUSTOMER_SEX;
    public int AGE;
    public String SERVICE_SOURCE;
    public int SERVICE_TYPE_ID;
    public int ORDER_ID;
    public String SERVICE_TYPE;
    public String REMAININGTIME;
    public String PAY_ID;
    public String ORDER_CREATE_TIME;
    public long SERVICE_CYCLE;
    public String SERVICE_GOLD;
    public String BIG_ICON_BACKGROUND;
    public String GROUP_ID;
    public String RECORD_NAME;
    public String ENJOY_CUSTOMER_ID;
    public String differenceTime;
    public int adapterInPosition;
//    public int waitTime;//等待时间
    public int isBack;
    public String LASTTIME;
    public String cycle;
    public UserInfo info;

    public String getStatus(){
        if (isBack == OrderStatus.REFUNDING){
            return "正在申请退款中";
        }
        if (isBack == OrderStatus.REFUNDERROE){
            return "退款失败";
        }
        if (isBack == OrderStatus.REFUNDSUCEESS){
            return "退款成功";
        }
        return "";
    }

    public static class UserInfo{
        public String CUSTOMER_SEX;
        public int AGE;
        public String CUSTOMER_NICKNAME;
        public String CUSTOMER_ACCOUNTS;
        public String CLIENT_ICON_BACKGROUND;

        public String getUserSex(){
            return CUSTOMER_SEX.equals("W") ? "女" : "男";
        }
    }
}
