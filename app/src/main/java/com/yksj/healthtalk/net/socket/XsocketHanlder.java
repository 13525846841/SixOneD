package com.yksj.healthtalk.net.socket;

import com.blankj.utilcode.util.LogUtils;

import org.xsocket.connection.INonBlockingConnection;

import java.io.IOException;
import java.nio.BufferUnderflowException;

public class XsocketHanlder implements XsocketUtilinterface {

    private final String TAG = this.getClass().getSimpleName();

    public XsocketHanlderListener listener;

    @Override
    public boolean onIdleTimeout(INonBlockingConnection iNonBlockingConnection) throws IOException {
        LogUtils.e("onIdleTimeout");
        return false;
    }

    public interface XsocketHanlderListener {
        void XsocketonConnect(INonBlockingConnection arg0);

        void XsocketonDisconnect(INonBlockingConnection arg0);

        void XsocketonData(INonBlockingConnection arg0);
    }

    public void setListener(XsocketHanlderListener listener) {
        this.listener = listener;
    }

    public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
        if (listener != null)
            listener.XsocketonDisconnect(arg0);
        System.out.println(TAG + "-------------onDisconnect----XsocketHanlder");
        return true;
    }

    public boolean onConnect(INonBlockingConnection arg0) throws BufferUnderflowException {
        if (listener != null)
            listener.XsocketonConnect(arg0);
        System.out.println(TAG + "-------------onConnect----XsocketHanlder");
        return true;
    }

    public boolean onData(INonBlockingConnection arg0) throws BufferUnderflowException {
        if (listener != null) {
            listener.XsocketonData(arg0);
        }
        return true;
    }

    /**
     * 连接超时
     * @param arg0
     * @return
     * @throws IOException
     */
    @Override
    public boolean onConnectionTimeout(INonBlockingConnection arg0) {
        LogUtils.e("-------------onConnectionTimeout----XsocketHanlder");
        return false;
    }
}