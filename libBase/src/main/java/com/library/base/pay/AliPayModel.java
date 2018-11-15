package com.library.base.pay;

/**
 * 支付宝支付
 */
public class AliPayModel {
    public String sign;
    public String source;
    public String pay_id;

    public String getOrder_spec() {
        return source + "&sign=\"" + sign + "\"&" + getSign_type();
    }

    public String getSign() {
        return sign;
    }

    public String getSign_type() {
        return "sign_type=\"RSA\"";
    }
}
