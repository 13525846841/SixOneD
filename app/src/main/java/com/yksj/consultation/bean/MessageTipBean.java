package com.yksj.consultation.bean;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

public class MessageTipBean {
    public String site_id;
    public String manage_status;
    public String customerId;
    public String customer_nickname;
    public String sms_req_content;
    public int type;
    public int server_code;
    public String dataHolder;
    public String sendTime;
    public String sendStatus;
    public String order_id;
    public String target_custom_nickname;
    public String isGroupMessage;
    public String sms_target_id;
    public String serverId;

    public String getSite_id() {
        if (!TextUtils.isDigitsOnly(site_id) && "101".equals(manage_status)) {
            return site_id;
        }
        return "";
    }

    public String getFromMsg() {
        if (customerId.equals("100000")) {
            return "系统通知";
        } else if (!TextUtils.isEmpty(customer_nickname)) {
            return customer_nickname;
        } else {
            return "匿名";
        }
    }

    /**
     * 信息内容
     * @return
     */
    public String getMessageContent() {
        if (!TextUtils.isEmpty(sms_req_content)) {
            if (type == 1) {
                return "[语音]";
            } else {
                Spanned spanned = Html.fromHtml(sms_req_content);
                return spanned.toString();
            }
        }
        return "";
    }
}
