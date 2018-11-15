package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AgencyActiveBean implements Parcelable {
    public String UNIT_PIC1;
    public String ACTIV_TITLE;
    public String ACTIV_CODE;
    public String ACTIV_DESC;
    public String ACTIV_TIME_DESC;
    public String AGENCY_ID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UNIT_PIC1);
        dest.writeString(this.ACTIV_TITLE);
        dest.writeString(this.ACTIV_CODE);
        dest.writeString(this.ACTIV_DESC);
        dest.writeString(this.ACTIV_TIME_DESC);
    }

    public AgencyActiveBean() {
    }

    protected AgencyActiveBean(Parcel in) {
        this.UNIT_PIC1 = in.readString();
        this.ACTIV_TITLE = in.readString();
        this.ACTIV_CODE = in.readString();
        this.ACTIV_DESC = in.readString();
        this.ACTIV_TIME_DESC = in.readString();
    }

    public static final Parcelable.Creator<AgencyActiveBean> CREATOR = new Parcelable.Creator<AgencyActiveBean>() {
        @Override
        public AgencyActiveBean createFromParcel(Parcel source) {
            return new AgencyActiveBean(source);
        }

        @Override
        public AgencyActiveBean[] newArray(int size) {
            return new AgencyActiveBean[size];
        }
    };
}
