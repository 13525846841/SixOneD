package com.yksj.healthtalk.entity;

import com.yksj.consultation.adapter.ChatAdapter.ViewHolder;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.ref.WeakReference;


/**
 * @author zhao
 * 聊天消息实体
 */
public class MessageEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 时间
     */
    public static final int TYPE_TIME = 0;
    /**
     * 语音消息
     */
    public static final int TYPE_VOICE = 1;
    /**
     * 文本
     */
    public static final int TYPE_TEXT = 4;
    /**
     * 图片
     */
    public static final int TYPE_PICTURE = 2;
    /**
     * 视频
     */
    public static final int TYPE_VIDEO = 15;
    /**
     * 商品卡片
     */
    public static final int TYPE_GOODS = 16;
//    /**
//     * 文件上传
//     */
//    public static final int TYPE_FILE = 17;
    /**
     * 坐标
     */
    public static final int TYPE_LOCATION = 10;

    /**
     * 关注,审核通过
     */
    public static final int TYPE_ATTENTION = 12;
    /**
     * 邀请,关注
     */
    public static final int TYPE_INVITATION = 8;
    /**
     * 特殊服务系统通知
     */
    public static final int SYSTME_TIP = 20;//

    /**
     * 消息默认状态
     */
    public static final int STATE_NORMAL = 0;

    /**
     * 消息发送成功 ,上传成功
     */
    public static final int STATE_OK = 1;
    /**
     * 消息发送失败
     */
    public static final int STATE_FAIL = 0;
    /**
     * 消息发送执行中
     */
    public static final int STATE_PROCESING = 2;
    //服务器id
    private String serverId;
    //消息id
    private String id;

    //消息是否已经读取0未读取 1读取
    private int readTag = 0;

    //发送者id
    private String senderId;

    //接收者
    private String receiverId;

    //消息类型
    private int type;

    //选中状态
    private boolean isSelected = false;

    //当前播放状态
    private boolean isPlaying = false;

    //区别消息发送与接收
    private boolean isSendFlag = false;

    //语音消息长度
    private String voiceLength;
    //日期
    private long date;

    //消息内容
    private String content;//图片情况下小图片&大图片
    //	private JSONObject contentJsonObj;//虚拟医生内容 连接内容
    private JSONArray contentJsonArray;
    public CharSequence contCharSequence;//文字内容,表情or可点击标签

    //消息大小
    private String size;

    private String userId;//关注或邀请时候,关注者id或邀请者id

    //详细地址
    private String address;

    public int playProgres;//当前播放的进度

    public int voiceLayoutWidth = -1;//语音显示宽度

    //群名称 (助手使用)
    private String groupName;

    //发送者名称(助手使用)
    private String senderName;

    //群id
    private String groupId;
    private String isBL;//是否是病历：（1是病历，0是会诊）

    private String consultationId;//会诊id,判断医生和患者能否自有聊天

    //发送状态
    private int sendState = STATE_NORMAL;


    //消息客户端类型 0患者 1医生
    private String isDoctorMessage;

    //特殊服务 id
    private String orderId;

    private String isWeChat;//0 不是微信 1 微信类型消息


    public String getIsWeChat() {
        return isWeChat;
    }

    public void setIsWeChat(String isWeChat) {
        this.isWeChat = isWeChat;
    }

    public String getIsBL() {
        return isBL;
    }

    public void setIsBL(String isBL) {
        this.isBL = isBL;
    }

    /**
     * 消息上传下载的状态
     */
    private int downOrUpState = STATE_OK;
    public String messageJsonObjest;//只有在导医护士中用到
    public volatile WeakReference<ViewHolder> viewHolder;//软引用

    private int groupType = 0;//群聊消息类型  0单聊  1群聊  2三人聊天 3特殊服务
    private String allCustomerId;//三人聊天时用的

    public String getAllCustomerId() {
        return allCustomerId;
    }

    public void setAllCustomerId(String allCustomerId) {
        this.allCustomerId = allCustomerId;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public MessageEntity() {
        this.date = System.currentTimeMillis();
    }

    public JSONArray getContentJsonArray() {
        return contentJsonArray;
    }

    public void setContentJsonArray(JSONArray contentJsonArray) {
        this.contentJsonArray = contentJsonArray;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public int getDownOrUpState() {
        return downOrUpState;
    }

    public void setDownOrUpState(int downOrUpState) {
        this.downOrUpState = downOrUpState;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSendFlag() {
        return isSendFlag;
    }

    public void setSendFlag(boolean isSendFlag) {
        this.isSendFlag = isSendFlag;
    }

    public String getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(String voiceLength) {
        this.voiceLength = voiceLength;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getReadTag() {
        return readTag;
    }

    public void setReadTag(int readTag) {
        this.readTag = readTag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getIsDoctorMessage() {
        return isDoctorMessage;
    }

    public void setIsDoctorMessage(String isDoctorMessage) {
        this.isDoctorMessage = isDoctorMessage;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
