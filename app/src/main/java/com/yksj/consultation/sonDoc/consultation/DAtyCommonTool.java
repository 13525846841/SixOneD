package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * Created by HEKL on 2015/7/15.
 * Used for
 */
public class DAtyCommonTool extends BaseActivity implements View.OnClickListener {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_commontool_layout);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        String url = getIntent().getStringExtra("URL");
        String name = getIntent().getStringExtra("NAME");
        titleTextV.setText(name);
        titleTextV.setSingleLine();
        titleTextV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleTextV.setMarqueeRepeatLimit(2);
        titleTextV.setFocusable(true);
        titleTextV.setFocusableInTouchMode(true);
        //启用支持javascript
        mWebView = (WebView) findViewById(R.id.wv_commontools);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }
}
