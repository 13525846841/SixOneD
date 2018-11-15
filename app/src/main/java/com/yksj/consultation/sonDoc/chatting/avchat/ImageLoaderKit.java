package com.yksj.consultation.sonDoc.chatting.avchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.library.base.imageLoader.ImageLoader;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.NimSingleThreadExecutor;
import com.yksj.consultation.sonDoc.chatting.avchat.common.NimUIKit;
import com.library.base.utils.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片缓存管理组件
 */
public class ImageLoaderKit {

    private static final String TAG = "ImageLoaderKit";

    private Context context;

    public ImageLoaderKit(Context context) {
        this.context = context;
    }

    /**
     * 清空图像缓存
     */
    public void clear() {
        NIMGlideModule.clearMemoryCache(context);
    }

    /**
     * 构建图像缓存
     */
    public void buildImageCache() {
        // clear avatar cache
        clear();

        // build self avatar cache
        List<String> accounts = new ArrayList<>(1);
        accounts.add(NimUIKit.getAccount());
        NimUIKit.getImageLoaderKit().buildAvatarCache(accounts);
    }

    private void buildAvatarCache(List<String> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        UserInfo userInfo;
        for (String account : accounts) {
            userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
            if (userInfo != null) {
                asyncLoadAvatarBitmapToCache(userInfo.getAvatar());
            }
        }

        LogUtils.i(TAG, "build avatar cache completed, avatar count=" + accounts.size());
    }

    /**
     * 获取通知栏提醒所需的头像位图，只存内存缓存/磁盘缓存中取，如果没有则返回空，自动发起异步加载
     * 注意：该方法在后台线程执行
     */
    public Bitmap getNotificationBitmapFromCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        final int imageSize = HeadImageView.DEFAULT_AVATAR_NOTIFICATION_ICON_SIZE;

        Bitmap bitmap = null;
        try {
            bitmap = ImageLoader
                    .load(url)
                    .getImage(imageSize, imageSize);
        } catch (Exception e) {
            bitmap = ResourceHelper.getBitmap(R.drawable.ic_launcher);
        }
        return bitmap;
    }

    /**
     * 异步加载头像位图到Glide缓存中
     */
    private void asyncLoadAvatarBitmapToCache(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final int imageSize = HeadImageView.DEFAULT_AVATAR_THUMB_SIZE;
        NimSingleThreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Glide.with(context)
                        .load(url)
                        .downloadOnly(imageSize, imageSize);
            }
        });
    }
}
