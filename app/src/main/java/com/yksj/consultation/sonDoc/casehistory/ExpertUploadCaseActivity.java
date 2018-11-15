package com.yksj.consultation.sonDoc.casehistory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * 专家上传病历选择界面
 * Created by lmk on 15/10/14.
 */
public class ExpertUploadCaseActivity extends BaseActivity implements View.OnClickListener{

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, ExpertUploadCaseActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_expert_uplaod_case);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("上传病历");
        findViewById(R.id.expert_upload_case_from_me).setOnClickListener(this);
        findViewById(R.id.expert_upload_case_manualy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.expert_upload_case_from_me:
                intent=new Intent(ExpertUploadCaseActivity.this,MyCaseListActivity.class);
                startActivity(intent);
                break;
            case R.id.expert_upload_case_manualy:
                ToastUtil.showShort(ExpertUploadCaseActivity.this,"暂未开通");
                break;
        }
    }
}
