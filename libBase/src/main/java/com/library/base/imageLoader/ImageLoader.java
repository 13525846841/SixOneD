package com.library.base.imageLoader;

import android.graphics.Bitmap;

import com.library.base.R;

public class ImageLoader {
    private ILoaderStrategy mLoaderStrategy;
    private static ImageLoader INSTANCE = null;

    private void ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (ImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    public static void setLoaderStrategy(ILoaderStrategy loaderStrategy) {
        getInstance().mLoaderStrategy = loaderStrategy;
    }

    public void loadOptions(LoaderOptions options) {
        this.mLoaderStrategy.loadImage(options);
    }

    public Bitmap donwloadImage(LoaderOptions options) {
        return this.mLoaderStrategy.donwloadImage(options);
    }

    /**
     * 清除内存缓存
     */
    public void clearMemoryCache() {
        this.mLoaderStrategy.clearMemoryCache();
    }

    /**
     * 清除文件缓存
     */
    public void clearDiskCache() {
        this.mLoaderStrategy.clearDiskCache();
    }

    public static <T> LoaderOptions<T> load(T model) {
        return new LoaderOptions(model)
                .placeholder(R.drawable.waterfall_default)
                .error(R.drawable.waterfall_default);
    }

    /**
     * 加载医生联盟封面图片
     * @param model
     * @param <T>
     * @return
     */
    public static <T> LoaderOptions<T> loadUnitCover(T model){
        return new LoaderOptions<>(model)
                .placeholder(R.drawable.ic_default_union_covert)
                .error(R.drawable.ic_default_union_covert);
    }

    /**
     * 加载用户头像方法
     * @param model String, byte[], File, Integer, Uri
     * @param <T>
     * @return
     */
    public static <T> LoaderOptions<T> loadAvatar(T model) {
        return new LoaderOptions(model)
                .placeholder(R.drawable.default_head_doctor)
                .error(R.drawable.default_head_doctor);
    }

    /**
     * 加载群头像
     * @param model
     * @param <T>
     * @return
     */
    public static <T> LoaderOptions<T> loadGroupImage(T model) {
        return new LoaderOptions(model)
                .placeholder(R.drawable.default_head_group)
                .error(R.drawable.default_head_group);
    }
}
