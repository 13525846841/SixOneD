package com.yksj.consultation.station.videoUpload;

import java.io.File;

class VideoSpace {
    public static final int CANCEL_NEXT = 0;
    public static final int PROGRESS_NEXT = 1;
    public static final int COMPLETE_NEXT = 2;
    public static final int ERROR_NEXT = 3;
    public static final int START_NEXT = 3;
    public String nosToken;
    public String bucket;
    public String object;
    public File uploadFile;
    public String fileName;
    public long total;
    public long current;
    public int next;
    public String appKey;
    public String token;
    public String accid;
    public String queryBody;
    public String vid;
}
