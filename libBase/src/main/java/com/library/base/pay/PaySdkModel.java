package com.library.base.pay;

import com.google.gson.Gson;
import com.library.base.pay.WeChatPayModel.WeChatPayParams;

public class PaySdkModel {
    public int code;
    public String message;
    public Object result;

    public WeChatPayParams getWxapay() {
        Gson gson = getGson();
        WeChatPayModel payModel = gson.fromJson(gson.toJson(result), WeChatPayModel.class);
        return payModel.repmap;
    }

    public AliPayModel getMalipay() {
        Gson gson = getGson();
        AliPayModel aliPayModel = new Gson().fromJson(gson.toJson(result), AliPayModel.class);
        return aliPayModel;
    }

    public UnionPayModel getBfupwapModel() {
        Gson gson = getGson();
        UnionPayModel payModel = new Gson().fromJson(gson.toJson(result), UnionPayModel.class);
        return payModel;
    }

    private Gson getGson(){
        return new Gson();
    }

    public boolean isSuccess() {
        return code == 1;
    }
}
