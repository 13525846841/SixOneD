package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 医院实体类
 */
public class HospitalBean implements Parcelable {
    public String UNIT_CODE;
    public String UNIT_NAME;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UNIT_CODE);
        dest.writeString(this.UNIT_NAME);
    }

    public HospitalBean() {}

    protected HospitalBean(Parcel in) {
        this.UNIT_CODE = in.readString();
        this.UNIT_NAME = in.readString();
    }

    public static final Parcelable.Creator<HospitalBean> CREATOR = new Parcelable.Creator<HospitalBean>() {
        @Override public HospitalBean createFromParcel(Parcel source) {return new HospitalBean(source);}

        @Override public HospitalBean[] newArray(int size) {return new HospitalBean[size];}
    };
}
