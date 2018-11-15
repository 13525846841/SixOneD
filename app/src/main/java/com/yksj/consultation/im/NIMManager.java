package com.yksj.consultation.im;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.library.base.utils.ResourceHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.yksj.consultation.app.AppContext;
import com.library.base.dialog.ConfirmDialog;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.main.MainActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.AVChatActivity;
import com.yksj.consultation.sonDoc.chatting.avchat.AVChatProfile;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.DemoCache;
import com.yksj.consultation.sonDoc.chatting.avchat.common.NimUIKit;
import com.yksj.consultation.sonDoc.chatting.avchat.init.UserPreferences;
import com.yksj.consultation.sonDoc.chatting.avchat.receiver.PhoneCallStateObserver;
import com.yksj.consultation.sonDoc.chatting.avchat.team.SessionHelper;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatHelper;
import com.yksj.healthtalk.utils.LogUtil;

import com.library.base.utils.StorageUtils;

import java.util.Map;

public class NIMManager {

    public static void init(Application application){
        DemoCache.setContext(application);
        NIMClient.init(application, null, getDefaultoptions());
        if (ProcessUtils.isMainProcess()) {
            // 初始化UIKit模块
            NimUIKit.init(application);

            // 会话窗口的定制初始化。
            SessionHelper.init();
            TeamAVChatHelper.sharedInstance().registerObserver(true);

            // 注册通知消息过滤器
            registerIMMessageFilter();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

            // 注册白板会话
//            registerRTSIncomingObserver(true);

//            // 注册语言变化监听
//            registerLocaleReceiver(true);
            // 注册网络通话来电
            registerAVChatIncomingCallObserver(true);
        }
    }

    /**
     * 如果返回值为 null，则全部使用默认参数。
     * @return
     */
    private static SDKOptions getDefaultoptions() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        // 配置保存图片，文件，log 等数据的目录
        // 如果 getDefaultoptions 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        String sdkPath = StorageUtils.getImPath(); // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = ScreenUtils.getScreenWidth() / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
                return BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.ic_launcher);
            }
        };
        return options;
    }

    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private static void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                    if (message.getAttachment() instanceof UpdateTeamAttachment) {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                            if (field.getKey() == TeamFieldEnum.ICON) {
                                return true;
                            }
                        }
                    } else if (message.getAttachment() instanceof AVChatAttachment) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private static void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                        || AVChatProfile.getInstance().isAVChatting()
                        || TeamAVChatHelper.sharedInstance().isTeamAVChatting()
                        || AVChatManager.getInstance().getCurrentChatId() != 0) {
                    LogUtil.i("InitBusiness", "reject incoming call data =" + data.toString() + " as local phone is not idle");
                    AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
                    return;
                }
                // 有网络来电打开AVChatActivity
                AVChatProfile.getInstance().setAVChatting(true);
                AVChatProfile.getInstance().launchActivity(data, AVChatActivity.FROM_BROADCASTRECEIVER);
            }
        }, register);
    }

    /**
     * 登陆
     * @param account
     * @param token
     */
    public static void doLogin(FragmentActivity activity, String account, String token){
        LoginInfo loginInfo = new LoginInfo(account, token, ResourceHelper.getString(R.string.nim_appkey));
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(new RequestCallback() {
            @Override
            public void onSuccess(Object param) {
                DemoCache.setAccount(account);
            }

            @Override
            public void onFailed(int code) {
                showLoginError(activity, loginInfo);
            }

            @Override
            public void onException(Throwable exception) {
                showLoginError(activity, loginInfo);
            }
        });
    }

    /**
     * 登出
     */
    public static void doLogout(){
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * IM登陆失败
     * @param activity
     * @param loginInfo
     */
    private static void showLoginError(FragmentActivity activity, LoginInfo loginInfo) {
        DialogManager.getConfrimDialog("IM登陆失败！")
                .setActionText("重试", "退出")
                .addListener(new ConfirmDialog.SimpleConfirmDialogListener(){
                    @Override
                    public void onPositiveClick(ConfirmDialog dialog, View v) {
                        super.onPositiveClick(dialog, v);
                        doLogin(activity, loginInfo.getAccount(), loginInfo.getToken());
                    }

                    @Override
                    public void onNegativeClick(ConfirmDialog dialog, View v) {
                        super.onNegativeClick(dialog, v);
                        AppContext.exitApp();
                    }
                })
                .show(activity.getSupportFragmentManager());
    }
}
