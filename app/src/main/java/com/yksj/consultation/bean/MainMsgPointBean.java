package com.yksj.consultation.bean;

import android.text.Html;
import android.text.TextUtils;

public class MainMsgPointBean {
    public PointBean topList;

    public int getNums(){
        return topList.message.nums;
    }

    public MessageInfoBean getMessageInfo(){
        return topList.message.message;
    }

    public MessageBean getMessage(){
        return topList.message;
    }

    /**
     * 是否有新消息
     * @return true 有新消息
     */
    public boolean hasNewMessage(){
        return getMessageInfo() != null && !TextUtils.isEmpty(getMessageInfo().MESSAGE_CONTENT);
    }

    /**
     * 信息来源
     * @return
     */
    public String getMessageFrom(){
        if (isSystemMsg()) return "系统消息";
        if (!TextUtils.isEmpty(getTargetName())) return getTargetName();
        return "未知来源";
    }

    /**
     * 是否是系统消息
     * @return
     */
    public boolean isSystemMsg(){
        return getMessageInfo().SEND_ID == 100000;
    }

    /**
     * 用户名
     * @return
     */
    public String getTargetName(){
        return getMessageInfo().TARGET_NAME;
    }

    /**
     * 信息内容
     * @return
     */
    public String getMessageContent(){
        String messageContent = getMessageInfo().MESSAGE_CONTENT;
        if (!TextUtils.isEmpty(messageContent)) {
            String messageType = getMessageType();
            if (!TextUtils.isEmpty(messageType)) {
                return messageType;
            } else {
                messageContent = Html.fromHtml(messageContent).toString();
                return messageContent;
            }
        }
        return "";
    }

    /**
     * 信息类型
     * @return
     */
    public String getMessageType(){
        int messageType = getMessageInfo().MESSAGE_TYPE;
        if(messageType == 1) return "[语音]";
        return "";
    }

    public static class PointBean {
        public MessageBean message;
        public ConsultationBean consultation;
    }

    public static class MessageBean {
        public MessageInfoBean message;
        public int nums;
    }

    public static class MessageInfoBean{
        public String MESSAGE_CONTENT;
        public int SEND_ID;
        public String TARGET_NAME;
        public int MESSAGE_TYPE;
    }

    public static class ConsultationBean {
        public String SERVICE_STATUS_NAME;
        public String CONSULTATION_NAME;
        public String NEW_CHANGE;
        public String CONSULTATION_ID;
    }
}
