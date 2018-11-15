package com.library.base.docloader;

import android.os.Parcel;
import android.os.Parcelable;

public class DocEntity implements Parcelable {
    public String path;
    public int size;
    public String time;
    public String mimeType;
    public String title;
    public int typeRes;

    public DocEntity(String path, int size, String title, String time, String mimeType, int typeRes) {
        this.path = path;
        this.size = size;
        this.time = time;
        this.title = title;
        this.mimeType = mimeType;
        this.typeRes = typeRes;
    }

    @Override
    public String toString() {
        return "DocEntity{" +
                "path='" + path + '\'' +
                ", size=" + size +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeInt(this.size);
        dest.writeString(this.time);
        dest.writeString(this.mimeType);
        dest.writeString(this.title);
        dest.writeInt(this.typeRes);
    }

    protected DocEntity(Parcel in) {
        this.path = in.readString();
        this.size = in.readInt();
        this.time = in.readString();
        this.mimeType = in.readString();
        this.title = in.readString();
        this.typeRes = in.readInt();
    }

    public static final Parcelable.Creator<DocEntity> CREATOR = new Parcelable.Creator<DocEntity>() {
        @Override
        public DocEntity createFromParcel(Parcel source) {return new DocEntity(source);}

        @Override
        public DocEntity[] newArray(int size) {return new DocEntity[size];}
    };
}
