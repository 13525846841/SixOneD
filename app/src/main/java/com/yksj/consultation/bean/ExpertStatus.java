package com.yksj.consultation.bean;

/**
 * Created by hww on 17/4/19.
 * Used for 专家邀请状态
 */

public class ExpertStatus {
    public final static String NO_STATE = "0";//无关(可邀请)
    public final static String INVITING_STATE = "10";//已邀请未回应(不可邀请，邀请中)
    public final static String NO_RESPONSE_STATE = "20";//长时间未回应，邀请失败(可邀请)
    public final static String REJECT_STATE = "30";//拒绝邀请(可邀请)
    public final static String ACCEPT_STATE = "40";//接受邀请(不可邀请，邀请中)
    public final static String OVER_ACCEPT_STATE = "50";//超时接受邀请(可邀请)
}
