package com.yksj.consultation.event;

import com.yksj.consultation.bean.MessageTipBean;

public class EMainMessage {

    public MessageTipBean data;

    public EMainMessage(MessageTipBean messageTipBean) {
        data = messageTipBean;
    }
}
