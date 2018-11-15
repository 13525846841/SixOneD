package com.yksj.consultation.bean;

import com.library.base.utils.AESUtils;
import com.yksj.healthtalk.bean.LoginStatus;

public class LoginBean {

    public static byte[] KEY = new byte[]{48, 48, 49, 55, 68, 67, 49, 66, 69, 50, 50, 53, 56, 53, 53, 52, 67, 70, 48, 50, 67, 53, 55, 66, 55, 56, 69, 55, 52, 48, 65, 53};

    public int loginState;
    public String account;
    public boolean isFirst = true;
    public int versionCode;
    public String password;
    private String token;

    public String getAccount() {
        return account;
    }

    public int getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginStatus loginState) {
        this.loginState = loginState.status;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setAccount(String account) {
        this.account = account;

    }

    public String getPassword() {
        return AESUtils.decrypt(LoginBean.KEY, password);
    }

    public void setPassword(String password) {
        this.password = AESUtils.encrypt(LoginBean.KEY, password);
    }
}
