package com.yksj.healthtalk.manager;

import com.blankj.utilcode.util.LogUtils;
import com.yksj.consultation.constant.Configs;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.net.socket.SocketParams;
import com.yksj.healthtalk.net.socket.XsocketHanlder;

import org.json.JSONException;
import org.json.JSONObject;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SocketManager {
    private final String TAG = this.getClass().getSimpleName();
    public static String IP = Configs.SOCKET_IP;
    private static int PORT = Configs.SOCKET_PORT;
    public static SocketManager mSocketManager;
    public static INonBlockingConnection mNbc;
    public static final String SYSTEM_VERSION = "system_version";
    private final static int SOCKET_TAG = 10;// 临时加的 暂无用
    private static final String osVersion = "";
    public static XsocketHanlder xsocketHanlder;
    private List<ConnectListener> mListeners = new ArrayList<>();

    public static synchronized SocketManager init() {
        if (mSocketManager == null) {
            mSocketManager = new SocketManager();
        }
        return mSocketManager;
    }

    /**
     * 设置事件监听
     * @param listener
     */
    public void addConnectionListener(ConnectListener listener){
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * 移除事件监听
     * @param listener
     */
    public void removeConnectionListener(ConnectListener listener){
        if (mListeners.contains(listener)){
            mListeners.remove(listener);
        }
    }

    /**
     * 链接成功
     */
    private void notifySucees(){
        for (ConnectListener listener : mListeners) {
            listener.connectSucees();
        }
    }

    /**
     * 链接错误
     */
    private void notifyError(Throwable e){
        for (ConnectListener listener : mListeners) {
            listener.connectError(e);
        }
    }

    /**
     * 断开链接
     */
    private void notifyDisconnect(){
        for (ConnectListener listener : mListeners) {
            listener.disconnect();
        }
    }

    public static SocketManager getSocketManager(XsocketHanlder xsocketHanlder) {
        SocketManager.xsocketHanlder = xsocketHanlder;
        return init();
    }

    /**
     * 总发送
     * @param params
     * @param SERVICE_CODE
     */
    public static void sendSocketParams(SocketParams params, int SERVICE_CODE) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (params != null)
                jsonObject.put("server_params", params.getParams());
            jsonObject.put("server_code", SERVICE_CODE);
            jsonObject.put("tag", SOCKET_TAG);
            jsonObject.put(SYSTEM_VERSION, osVersion);
            write(jsonObject.toString());
            if (SERVICE_CODE == 100) {
            } else {
                LogUtils.json(jsonObject.toString());
            }
        } catch (JSONException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 最终写法
     * @param params
     */
    private static void write(String params) {
        try {
            mNbc.write(params);
            mNbc.write(SmartControlClient.BYDELIMITER);
            mNbc.flush();
        } catch (Exception e) {
            LogUtils.d("SocketManager", "----------write" + e.toString());
        }
    }

    /**
     * 连接Socket
     */
    public synchronized void connect() {
        try {
            if (isConnected())
                disConnect();
            mNbc = new NonBlockingConnection(InetAddress.getByName(IP), PORT, xsocketHanlder, true, SmartControlClient.LOGIN_TIMEOUT);
            mNbc.setOption(INonBlockingConnection.SO_LINGER, "0");
            LogUtils.d(TAG, "----------connect" + IP + ":" + PORT);
            notifySucees();
        } catch (Exception e) {
            LogUtils.d(TAG, "----------connect" + e.toString());
            notifyError(e);
        }
    }

    /**
     * 断开Socket
     */
    public synchronized void disConnect() {
        if (mNbc != null && mNbc.isOpen())
            try {
                LogUtils.d(TAG, "----------disConnect");
                notifyDisconnect();
                mNbc.close();
            } catch (IOException e) {
                LogUtils.d(TAG, "----------disConnect" + e.toString());
                notifyError(e);
            }
    }

    /**
     * 判断Socket连接状态
     */
    public boolean isConnected() {
        if (mNbc != null && mNbc.isOpen()) {
            return true;
        }
        return false;
    }

    public interface ConnectListener {
        void connectSucees();
        void disconnect();
        void connectError(Throwable e);
    }

    public abstract static class SimpleConnectListener implements ConnectListener {
        @Override
        public void connectSucees() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void connectError(Throwable e) {

        }
    }
}
