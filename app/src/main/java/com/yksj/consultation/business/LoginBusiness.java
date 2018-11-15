package com.yksj.consultation.business;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.event.EOffsiteLogin;
import com.library.base.utils.AESUtils;
import com.library.base.utils.BeanCacheHelper;
import com.library.base.utils.EventManager;
import com.orhanobut.logger.Logger;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.SettingManager;
import com.yksj.consultation.bean.DoctorInfoBean;
import com.yksj.consultation.bean.LoginBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.event.EDoctorLogin;
import com.yksj.consultation.im.NIMManager;
import com.yksj.consultation.service.CoreService;
import com.yksj.consultation.sonDoc.chatting.avchat.login.LogoutHelper;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.bean.LoginStatus;
import com.yksj.healthtalk.db.ChatUserHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.manager.SocketManager;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.socket.IMManager;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.net.socket.SocketParams;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.DialogUtils;
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ThreadManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.functions.Consumer;

/**
 * 登录,退出,
 * @author jack_tang
 */
public class LoginBusiness extends IMManager {
    private static final String TAG = LoginBusiness.class.getSimpleName();
    public boolean isVisitor = true;//默认是游客
    private String userName = "";//登录名
    private String password = "";//登录密码
    private String userId = "";//用户id
    private String loginType = "";
    private CustomerInfoEntity mLoginEntity = null;//用户实例
    public LoginStatus mLoginState = LoginStatus.NONE;//登录状态-1未登录,0登录中,1登录成功,2资料加载完成
    private SocketManager manager;
    private static final int LOGIN_TIME_OUT = 2000 * 10;
    private WeakReference<OnLogingCallback> mWeakCallback;
    private static LoginBusiness INSTANCE = null;
    private Subscription mTimerSubscription;
    private SocketManager.SimpleConnectListener mConnectListener = new SocketManager.SimpleConnectListener() {
        @Override
        public void connectError(Throwable e) {
            super.connectError(e);
            stopTimeOutTask();
            if (mWeakCallback != null && mWeakCallback.get() != null) {
                mWeakCallback.get().onLoginError(new IllegalStateException("服务器链接失败"));
            }
        }
    };

    public static LoginBusiness getInstance() {
        if (INSTANCE == null) {
            synchronized (LoginBusiness.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoginBusiness();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 接口名称：192.168.16.45:8899/DuoMeiHealth/    server_code  1001
     * 入参json：
     * USERID		第三方授权id
     * loginType	登录方式0正常登录 /1第三方登录
     * ACCOUNT		账号或手机
     * PASSWORD	密码
     */
    public void login(String userName, String password, String loginType, OnLogingCallback callback) {
        this.userName = userName;
        this.password = password;
//        this.userId = userId;
        this.loginType = loginType;
        this.mWeakCallback = new WeakReference(callback);
        manager = SocketManager.init();
        manager.addConnectionListener(mConnectListener);
        login(SmartControlClient.LOGIN_CODE);
    }

    /**
     * 登陆
     */
    public void login() {
        login(SmartControlClient.LOGIN_CODE);
    }

    /**
     * 重新登陆
     */
    public void replyLogin() {
        login(SmartControlClient.REPLY_LOGIN_CODE);
    }

    @SuppressLint("MissingPermission")
    public void login(int code) {
        if (!networkAvilible()) {
            return;
        }
        SmartControlClient.getControlClient().setUserPassword(userName, password);
        mLoginState = LoginStatus.LOGINING;
        if (manager.isConnected()) {
            startTimeOutTask();
            SocketParams params = new SocketParams();
            params.put("ACCOUNT", userName);
            params.put("PASSWORD", MD5Utils.getMD5(password));
            params.put("USERID", "");
            params.put("PLATFORM_NAME", "");
            params.put("FLAG", loginType);
            params.put("client_type", AppContext.CLIENT_TYPE);
            params.put("CLIENTONLY", PhoneUtils.getDeviceId());
            String IS_AFTER_MINIMIZATION_LOGIN = mLoginState == LoginStatus.LOGIN_OK ? "1" : "0";
            params.put("IS_AFTER_MINIMIZATION_LOGIN", IS_AFTER_MINIMIZATION_LOGIN);
            SocketManager.sendSocketParams(params, code);
        } else {
            ThreadManager.getInstance().createShortPool().execute(new Runnable() {
                @Override
                public void run() {
                    manager.connect();
                }
            });
        }
    }

    /**
     * 网络是否可用
     * @return
     */
    private boolean networkAvilible() {
        if (!NetworkUtils.isConnected()) {
            if (mWeakCallback != null && mWeakCallback.get() != null) {
                mWeakCallback.get().onLoginError(new NetworkErrorException("网络链接失败"));
            }
            ToastUtils.showShort("网络链接失败");
            return false;
        }
        return true;
    }

    /**
     * 退出
     */
    public synchronized void loginOut() {
        if (mLoginState == LoginStatus.LOGINING) {
            return;
        }
        NIMManager.doLogout();
        mLoginEntity = null;
        isVisitor = true;//设为游客
        SharePreHelper.updateLoginState(false);
        AppContext.clearAll();
        mLoginState = LoginStatus.NONE;
        DoctorHelper.quit();
        BeanCacheHelper.remove(ctx, LoginBean.class);
        SettingManager.destory();
        ChatUserHelper.close();
        CoreService.actionLogout(ctx);
        manager.disConnect();
    }

    public synchronized void setLoginInfo(CustomerInfoEntity info) {
        if (info != null)
            this.mLoginEntity = info;
    }

    public CustomerInfoEntity getLoginEntity() {
        return mLoginEntity;
    }

    /**
     * jo.put("code", code);//成功 失败
     * jo.put("message", mesg);//提示消息
     * jo.put("server_params", result);//
     */
    public void dealLoginInfo(final JSONObject jo) {
        if (!jo.has("code")) {
            return;
        }
        int code = jo.optInt("code");
        if (code == 1) {//登陆成功
            dealSucees(jo);
            stopTimeOutTask();
        } else if (code == 0) {//登陆出错
            dealError(jo);
            stopTimeOutTask();
        } else if (code == 2) {//异地登陆
            dealOffsite(jo);
        }
        if (manager != null) {// 移除socket事件监听
            manager.removeConnectionListener(mConnectListener);
        }
    }

    /**
     * 异地登陆
     * @param jo
     */
    private void dealOffsite(JSONObject jo) {
        if (mLoginState == LoginStatus.LOGINING) {
            return;
        }
        LogUtils.d(TAG, "=====异地登录====");
        DialogUtils.showLoginOutDialog2(ctx);
        LogoutHelper.logout();
        mLoginState = LoginStatus.NONE;//改变状态
        loginOut();
        EventManager.post(new EOffsiteLogin());
        saveLogin(this.userName, this.password, LoginStatus.NONE);
    }

    /**
     * 登陆出错
     * @param jo
     */
    private void dealError(JSONObject jo) {
        try {
            if (jo.has("message")) {
                String message = jo.getString("message");
                ToastUtils.showShort(message);
                EventManager.post(new EDoctorLogin(message, LoginStatus.LOGIN_ERROR));
            }
            if (mWeakCallback != null && mWeakCallback.get() != null) {
                mWeakCallback.get().onLoginError(new IllegalStateException("登陆账号/密码错误"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (mWeakCallback != null && mWeakCallback.get() != null) {
                mWeakCallback.get().onLoginError(new IllegalArgumentException("参数错误"));
            }
        }
    }

    /**
     * 登陆成功
     * @param jo
     */
    private void dealSucees(JSONObject jo) {
        try {
            mLoginState = LoginStatus.LOGIN_OK;//改变状态
            EventManager.post(new EDoctorLogin("登陆成功", LoginStatus.LOGIN_OK));
            //保存登录用户信息
            saveLogin(this.userName, this.password, LoginStatus.LOGIN_OK);
            String parame = jo.optString("server_params");
            CustomerInfoEntity entity = DataParseUtil.jsonToCustomerInfo2(parame);
            DoctorInfoBean doctorInfoBean = GsonSerializer.deserialize(parame, DoctorInfoBean.class);
            if (entity != null) {
                DoctorHelper.saveLoginInfo(doctorInfoBean);
                this.mLoginEntity = entity;
                ApiService.addHttpHeader("username", mLoginEntity.getUsername());
                ApiService.addHttpHeader("password", this.password);
                ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
            }
            isVisitor = false;
            if (mWeakCallback != null && mWeakCallback.get() != null) {
                mWeakCallback.get().onLoginSucees();
                mWeakCallback.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventManager.post(new EDoctorLogin("登陆失败", LoginStatus.LOGIN_ERROR));
            LogUtils.e("登陆失败：" + e);
        }
    }

    /**
     * 缓存登陆信息
     */
    private void saveLogin(String account, String password, LoginStatus loginStatus) {
        LoginBean loginBean = new LoginBean();
        loginBean.account = account;
        loginBean.password = AESUtils.encrypt(LoginBean.KEY, password);
        loginBean.versionCode = AppUtils.getAppVersionCode();
        loginBean.loginState = loginStatus.status;
        BeanCacheHelper.save(ctx, loginBean);
    }

    @Override
    public void doOnStart() {
        Logger.d(TAG, "----------doOnStart");
    }

    @Override
    public void reset() {
        Logger.d(TAG, "----------doOnStart");
    }

    /**
     * 获得登录状态
     * @return
     */
    public LoginStatus getLoginState() {
        return mLoginState;
    }

    /**
     * 开始登陆超时任务
     */
    private void startTimeOutTask() {
        Flowable.timer(LOGIN_TIME_OUT, TimeUnit.MILLISECONDS)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (mWeakCallback != null && mWeakCallback.get() != null) {
                            mWeakCallback.get().onLoginStart();
                        }
                    }
                })
                .subscribe(new FlowableSubscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mTimerSubscription = s;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (mWeakCallback != null && mWeakCallback.get() != null && mLoginState == LoginStatus.LOGINING) {
                            mLoginState = LoginStatus.NONE;
                            mWeakCallback.get().onLoginError(new TimeoutException("登陆超时，请稍后重试"));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mWeakCallback != null && mWeakCallback.get() != null) {
                            mWeakCallback.get().onLoginError(new IllegalStateException(t.getMessage()));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mWeakCallback != null && mWeakCallback.get() != null) {
                            mWeakCallback.get().onLoginFinish();
                        }
                    }
                });
    }

    /**
     * 停止登陆超时任务
     */
    private void stopTimeOutTask() {
        if (mWeakCallback != null && mWeakCallback.get() != null) {
            mWeakCallback.get().onLoginFinish();
        }
        if (mTimerSubscription != null) {
            mTimerSubscription.cancel();
            mTimerSubscription = null;
        }
    }

    public static abstract class SimpleLoginCallback implements OnLogingCallback {
        @Override
        public void onLoginStart() {
        }

        @Override
        public void onLoginSucees() {
        }

        @Override
        public void onLoginError(Exception e) {
        }

        @Override
        public void onLoginFinish() {
        }
    }

    public interface OnLogingCallback {
        void onLoginStart();

        void onLoginSucees();

        void onLoginError(Exception e);

        void onLoginFinish();
    }
}
