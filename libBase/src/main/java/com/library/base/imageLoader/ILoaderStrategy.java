package com.library.base.imageLoader;

import android.graphics.Bitmap;

public interface ILoaderStrategy {
    /**
     * 加载图片
     * @param options
     */
    void loadImage(LoaderOptions options);
    Bitmap donwloadImage(LoaderOptions options);
    /**
     * 清理内存缓存
     */
    void clearMemoryCache();
    /**
     * 清理磁盘缓存
     */
    void clearDiskCache();
}
