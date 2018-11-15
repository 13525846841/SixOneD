package com.yksj.consultation.login;

import android.content.Intent;
import android.os.Bundle;

import com.library.base.base.BaseActivity;
import com.yksj.healthtalk.utils.SharePreHelper;

/**
 * 初始化页面
 */
public class InitializeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        if (SharePreHelper.isFirstUse()) {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }
}