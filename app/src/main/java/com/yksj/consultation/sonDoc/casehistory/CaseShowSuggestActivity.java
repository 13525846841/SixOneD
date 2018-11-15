package com.yksj.consultation.sonDoc.casehistory;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * 查看会诊意见界面
 * Created by lmk on 15/10/14.
 */
public class CaseShowSuggestActivity extends BaseActivity {

    private TextView tvExpert,tvContent;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_case_expert_suggest);
        initView();

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("专家意见");
        titleLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvExpert= (TextView) findViewById(R.id.case_expert_suggest_name);
        tvContent= (TextView) findViewById(R.id.case_expert_suggest_content);
        tvExpert.setText(getIntent().getStringExtra("expert"));
        tvContent.setText(getIntent().getStringExtra("suggest"));
    }
}
