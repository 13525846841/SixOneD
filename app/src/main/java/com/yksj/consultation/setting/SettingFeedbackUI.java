package com.yksj.consultation.setting;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.MessageDialog;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

/**
 * 意见反馈
 * @author Administrator
 */
public class SettingFeedbackUI extends BaseTitleActivity implements OnClickListener {
    private EditText mEditText;
    private int textNumber = 0;
    TextView textNum = null;
    IntentFilter filter_faceback;//广播注册

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.setting_layout_feedback);

        initView();
    }

    private void initView() {
        setTitle("提交");
        setRight("提交", new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        mEditText = (EditText) findViewById(R.id.setting_feedback_text);
        mEditText.addTextChangedListener(textWatcher);
        textNum = (TextView) findViewById(R.id.textcount);
        textNum.setText("0/500");
        filter_faceback = new IntentFilter();
        filter_faceback.addAction("com.yksj.ui.ACTION_SEND_FACEBACK");
    }

    /**
     * 文字监听
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ((start + count) <= 500 && mEditText.getText().toString().length() <= 500) {
                textNumber = mEditText.getText().toString().length();
                textNum.setText(textNumber + "/500");
            } else {
                mEditText.setText(s.subSequence(0, 500));
                ToastUtil.showShort(SettingFeedbackUI.this, R.string.most_five_hundred);
            }
        }
    };

    /**
     * 提交意见内容
     */
    private void submitFeedback() {
        WheelUtils.hideInput(SettingFeedbackUI.this, mEditText.getWindowToken());
        String value = mEditText.getEditableText().toString().trim();
        if ("".equals(value)) {
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), getString(R.string.opinion_null));
            return;
        }
        ApiService.OKHttpSaveFeedBackHZ(value, new ApiCallbackWrapper<ResponseBean<String>>() {
            @Override
            public void onResponse(ResponseBean<String> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    MessageDialog.newInstance("", response.result)
                            .addListener(new MessageDialog.SimpleMessageDialogListener() {
                                @Override public void onPositiveClick(MessageDialog dialog, View v) {
                                    SettingFeedbackUI.this.finish();
                                }
                            }).show(SettingFeedbackUI.this.getSupportFragmentManager());
                } else {
                    ToastUtil.showShort(getApplicationContext(), R.string.request_error);
                }
            }
        }, this);
    }
}
