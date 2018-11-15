package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.IntentConstant;

/**
 * 邀请需知
 */
public class InviteKnowActivity extends BaseActivity {

    private String conId = "";//会诊id
    private String expId = "";//主诊专家id
    private String inviteInfo = "";//被邀请专家信息
    private String conName = "";//会诊名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_know);
        initView();
    }

    private void initView() {
        initializeTitle();

        if (getIntent().hasExtra(IntentConstant.ConsultationId)) {
            conId = getIntent().getStringExtra(IntentConstant.ConsultationId);
        }if (getIntent().hasExtra(IntentConstant.ConsultationName)) {
            conName = getIntent().getStringExtra(IntentConstant.ConsultationName);
        }
        if (getIntent().hasExtra(IntentConstant.ConsultationMainExpert)) {
            expId = getIntent().getStringExtra(IntentConstant.ConsultationMainExpert);
        }
        if (getIntent().hasExtra(IntentConstant.InvitedExpert)) {
            inviteInfo = getIntent().getStringExtra(IntentConstant.InvitedExpert);
        }
        titleTextV.setText("邀请需知");
        titleLeftBtn.setOnClickListener(this);
        findViewById(R.id.inviting).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.inviting:
                intent = new Intent(this, InviteActivity.class);
                intent.putExtra(IntentConstant.ConsultationId, conId);
                intent.putExtra(IntentConstant.ConsultationName, conName);
                intent.putExtra(IntentConstant.ConsultationMainExpert, expId);
                intent.putExtra(IntentConstant.InvitedExpert, inviteInfo);
                startActivity(intent);
                finish();
                break;
        }
    }
}
