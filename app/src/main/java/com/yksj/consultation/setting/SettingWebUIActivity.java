package com.yksj.consultation.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.DialogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * web页面
 * getIntent().getStringExtra("url")   地址 http://www.baidu.com
 * getIntent().getStringExtra("title")  title标题
 *
 * @author root
 */
public class SettingWebUIActivity extends BaseActivity {

    private WebView mWebView;
    private WebSettings settings;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting_web_ui);
        mWebView = (WebView) findViewById(R.id.webview);
        settings = mWebView.getSettings();
        initView();
    }

    void initView() {
        initializeTitle();
        int textSize = getIntent().getIntExtra("TextSize", 100);//网页大小
        initTextSize(textSize);
        settings.setTextSize(TextSize.SMALLEST);
        settings.setJavaScriptEnabled(true);
        titleTextV.setText(getIntent().getStringExtra("title"));
        titleLeftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_activity_close_enter,R.anim.anim_activity_close_exit);
            }
        });


//        mWebView.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                SettingWebUIActivity.this.setProgress(progress * 1000);
//            }
//
//        });
//        settings.setSupportZoom(true);
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView arg0, String arg1, Bitmap arg2) {
//                super.onPageStarted(arg0, arg1, arg2);
//                try {
//                    showDialog(1);
//                } catch (Exception e) {
//                }
//            }
//
//            @Override
//            public void onPageFinished(WebView arg0, String arg1) {
//                super.onPageFinished(arg0, arg1);
//                try {
//                    removeDialog(1);
//                } catch (Exception e) {
//                }
//            }
//        });
        String url = getIntent().getStringExtra("url");
        if (!url.startsWith("http")) url = "http://" + url;
        LogUtils.e(url);
        mWebView.loadUrl(url, ApiService.getDefaultHeaders());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    private void initTextSize(int textSize) {
        // TODO Auto-generated method stub
        Object[] objs = TextSize.class.getEnumConstants();
        for (Object obj : objs) {
            Method m;
            try {
                m = obj.getClass().getDeclaredMethod("values", new Class<?>[0]);
                Object[] results = (Object[]) m.invoke(obj, new Object[0]);
                Object objOne = results[0];
                Field code = objOne.getClass().getDeclaredField("value");
                code.setAccessible(true);
                code.set(objOne, textSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 重写onKeyDown
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack())) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int arg0) {
        Dialog dialog = DialogUtils.getLoadingDialog(this, getResources().getString(R.string.dataload));
        return dialog;
    }
}