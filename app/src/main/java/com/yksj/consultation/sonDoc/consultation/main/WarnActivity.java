package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

public class WarnActivity extends BaseActivity implements View.OnClickListener {

    private Button sure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("余额提现");
        titleLeftBtn.setOnClickListener(this);
        sure = (Button) findViewById(R.id.sure);
        sure.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.sure:
                finish();
                break;
        }
    }
}
