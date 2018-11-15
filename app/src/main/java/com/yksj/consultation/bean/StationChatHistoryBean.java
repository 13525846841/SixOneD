package com.yksj.consultation.bean;

import java.util.List;

public class StationChatHistoryBean {
    public String content;
    public String customerId;
    public String dataHolder;
    public String duration;
    public String fileName;
    public String groupid;
    public String isGroupMessage;
    public String isWechat;
    public String object_id;
    public String self;
    public String serverId;
    public String targetCustomerId;
    public String timeStamp;
    public String type;
    public List<KeyWord> keyWords;

    public class KeyWord{
        public String cont;
        public int type;
    }
}
