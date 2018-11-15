package com.library.base.imageLoader;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;

public class GlideLoader implements ILoaderStrategy {

    @Override
    public void loadImage(LoaderOptions options) {
        GlideRequest request = null;
        if (options.asBitmap) {
            request = GlideApp.with(ActivityUtils.getTopActivity()).asBitmap().load(options.model);
        } else if (options.asGif) {
            request = GlideApp.with(Utils.getApp()).asGif().load(options.model);
        } else {
            request = GlideApp.with(Utils.getApp()).load(options.model);
        }
        if (options.placeholderRes != 0) {
            request.placeholder(options.placeholderRes);
        }
        if (options.errorRes != 0) {
            request.error(options.errorRes);
        }
        if (options.fallback != 0) {
            request.fallback(options.fallback);
        }
        if (options.skipMemoryCache) {
            request.skipMemoryCache(true);
        }
        if (options.skipDiskCache){
            request.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        if (options.targetWidth != 0 && options.targetHeight != 0) {
            request.override(options.targetWidth, options.targetHeight);
        }
        if (options.dontAnimate) {
            request.dontAnimate();
        }
        if (options.skipDiskCache) {
            request.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        if (options.fitCenter) {
            request.fitCenter();
        }
        if (options.centerCrop) {
            request.centerCrop();
        }
        if (options.thumbnail != 0) {
            request.thumbnail(options.thumbnail);
        }
        request.into(options.targetView);
    }

    @Override
    public Bitmap donwloadImage(final LoaderOptions options) {
        Bitmap bitmap = null;
        try {
            GlideRequest<Bitmap> request = GlideApp.with(Utils.getApp())
                    .asBitmap()
                    .load(options.model);
            FutureTarget<Bitmap> submit;
            if (options.targetWidth != 0 && options.targetHeight != 0) {
                submit = request.submit(options.targetWidth, options.targetHeight);
            }else{
                submit = request.submit();
            }
            bitmap = submit.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void clearMemoryCache() {
        Glide.get(Utils.getApp()).clearMemory();
    }

    @Override
    public void clearDiskCache() {
        Glide.get(Utils.getApp()).clearDiskCache();
    }
}
