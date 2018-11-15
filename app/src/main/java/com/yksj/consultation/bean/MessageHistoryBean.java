package com.yksj.consultation.bean;

import android.text.Html;

import com.yksj.healthtalk.net.socket.SmartFoxClient;

public class MessageHistoryBean {
    public String TARGET_ID;
    public String OFFLINE_ID;
    public String SEND_ID;
    public String TARGET_NAME;
    public String TARGET_ROLE_ID;
    public String TARGET_SEX;
    public String MESSAGE_CONTENT;
    public String OBJECT_TYPE;
    public String GROUP_ID;
    public int NUMS;
    public String SEND_TIME;
    public int MESSAGE_TYPE;
    public String ISMEDICAL_REC;
    public String OBJECT_ID;
    public int CLIENT_TYPE;
    public int ISGROUPMESSAGE;
    public String SEND_TIME_ORIGINAL;
    public String CLIENT_ICON_BACKGROUND;

    public boolean isGroup() {
        return 1 == ISGROUPMESSAGE;
    }

    public String getContent() {
        switch (MESSAGE_TYPE) {
            case 1:
                return "[语音]";
            case 2:
                return "[图片]";
            case 10:
                return "[坐标]";
            default:
                return Html.fromHtml(MESSAGE_CONTENT).toString();
        }
    }

    public String getName(){
        if (isSystemMsg()){
            return "系统通知";
        }else{
            return TARGET_NAME;
        }
    }

    public boolean isSystemMsg(){
        return SmartFoxClient.helperId.equals(SEND_ID) || SmartFoxClient.helperId.equals(TARGET_ID);
    }
}
