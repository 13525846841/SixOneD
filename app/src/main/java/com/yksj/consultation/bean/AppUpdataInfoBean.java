package com.yksj.consultation.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 更新数据封装类
 */
public class AppUpdataInfoBean implements Parcelable {

    @SerializedName(value = "server_version")
    public String version;

    @SerializedName(value = "DOWNLOAD_URL")
    public String downloadUrl;

    @SerializedName(value = "MUST_FLAG")
    public int mustFlag;

    @SerializedName(value = "update_message")
    public String message;

    @SerializedName(value = "MD5")
    public long length;

    public boolean isNowInstall;//是否现在安装

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
        dest.writeString(this.downloadUrl);
        dest.writeInt(this.mustFlag);
        dest.writeString(this.message);
        dest.writeLong(this.length);
        dest.writeByte(this.isNowInstall ? (byte) 1 : (byte) 0);
    }

    public AppUpdataInfoBean() {}

    protected AppUpdataInfoBean(Parcel in) {
        this.version = in.readString();
        this.downloadUrl = in.readString();
        this.mustFlag = in.readInt();
        this.message = in.readString();
        this.length = in.readLong();
        this.isNowInstall = in.readByte() != 0;
    }

    public static final Creator<AppUpdataInfoBean> CREATOR = new Creator<AppUpdataInfoBean>() {
        @Override public AppUpdataInfoBean createFromParcel(Parcel source) {return new AppUpdataInfoBean(source);}

        @Override public AppUpdataInfoBean[] newArray(int size) {return new AppUpdataInfoBean[size];}
    };
}
