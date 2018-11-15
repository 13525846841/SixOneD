package com.library.base.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.library.base.imageLoader.ImageLoader;

public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
