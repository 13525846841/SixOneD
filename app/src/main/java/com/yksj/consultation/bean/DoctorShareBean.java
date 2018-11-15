package com.yksj.consultation.bean;

import java.util.List;

public class DoctorShareBean {
    public String SHARE_ID;
    public String CUSTOMER_ID;
    public String SHARE_CONTENT;
    public String PUBLIC_TIME;
    public String NOTE;
    public String CUSTOMER_NAME;
    public String CUSTOMER_NICKNAME;
    public String OFFICE_NAME;
    public String CLIENT_ICON_BACKGROUP;
    public int PRAISE_COUNT;
    public int ISFOLLOW;
    public int COMMENT_COUNT;
    public int ISLIKE;
    public int RN;
    public List<CommentPicture> picture;
    public List<DoctorShareCommentBean> comment;

    public boolean isLike() {
        return ISLIKE == 1;
    }

    public void likeChange(boolean like) {
        this.ISLIKE = like ? 1 : 2;
        this.PRAISE_COUNT = like ? ++this.PRAISE_COUNT : --this.PRAISE_COUNT;
    }
}
