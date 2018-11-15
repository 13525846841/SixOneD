package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yksj.consultation.constant.LectureType;

public class LectureBean implements Parcelable {
    public String COURSE_ID;
    public String SITE_ID;
    public String COURSE_UP_ID;
    public String COURSE_UP_TIME;
    public String COURSE_NAME;
    public String COURSE_DESC;
    public String SMALL_PIC;
    public String SMALL_COURSE_ADDRESS;
    public String BIG_PIC;
    public int COURSE_PAY;
    public float COURSE_IN_PRICE;
    public float COURSE_OUT_PRICE;
    public int COURSE_CLASS;
    public String COURSE_ADDRESS;
    public int COURSE_LIST_TYPE;
    public String COURSE_TIME1;
    public String COURSE_TIME2;
    public int COURSE_IN_LIST;
    public String COURSE_OUT_LIST;
    public String COURSE_STATUS;
    public int COURSE_SCORE;
    public String COURSE_STATUS_TM;
    public String COURSE_STATUS_PERSON;
    public String NOTE;
    public String COURSE_CATEGORY;
    public int EvaNum;
    public int BuyerNum;
    public int pay_status;// 支付状态(10-已生成订单、未支付；20-已支付；30-超时支付;
    public int avgStar;
    public int VISIT_TIME;
    public String COURSE_UP_NAME;

    /**
     * 是否已经购买
     * @return
     */
    public boolean isPay(){
        return pay_status == 20;
    }

    /**
     * 是否是视频课件
     * @return
     */
    public boolean isVideo(){
        return COURSE_CLASS == LectureType.VIDEO_TYPE;
    }

    /**
     * 是否是图文课件
     * @return
     */
    public boolean isArticle(){
        return COURSE_CLASS == LectureType.GRAPHIC_TYPE;
    }

    /**
     * 是否免费
     * @return
     */
    public boolean isFree(){
        return getPrice() == 0f;
    }

    /**
     * 是否是站内课件
     * @return
     */
    public boolean isInLecture(){
        return COURSE_IN_LIST == 1;
    }

    /**
     * 获取课件价格
     * @return
     */
    public float getPrice(){
        return isInLecture() ? COURSE_IN_PRICE : COURSE_OUT_PRICE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.COURSE_ID);
        dest.writeString(this.SITE_ID);
        dest.writeString(this.COURSE_UP_ID);
        dest.writeString(this.COURSE_UP_TIME);
        dest.writeString(this.COURSE_NAME);
        dest.writeString(this.COURSE_DESC);
        dest.writeString(this.SMALL_PIC);
        dest.writeString(this.SMALL_COURSE_ADDRESS);
        dest.writeString(this.BIG_PIC);
        dest.writeFloat(this.COURSE_IN_PRICE);
        dest.writeFloat(this.COURSE_OUT_PRICE);
        dest.writeInt(this.COURSE_CLASS);
        dest.writeString(this.COURSE_ADDRESS);
        dest.writeInt(this.COURSE_LIST_TYPE);
        dest.writeString(this.COURSE_TIME1);
        dest.writeString(this.COURSE_TIME2);
        dest.writeInt(this.COURSE_IN_LIST);
        dest.writeString(this.COURSE_OUT_LIST);
        dest.writeString(this.COURSE_STATUS);
        dest.writeString(this.COURSE_STATUS_TM);
        dest.writeString(this.COURSE_STATUS_PERSON);
        dest.writeString(this.NOTE);
        dest.writeString(this.COURSE_CATEGORY);
        dest.writeInt(this.VISIT_TIME);
        dest.writeInt(this.COURSE_SCORE);
        dest.writeString(this.COURSE_UP_NAME);
        dest.writeInt(this.COURSE_PAY);
    }

    public LectureBean() {
    }

    protected LectureBean(Parcel in) {
        this.COURSE_ID = in.readString();
        this.SITE_ID = in.readString();
        this.COURSE_UP_ID = in.readString();
        this.COURSE_UP_TIME = in.readString();
        this.COURSE_NAME = in.readString();
        this.COURSE_DESC = in.readString();
        this.SMALL_PIC = in.readString();
        this.SMALL_COURSE_ADDRESS = in.readString();
        this.BIG_PIC = in.readString();
        this.COURSE_IN_PRICE = in.readFloat();
        this.COURSE_OUT_PRICE = in.readFloat();
        this.COURSE_CLASS = in.readInt();
        this.COURSE_ADDRESS = in.readString();
        this.COURSE_LIST_TYPE = in.readInt();
        this.COURSE_TIME1 = in.readString();
        this.COURSE_TIME2 = in.readString();
        this.COURSE_IN_LIST = in.readInt();
        this.COURSE_OUT_LIST = in.readString();
        this.COURSE_STATUS = in.readString();
        this.COURSE_STATUS_TM = in.readString();
        this.COURSE_STATUS_PERSON = in.readString();
        this.NOTE = in.readString();
        this.COURSE_CATEGORY = in.readString();
        this.VISIT_TIME = in.readInt();
        this.COURSE_SCORE = in.readInt();
        this.COURSE_UP_NAME = in.readString();
        this.COURSE_PAY = in.readInt();
    }

    public static final Parcelable.Creator<LectureBean> CREATOR = new Parcelable.Creator<LectureBean>() {
        @Override
        public LectureBean createFromParcel(Parcel source) {
            return new LectureBean(source);
        }

        @Override
        public LectureBean[] newArray(int size) {
            return new LectureBean[size];
        }
    };
}
