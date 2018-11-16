package com.yksj.consultation.constant;

/**
 * Created by hww on 17/4/20.
 * Used for 常用常量
 */

public class Constant {
    public final static String IS_CHECKED = "isChecked";//是否选中
    public final static String EXPERT_STATE= "expert_state";//专家被邀请的状态
    public final static String All_IDS= "all_ids";//会诊相关人id

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_HOSPITAL = "user_hospital";
    public static final String USER_OFFICE = "user_office";
    public static final String USER_TITLE = "user_title";
    public static final String USER_TOOL = "user_tool";

    public static class Station{
        public static final String STATION_HOME_TYPE = "station_home_type";
        public static final String STATION_ID = "station_id";
        public static final String USER_ID = "user_id";
        public final static String SERVICE_TYPE_ID = "SERVICE_TYPE_ID";//5图文 6电话 7包月 8视频
        public final static String ORDER_ON_OFF = "ORDER_ON_OFF";
        public final static String PRICE = "PRICE";
        public final static String DOCTOR_HOME_TYPE = "doctor_home_type";//0=邀请的、1=申请的
        public final static String STATION_APPLY_REASON = "station_apply_reason";
        public final static String STATION_APPLY_MANAGE_STATUS = "station_apply_manage_status";
        public final static String CHAT_ID = "chat_id";
        public final static String ORDER_TYPE = "order_type";
        public final static String ORDERID_EXTRA = "orderid_extra";
        public final static String GROUPID_EXTRA = "groupid_extra";
    }

    public static class Chat{
        public final static String KEY_PARAME = "param";
        public final static String KEY_CONTENT = "retgretting";
        public final static String SINGLE_ID = "single_id";
        public final static String SINGLE_NAME = "single_name";
        public static final int GROUP_INFO = 3003;//群聊天
        public final static String CONSULTATION_ID = "consult_id";//会诊id
        public final static String CONSULTATION_TYPE = "consult_type";//会诊id
        public final static String CONSULTATION_NAME = "consult_name";//会诊名称
        public final static String GROUP_ID = "group_id";//群聊id
        public final static String OBJECT_TYPE = "object_type";//聊天类型
        public final static String ORDER_ID = "order_id";//订单id
        public final static String CHAT_ID = "chat_id";
    }

    /**
     * 0待分配(是抢单列表) 1待服务(是抢单成功) 2待分配(站长分配) 3服务中  4已完成
     */
    public static class StationOrderStatus {
        public static final String QD = "0";
        public static final String QDSUCESS = "1";
        public static final String ZZFP = "2";
        public static final String FWZ = "3";
        public static final String OVER = "4";
    }

    /**
     * 0 分配订单 1 邀请医生会话
     */
    public static class ChoiceType {
        public static final String FP = "0";
        public static final String YQ = "1";
    }

    /**
     * 2 拒绝 1 接受
     */
    public static class AcceptType {
        public static final String YES = "1";
        public static final String NO = "2";
    }
}
