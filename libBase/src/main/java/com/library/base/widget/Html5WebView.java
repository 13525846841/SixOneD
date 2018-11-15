package com.library.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.NetworkUtils;

import java.io.File;

/**
 * Wing_Li
 * 2016/9/9.
 */
public class Html5WebView extends WebView {

    private Context mContext;

    private OnPageStateChangedListener mPageStateChangedListener;

    public Html5WebView(Context context) {
        this(context, null);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings mWebSettings = getSettings();
//        mWebSettings.setDefaultFontSize(14);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setDisplayZoomControls(false);

        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应内容大小
        //缓存数据
        saveData(mWebSettings);
        newWin(mWebSettings);
        setWebChromeClient(webChromeClient);
        setWebViewClient(webViewClient);
    }

    /**
     * 监听页面加载状态变化
     * @param listener
     */
    public void setPageStateChangedListener(OnPageStateChangedListener listener) {
        this.mPageStateChangedListener = listener;
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * HTML5数据存储
     */
    @SuppressLint("MissingPermission")
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        if (NetworkUtils.isConnected()) {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }
        File cacheDir = mContext.getCacheDir();
        if (cacheDir != null) {
            String appCachePath = cacheDir.getAbsolutePath();
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setDatabaseEnabled(true);
            mWebSettings.setAppCacheEnabled(true);
            mWebSettings.setAppCachePath(appCachePath);
        }
    }

    public void setupBody(String title, String avatarPath, String autherName, String time, String content) {
        String body = createBody(title, content);
        loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
    }

    private String createBody(String title, String content) {
        return String.format(
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Title</title>"
                        + "<link type=\"text/css\" href=\"file:///android_asset/article.css\" rel=\"stylesheet\">"
                        + "</head>"
                        + "<body>"
                        + "<div id=\"article\">"
                        + "    <div class=\"article-note\">"
                        + "        <h1 class=\"article-title\">%s</h1>"
                        + "        <div class=\"article-content\">%s</div>"
                        + "    </div>"
                        + "</div>"
                        + "</body>"
                        + "</html>"
                , title, content);
    }

    WebViewClient webViewClient = new WebViewClient() {
        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mPageStateChangedListener != null) {
                mPageStateChangedListener.onStart();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mPageStateChangedListener != null) {
                mPageStateChangedListener.onFinish();
            }
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (mPageStateChangedListener != null) {
                mPageStateChangedListener.onProgress(newProgress);
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebViewTransport transport = (WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
        //=========多窗口的问题==========================================================
    };

    public interface OnPageStateChangedListener {
        void onStart();

        void onFinish();

        void onProgress(int progress);

        void onError();
    }
}
