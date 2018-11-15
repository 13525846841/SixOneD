package com.yksj.consultation.bean;

import android.text.TextUtils;

public class DoctorShareCommentBean {
    public String COMMENT_ID;
    public String COMMENT_TIME;
    public String CUSTOMER_ID;
    public String COMMENT_CUSTOMER_ID;
    public String CUSTOMER_NAME;
    public String COMMENT_CONTENT;
    public String COMMENT_CUSTOMER_NAME;

    public boolean isReply(){
        return !TextUtils.isEmpty(COMMENT_CUSTOMER_ID);
    }
}
