package com.yksj.consultation.app;

import com.blankj.utilcode.util.Utils;
import com.library.base.base.BaseApplication;
import com.library.base.event.EExitApp;
import com.library.base.utils.EventManager;
import com.yksj.consultation.business.InitBusiness;
import com.yksj.consultation.constant.Configs;
import com.yksj.consultation.service.CoreService;
import com.yksj.healthtalk.net.http.ApiRepository;
import com.yksj.healthtalk.net.http.ApiService;

/**
 * 应用全局上下文
 */
public class AppContext extends BaseApplication {

    public static final String TAG = "AppContext";
    public static final String APP_CONSULTATION_CENTERID = "6";//六一健康id
    public static final String APP_VALID_MARK = "6010";
    public static final String CLIENT_TYPE = "6010";
    private static AppContext mApplication;

    // 系统所有http请求地址集合
    private static ApiRepository mApiRepository;

    // 全局数据
    @Deprecated
    private static AppData appData;

    public void onCreate() {
        super.onCreate();
        mApplication = this;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        InitBusiness initBusiness = new InitBusiness();
        initBusiness.init(this);
        if (mApiRepository == null) mApiRepository = new ApiRepository(Configs.WEB_IP);
        if (appData == null) appData = new AppData();
        ApiService.setmRepository(mApiRepository);
        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
    }

    /**
     * {@link Utils#getApp()}
     * @return
     */
    @Deprecated
    public static AppContext getApplication() {
        return mApplication;
    }

    /**
     * 登录清除
     */
    public static void clearAll() {
        if (appData != null) {
            appData.clearAll();
        }
    }

    /**
     * 历史遗留代码
     * 请使用{@link Utils#getApp()}
     * @return
     */
    @Deprecated
    public static AppContext getHTalkApplication() {
        return mApplication;
    }

    /**
     * 历史遗留代码，具体用途不清楚
     * @return
     */
    @Deprecated
    public static AppData getAppData() {
        return appData;
    }

    /**
     * 获取服务器接口类，该类中存有应用类所有接口
     * @return
     */
    public static ApiRepository getApiRepository() {
        return mApplication.mApiRepository;
    }

    /**
     * 退出App
     */
    public static void exitApp(){
        CoreService.actionStop(mApplication);
        EventManager.post(new EExitApp());
    }
}
