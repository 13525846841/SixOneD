package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;

/**
 * Created by HEKL on 16/5/6.
 * Used for
 */

public class CommonwealAidAty extends BaseActivity implements View.OnClickListener {
    public static final String URL = "url";
    public static final String TITLE="TITLE";
    private WebView mWebView;
    String url = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_commonwealaid);

        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setVisibility(View.VISIBLE);
        titleLeftBtn.setOnClickListener(this);
        if (getIntent().hasExtra(TITLE))
            titleTextV.setText(getIntent().getStringExtra(TITLE));

        if (getIntent().hasExtra(URL))
            url = getIntent().getStringExtra(URL);
        mWebView = (WebView) findViewById(R.id.wv_web);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.myProgressBar);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        setWebStyle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    onBackPressed();
                }
                break;
        }
    }

    /**
     * WebView设置
     */
    private void setWebStyle() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.requestFocus();
        if (HStringUtil.isEmpty(url)) {
            url = ApiService.getRepository().PUBLICDONATE;
        }


        if (!url.startsWith("http")){
            mWebView.loadUrl("https://"+url);
        }else {
            mWebView.loadUrl(url);
        }
       // mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);

                return true;
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
