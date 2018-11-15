package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 健康讲堂上传数据封装类
 */
public class LectureUploadBean implements Parcelable {
    public int lectureType;// 课件类型
    public String title;// 课件标题
    public String content;// 课件内容
    public String picturePath;// 图片地址
    public String avatarPath;// 视频封面地址
    public String videoPath; // 视频地址
    public String price;// 金额
    public String isOut;// 站内课件
    public String isIn;// 站外课件
    public String stationId;// 工作站ID
    public String videoName;// 视频的TAG

    @Override
    public String toString() {
        return "LectureUploadBean{" +
                "lectureType=" + lectureType +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", picturePath='" + picturePath + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", price='" + price + '\'' +
                ", isOut='" + isOut + '\'' +
                ", isIn='" + isIn + '\'' +
                ", stationId='" + stationId + '\'' +
                ", videoName='" + videoName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.lectureType);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.picturePath);
        dest.writeString(this.avatarPath);
        dest.writeString(this.videoPath);
        dest.writeString(this.price);
        dest.writeString(this.isOut);
        dest.writeString(this.isIn);
        dest.writeString(this.stationId);
        dest.writeString(this.videoName);
    }

    public LectureUploadBean() {
    }

    protected LectureUploadBean(Parcel in) {
        this.lectureType = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.picturePath = in.readString();
        this.avatarPath = in.readString();
        this.videoPath = in.readString();
        this.price = in.readString();
        this.isOut = in.readString();
        this.isIn = in.readString();
        this.stationId = in.readString();
        this.videoName = in.readString();
    }

    public static final Creator<LectureUploadBean> CREATOR = new Creator<LectureUploadBean>() {
        @Override
        public LectureUploadBean createFromParcel(Parcel source) {
            return new LectureUploadBean(source);
        }

        @Override
        public LectureUploadBean[] newArray(int size) {
            return new LectureUploadBean[size];
        }
    };
}
