package com.yksj.consultation.bean;

import java.util.List;

public class DoctorCommentListBean {
    public int commentNum;
    public List<CommentListBean> commentList;

    public static class CommentListBean{
        public String COMMENT_RESULT;
        public String PATIENT_ID;
        public String SERVICE_LEVEL;
        public String REAL_NAME;
    }
}
