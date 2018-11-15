package com.library.base.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;

import com.blankj.utilcode.util.Utils;

/**
 * 资源文件帮助类
 */
public class ResourceHelper {

    public static Resources getResource(){
        return Utils.getApp().getResources();
    }

    public static String getString(@StringRes int resId){
        return getResource().getString(resId);
    }

    public static int getDimens(@DimenRes int resId){
        return getResource().getDimensionPixelSize(resId);
    }

    public static Bitmap getBitmap(int resId){
        return BitmapFactory.decodeResource(getResource(), resId);
    }

    public static int getColor(int resId){
        return getResource().getColor(resId);
    }

    public static Drawable getDrawable(int resId){
        return getResource().getDrawable(resId);
    }

    public static String[] getStringArray(int resId){
        return getResource().getStringArray(resId);
    }
}
