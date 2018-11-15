package com.yksj.consultation.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 群文件
 */
public class GroupFileBean {
    @SerializedName("GROUP_FILE_ID")
    public String id;

    @SerializedName("GROUP_ID")
    public String groupId;

    @SerializedName("CUSTOMER_ID")
    public String doctorId;

    @SerializedName("FILE_NAME")
    public String fileName;

    @SerializedName("FILE_SIZE")
    public String fileSize;

    @SerializedName("FILE_PATH")
    public String filePath;

    @SerializedName("UPLOAD_TIME")
    public String time;

    @SerializedName("FILE_TYPE")
    public String fileType;

    @Override
    public String toString() {
        return "GroupFileBean{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", filePath='" + filePath + '\'' +
                ", time='" + time + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
