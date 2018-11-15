package com.library.base.pay;

import com.google.gson.annotations.SerializedName;

/**
 * 微信支付
 */
public class WeChatPayModel {
    public WeChatPayParams repmap;
    public String pay_id;

    public static class WeChatPayParams {
        public String appid;
        public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
        @SerializedName("package")
        public String packagevalue;
        public String sign;
    }
}
