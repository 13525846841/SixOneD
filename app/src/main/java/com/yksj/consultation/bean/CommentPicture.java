package com.yksj.consultation.bean;

public class CommentPicture {
    public int SHARE_ID;
    public String PICTURE_PATH;
    public int PICTURE_REQ;
    public String NOTE;
    public String SMALL_PICTURE_PATH;
    public int WIDTH;
    public int HEIGHT;

    public static CommentPicture valueOf(String picturePath){
        CommentPicture picture = new CommentPicture();
        picture.PICTURE_PATH = picturePath;
        return picture;
    }
}
