package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DoctorToolsBean implements Parcelable {
    public String CONSULTATION_CENTER_ID;
    public String TOOL_CODE;
    public String TOOL_NAME;
    public String TOOL_DESC;
    public String TOOL_URL;
    public String TOOL_SEQ;
    public String NOTE;
    public String DOCTOR_ID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CONSULTATION_CENTER_ID);
        dest.writeString(this.TOOL_CODE);
        dest.writeString(this.TOOL_NAME);
        dest.writeString(this.TOOL_DESC);
        dest.writeString(this.TOOL_URL);
        dest.writeString(this.TOOL_SEQ);
        dest.writeString(this.NOTE);
        dest.writeString(this.DOCTOR_ID);
    }

    public DoctorToolsBean() {
    }

    protected DoctorToolsBean(Parcel in) {
        this.CONSULTATION_CENTER_ID = in.readString();
        this.TOOL_CODE = in.readString();
        this.TOOL_NAME = in.readString();
        this.TOOL_DESC = in.readString();
        this.TOOL_URL = in.readString();
        this.TOOL_SEQ = in.readString();
        this.NOTE = in.readString();
        this.DOCTOR_ID = in.readString();
    }

    public static final Parcelable.Creator<DoctorToolsBean> CREATOR = new Parcelable.Creator<DoctorToolsBean>() {
        @Override
        public DoctorToolsBean createFromParcel(Parcel source) {
            return new DoctorToolsBean(source);
        }

        @Override
        public DoctorToolsBean[] newArray(int size) {
            return new DoctorToolsBean[size];
        }
    };
}
