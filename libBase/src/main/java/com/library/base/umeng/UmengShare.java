package com.library.base.umeng;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.R;
import com.library.base.utils.ResourceHelper;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseMediaObject;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 友盟分享
 */
public class UmengShare {

    private WeakReference<FragmentActivity> mActivity;
    private SHARE_MEDIA mShareMedia;

    private static UMShareListener DEFAULTSHARELISTENER = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            ToastUtils.showShort("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtils.showShort("分享失败");
            LogUtils.e(throwable.toString());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            ToastUtils.showShort("分享取消");
        }
    };

    public static void init(Application app) {
        UMConfigure.init(app
                , ResourceHelper.getString(R.string.umeng_app_key)
                , ResourceHelper.getString(R.string.umeng_channel)
                , UMConfigure.DEVICE_TYPE_PHONE
                , "");
        UMShareConfig config = new UMShareConfig();
        config.isOpenShareEditActivity(true);
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO); //如果有安装客户端优先客户端授权登录
        config.isNeedAuthOnGetUserInfo(true); //每次登录都重新授权
        UMShareAPI.get(app).setShareConfig(config);

        String wxAppKey = app.getResources().getString(R.string.wx_app_id);
        String wxAppSecret = app.getResources().getString(R.string.wx_app_secret);

        String qqAppKey = app.getResources().getString(R.string.qq_app_id);
        String qqAppSecret = app.getResources().getString(R.string.qq_app_key);

        String sinaAppKey = app.getResources().getString(R.string.sina_app_key);
        String sinaAppSecret = app.getResources().getString(R.string.sina_app_secret);

        PlatformConfig.setWeixin(wxAppKey, wxAppSecret);
        PlatformConfig.setQQZone(qqAppKey, qqAppSecret);
        PlatformConfig.setSinaWeibo(sinaAppKey, sinaAppSecret, "http://sns.whalecloud.com");
    }

    public static SHARE_MEDIA[] getPlatform() {
        ArrayList<SHARE_MEDIA> list = new ArrayList<>();
        list.add(SHARE_MEDIA.WEIXIN);
        list.add(SHARE_MEDIA.WEIXIN_CIRCLE);
        list.add(SHARE_MEDIA.QQ);
        list.add(SHARE_MEDIA.QZONE);
        list.add(SHARE_MEDIA.SINA);
        SHARE_MEDIA[] displaylist = new SHARE_MEDIA[list.size()];
        list.toArray(displaylist);
        return displaylist;
    }

    public UmengShare(Activity activity) {
        this.mActivity = new WeakReference(activity);
    }

    public static UmengShare from(Activity activity) {
        return new UmengShare(activity);
    }

    public ShareSpace share() {
        return new ShareSpace(this);
    }

    public ShareSpace share(SHARE_MEDIA shareMedia) {
        this.mShareMedia = shareMedia;
        return new ShareSpace(this);
    }

    public Activity getActivity() {
        return mActivity.get();
    }

    public SHARE_MEDIA getShareMedia() {
        return mShareMedia;
    }

    private void shareWeixin(ShareAction action) {
        if (isWechatAvilible(mActivity.get())) {
            action.setPlatform(SHARE_MEDIA.WEIXIN)
                    .share();
        } else {
            ToastUtils.showShort("微信未安装!");
        }
    }

    private void shareWeixinCircle(ShareAction action) {
        if (isWechatAvilible(mActivity.get())) {
            action.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .share();
        } else {
            ToastUtils.showShort("微信未安装!");
        }
    }

    private void shareQQ(ShareAction action) {
        if (isQQAvilible(mActivity.get())) {
            action.setPlatform(SHARE_MEDIA.QQ)
                    .share();
        } else {
            ToastUtils.showShort("QQ未安装!");
        }
    }

    private void shareQzone(ShareAction action) {
        if (isQQAvilible(mActivity.get())) {
            action.setPlatform(SHARE_MEDIA.QZONE)
                    .share();
        } else {
            ToastUtils.showShort("QQ未安装!");
        }
    }

    private void shareSina(ShareAction action) {
        if (isSinaAvilible(mActivity.get())) {
            action.setPlatform(SHARE_MEDIA.SINA)
                    .share();
        } else {
            ToastUtils.showShort("新浪微博未安装!");
        }
    }

    public void startShare(SHARE_MEDIA shareMedia, String title, String content, BaseMediaObject media, UMShareListener listener) {
        if (mActivity.get() == null) {
            return;
        }
        ShareAction share = new ShareAction(mActivity.get());

        if (TextUtils.isEmpty(title)) {
            title = "title";
        }
        if (TextUtils.isEmpty(content)) {
            content = "content";
        }
        if (listener == null) {
            listener = DEFAULTSHARELISTENER;
        }

        share.setDisplayList(getPlatform())
                .withText(content)
                .setCallback(listener);

        media.setTitle(title);
        media.setDescription(content);
        if (media instanceof UMImage) {
            share.withMedia((UMImage) media);
        } else if (media instanceof UMusic) {
            share.withMedia((UMusic) media);
        } else if (media instanceof UMVideo) {
            share.withMedia((UMVideo) media);
        } else if (media instanceof UMWeb) {
            share.withMedia((UMWeb) media);
        }

        switch (shareMedia){
            case WEIXIN:
                shareWeixin(share);
                break;
            case WEIXIN_CIRCLE:
                shareWeixinCircle(share);
                break;
            case QQ:
                shareQQ(share);
                break;
            case QZONE:
                shareQzone(share);
                break;
            case SINA:
                shareSina(share);
                break;
        }
    }

    /**
     * 微信是否可用
     * @param context
     * @return
     */
    private static boolean isWechatAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (int i = 0; i < installedPackages.size(); i++) {
                PackageInfo packageInfo = installedPackages.get(i);
                String packageName = packageInfo.packageName;
                if (packageName.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * QQ是否可用
     * @param context
     * @return
     */
    private static boolean isQQAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo packageInfo : installedPackages) {
                String packageName = packageInfo.packageName;
                if (packageName.equalsIgnoreCase("com.tencent.qqlite") || packageName.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新浪是否可用
     * @param context
     * @return
     */
    private static boolean isSinaAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo packageInfo : installedPackages) {
                String packageName = packageInfo.packageName;
                if (packageName.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }
}