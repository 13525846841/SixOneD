package com.yksj.consultation.station.videoUpload;

import java.util.List;

/**
 * 视频查询实体类
 */
public class VideoQueryBean {
    public Result ret;
    public String requestId;
    public int code;

    public boolean isSucees(){
        return code == 200;
    }

    public static class Result{
        public int count;
        public List<QueryBean> list;
    }

    public static class QueryBean{
        public String objectName;
        public String vid;
    }
}
