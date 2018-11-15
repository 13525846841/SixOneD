package com.library.base.okhttpIntercepter;


import android.util.Log;

import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 10/02/2017.
 */
class I {

    protected I() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        Logger logger = Logger.DEFAULT;
        switch (type) {
            case Platform.INFO:
                Log.i(tag, msg);
                break;
            default:
                Log.w(null, msg);
                break;
        }
    }
}
