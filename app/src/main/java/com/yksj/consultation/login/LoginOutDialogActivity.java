package com.yksj.consultation.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

/**
 * 异地登录的Activity
 * Created by lmk on 2015/8/20.
 */
public class LoginOutDialogActivity extends FragmentActivity implements View.OnClickListener {

    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login_out_dialog);
        setFinishOnTouchOutside(false);
        tvContent = (TextView) findViewById(R.id.dialog_text);
        tvContent.setText(R.string.login_agin);
        TextView button = (TextView) findViewById(R.id.dialog_btn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(LoginOutDialogActivity.this, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginOutDialogActivity.this, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
