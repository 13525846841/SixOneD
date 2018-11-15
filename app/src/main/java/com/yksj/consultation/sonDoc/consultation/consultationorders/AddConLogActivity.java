package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddConLogActivity extends BaseActivity {

    public static final String CONID = "CONID";
    private EditText mEditText;
    public String conID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_con_log);
        initView();
    }

    private void initView() {
        initializeTitle();
        if (getIntent().hasExtra(CONID))
            conID = getIntent().getStringExtra(CONID);
        titleTextV.setText("添加接诊日志");
        titleLeftBtn.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.et_text);
        findViewById(R.id.sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.sure://确定
                getData();
                break;
        }
    }


    /**
     * 添加日志
     */
    private void getData() {
        String content = mEditText.getText().toString();
        if (HStringUtil.isEmpty(content)) {
            ToastUtil.showShort("内容不能为空");
            return;
        }
        if (HStringUtil.isEmpty(conID)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("consultation_id", conID);
        map.put("doctor_id", DoctorHelper.getId());
        map.put("op", "addLog");
        map.put("log_content", content);
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                        ToastUtil.showShort(obj.optString("message"));
                        if (HttpResult.SUCCESS.endsWith(obj.optString("code"))) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, this);
    }

    @Override
    public void onBackPressed() {
        if (!HStringUtil.isEmpty(mEditText.getText().toString())) {
            DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "确定要退出编辑吗？", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                @Override
                public void onDismiss(DialogFragment fragment) {

                }

                @Override
                public void onClick(DialogFragment fragment, View v) {
                    AddConLogActivity.super.onBackPressed();
                }
            });
        } else {
            super.onBackPressed();
        }

    }
}
