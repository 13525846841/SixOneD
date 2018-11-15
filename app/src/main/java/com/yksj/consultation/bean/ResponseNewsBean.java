package com.yksj.consultation.bean;

import java.util.List;

public class ResponseNewsBean {
    public String message;
    public News news;

    public static class News{
        public List<NewsBean> arts;
        public List<NewsCommentBean> comments;
    }
}
