package com.library.base.imageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class LoaderOptions<T> {
    @DrawableRes
    protected int placeholderRes;
    @DrawableRes
    protected int errorRes;
    protected int fallback;
    protected WeakReference<Context> context;
    protected boolean centerCrop;
    protected boolean fitCenter;
    protected boolean dontAnimate;
    protected boolean skipMemoryCache; //是否缓存内存
    protected boolean skipDiskCache;
    protected Bitmap.Config config = Bitmap.Config.RGB_565;
    protected boolean asBitmap;
    protected boolean asGif;
    protected int targetWidth;
    protected int targetHeight;
    protected boolean crossFade;
    protected float thumbnail;
    protected float bitmapAngle; //圆角角度
    protected float degrees; //旋转角度.注意:picasso针对三星等本地图片，默认旋转回0度，即正常位置。此时不需要自己rotate
    protected ImageView targetView;//targetView展示图片
    T model;

    public LoaderOptions(T model) {
        this.model = model;
    }

    public LoaderOptions placeholder(@DrawableRes int placeholder) {
        this.placeholderRes = placeholder;
        return this;
    }

    public LoaderOptions error(@DrawableRes int error) {
        this.errorRes = error;
        return this;
    }

    public LoaderOptions fallback(@DrawableRes int fallback){
        this.fallback = fallback;
        return this;
    }

    public LoaderOptions centerCrop() {
        this.centerCrop = true;
        return this;
    }

    public LoaderOptions fitCenter() {
        this.fitCenter = true;
        return this;
    }

    public LoaderOptions dontAnimate() {
        this.dontAnimate = true;
        return this;
    }

    public LoaderOptions skipMemoryCache() {
        this.skipMemoryCache = true;
        return this;
    }

    public LoaderOptions skipDiskCache() {
        this.skipDiskCache = true;
        return this;
    }

    public LoaderOptions asBitmap() {
        this.asBitmap = true;
        return this;
    }

    public LoaderOptions asGif() {
        this.asGif = true;
        return this;
    }

    public LoaderOptions override(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        return this;
    }

    public LoaderOptions crossFade() {
        this.crossFade = true;
        return this;
    }

    public Bitmap getImage(int width, int height){
        this.targetWidth = width;
        this.targetHeight = height;
        return ImageLoader.getInstance().donwloadImage(this);
    }

    public void into(ImageView targetView) {
        this.targetView = targetView;
        ImageLoader.getInstance().loadOptions(this);
    }

    public Bitmap submit(){
        return ImageLoader.getInstance().donwloadImage(this);
    }
}
