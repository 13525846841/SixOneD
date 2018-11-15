package com.yksj.consultation.bean;

public class NewsCommentBean {
    public int INFO_ID;
    public int CUSTOMER_ID;
    public int COMMENT_ID;
    public int UPPER_COMMENT_ID;
    public String COMMENT_CONTENT;
    public String COMMENT_TIME;
    public String NOTE;
    public int RN;
    public UserBean customer;

    public static class UserBean {
        public String CUSTOMER_NICKNAME;
        public String CUSTOMER_SEX;
        public String CLIENT_ICON_BACKGROUND;
        public int CUSTOMER_ID;
        public String CUSTOMER_ACCOUNTS;
    }
}
