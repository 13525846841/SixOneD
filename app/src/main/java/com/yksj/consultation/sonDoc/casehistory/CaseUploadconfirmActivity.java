package com.yksj.consultation.sonDoc.casehistory;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.dialog.DialogManager;
import com.library.base.dialog.MessageDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

;

/**
 * 病历上传确认界面
 * Created by lmk on 15/10/15.
 */
public class CaseUploadconfirmActivity extends BaseTitleActivity implements View.OnClickListener {

    private EditText editName;
    private String recordId;

    @Override
    public int createLayoutRes() {
        return R.layout.aty_case_upload_confirm;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        recordId = getIntent().getStringExtra("recordId");
        initView();
    }

    private void initView() {
        setTitle("上传病历");
        editName = (EditText) findViewById(R.id.case_upload_name);
        setRight("上传", this::onUploadClick);
    }

    /**
     * 上传病例
     * @param v
     */
    private void onUploadClick(View v){
        String caseName = editName.getText().toString();
        if (TextUtils.isEmpty(caseName)){
            ToastUtils.showShort("请填写病例名称");
            return;
        }
        DialogManager.getMessageDialog("上传后病历将共享给所有专家医生,确认现在上传吗?")
                .addListener(new MessageDialog.SimpleMessageDialogListener(){
                    @Override
                    public void onPositiveClick(MessageDialog dialog, View v) {
                        requestUpload(caseName);
                    }
                })
                .show(getSupportFragmentManager());
    }

    /**
     * 共享病历
     */
    private void requestUpload(String caseName) {
        ApiService.OkHttpCaseUpload(caseName, recordId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()){
                    finish();
                }
                ToastUtils.showShort(response.message);
            }
        });
    }
}
