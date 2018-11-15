package com.library.base.umeng;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.library.base.dialog.ShareDialog;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseMediaObject;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;

import java.io.File;

public class ShareSpace {
    private String title;
    private String content;
    private UmengShare umengShare;
    private UMShareListener listener;
    private BaseMediaObject media;
    private UMImage thumb;

    public ShareSpace(UmengShare umengShare) {
        this.umengShare = umengShare;
    }

    public ShareSpace setThumb(Object obj) {
        Activity activity = umengShare.getActivity();
        if (obj instanceof String) {
            thumb = new UMImage(activity, ((String) obj));
        } else if (obj instanceof File) {
            thumb = new UMImage(activity, ((File) obj));
        } else if (obj instanceof Integer) {
            thumb = new UMImage(activity, ((Integer) obj));
        } else if (obj instanceof Bitmap) {
            thumb = new UMImage(activity, ((Bitmap) obj));
        } else if (obj instanceof byte[]) {
            thumb = new UMImage(activity, ((byte[]) obj));
        } else {
            throw new IllegalArgumentException("图片地址错误");
        }
        if (media != null) {
            media.setThumb(thumb);
        }
        return this;
    }

    public ShareSpace setImage(Object obj) {
        Activity activity = umengShare.getActivity();
        if (obj instanceof String) {
            media = new UMImage(activity, ((String) obj));
        } else if (obj instanceof File) {
            media = new UMImage(activity, ((File) obj));
        } else if (obj instanceof Integer) {
            media = new UMImage(activity, ((Integer) obj));
        } else if (obj instanceof Bitmap) {
            media = new UMImage(activity, ((Bitmap) obj));
        } else if (obj instanceof byte[]) {
            media = new UMImage(activity, ((byte[]) obj));
        } else {
            throw new IllegalArgumentException("图片地址错误");
        }
        if (thumb != null) {
            media.setThumb(thumb);
        }
        return this;
    }

    public ShareSpace setMusic(String path) {
        media = new UMusic(path);
        return this;
    }

    public ShareSpace setVideo(String path) {
        media = new UMVideo(path);
        return this;
    }

    public ShareSpace setUrl(String url) {
        media = new UMWeb(url);
        return this;
    }

    public ShareSpace setTitle(String title) {
        this.title = title;
        return this;
    }

    public ShareSpace setContent(String content) {
        this.content = content;
        return this;
    }

    public ShareSpace setListener(UMShareListener listener) {
        this.listener = listener;
        return this;
    }

    public void startShare() {
        umengShare.startShare(umengShare.getShareMedia(), title, content, media, listener);
    }

    public void showDialog(FragmentActivity activity) {
        final ShareDialog shareDialog = ShareDialog.newInstance();
        shareDialog.setListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onWechatClick(ShareDialog dialog) {
                umengShare.startShare(SHARE_MEDIA.WEIXIN, title, content, media, listener);
                shareDialog.dismiss();
            }

            @Override
            public void onWechatPyqClick(ShareDialog dialog) {
                umengShare.startShare(SHARE_MEDIA.WEIXIN_CIRCLE, title, content, media, listener);
                shareDialog.dismiss();
            }

            @Override
            public void onQQClick(ShareDialog dialog) {
                umengShare.startShare(SHARE_MEDIA.QQ, title, content, media, listener);
                shareDialog.dismiss();
            }

            @Override
            public void onQQZoneClick(ShareDialog dialog) {
                umengShare.startShare(SHARE_MEDIA.QZONE, title, content, media, listener);
                shareDialog.dismiss();
            }

            @Override
            public void onSinClick(ShareDialog dialog) {
                umengShare.startShare(SHARE_MEDIA.SINA, title, content, media, listener);
                shareDialog.dismiss();
            }
        });
        shareDialog.show(activity.getSupportFragmentManager());
    }
}