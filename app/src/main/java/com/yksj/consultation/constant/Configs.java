package com.yksj.consultation.constant;

import com.yksj.consultation.sonDoc.BuildConfig;

/**
 * Created by hekl on 15/12/23.
 * Used for socket,http地址端口链接
 */
public class Configs {
    //六一正式
//    public static int SOCKET_PORT = 8014;//Socket端口
//    public static String SOCKET_IP = "www.61120.cn";
//    public static String WEB_IP = "https://www.61120.cn";

//    public static int SOCKET_PORT = 8014;//Socket端口
//    public static String SOCKET_IP = "www.61120.vip";
//    public static String WEB_IP = "https://www.61120.vip";

//    六一测试
//    public static int SOCKET_PORT = 8015;//Socket端口
//    public static String SOCKET_IP = "61.jiansion.com";
//    public static String WEB_IP = "https://61.jiansion.com";

    //本地测试
//    public static int SOCKET_PORT = 8014;//Socket端口
//    public static String SOCKET_IP = "www.61120.cn";
//    public static String WEB_IP = "https://www.61120.cn";

    public static int SOCKET_PORT = BuildConfig.SOCKET_PORT;//Socket端口
    public static String SOCKET_IP = BuildConfig.SOCKET_IP; //Socket IP
    public static String WEB_IP = BuildConfig.WEB_IP;//服务器地址

    //分享地址
    public static String SHARE_WEB = BuildConfig.SHARE_WEB;//六一健康分享地址
}
