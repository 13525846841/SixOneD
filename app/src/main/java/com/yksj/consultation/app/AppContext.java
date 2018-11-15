package com.yksj.consultation.app;

import com.library.base.base.BaseApplication;
import com.library.base.event.EExitApp;
import com.library.base.utils.EventManager;
import com.yksj.consultation.business.InitBusiness;
import com.yksj.consultation.constant.Configs;
import com.yksj.consultation.service.CoreService;
import com.yksj.healthtalk.net.http.ApiRepository;
import com.yksj.healthtalk.net.http.ApiService;

public class AppContext extends BaseApplication {

    public static final String TAG = "AppContext";
    public static final String APP_CONSULTATION_CENTERID = "6";//六一健康id
    public static final String APP_VALID_MARK = "6010";
    public static final String CLIENT_TYPE = "6010";
    private static AppContext mApplication;

    // 系统所有http请求地址集合
    private static ApiRepository mApiRepository;

    // 全局数据
    private static AppData appData;
    private InitBusiness mInitBusiness;

    public void onCreate() {
        super.onCreate();
        mApplication = this;
        init();
    }

    private void init() {
        mInitBusiness = new InitBusiness();
        mInitBusiness.init(this);
        if (mApiRepository == null) mApiRepository = new ApiRepository(Configs.WEB_IP);
        if (appData == null) appData = new AppData();
        ApiService.setmRepository(mApiRepository);
        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
    }

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

    public static AppContext getHTalkApplication() {
        return mApplication;
    }

    public static AppData getAppData() {
        return appData;
    }

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
