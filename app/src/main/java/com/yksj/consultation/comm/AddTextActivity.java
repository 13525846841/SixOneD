package com.yksj.consultation.comm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.MaxLengthWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

;

/**
 * 群资料添加   及 记事本添加 输入提醒内容
 */
public class AddTextActivity extends BaseTitleActivity {

    private static Build mBuild;

    @BindView(R.id.et_text)
    EditText mContentView;

    @BindView(R.id.textcount)
    TextView textNum;//记事本输入文字数量提示
    private int textNumber = 0;

    public static Build from(Activity activity) {
        return mBuild = new Build(activity);
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_add_text;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setRight("确定", this::onRightClick);

        initViewByType();

        if (!HStringUtil.isEmpty(mBuild.content)) {
            mContentView.setText(mBuild.content);
        }

        if (mBuild.inputType != 0) {
            mContentView.setInputType(mBuild.inputType);
        }

        if (mBuild.maxLen != 0){
            mContentView.addTextChangedListener(new MaxLengthWatcher(mBuild.maxLen, mContentView));
        }

        if (!TextUtils.isEmpty(mBuild.title)) {
            setTitle(mBuild.title);
        }
    }

    private void onRightClick(View view) {
        String content = mContentView.getText().toString();
        if (mBuild.listener != null) {
            mBuild.listener.onConfrimClick(view, content, AddTextActivity.this);
        }
        onClickConfrim();
    }

    private void initViewByType() {
        if (mBuild.type == 0) {
            return;
        }
        switch (mBuild.type) {
            case 3:
                titleRightBtn2.setVisibility(View.VISIBLE);
                titleTextV.setText("输入提醒内容");
                textNum.setVisibility(View.VISIBLE);
                textNum.setText("0/20");
                mContentView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if ((start + count) <= 20 && mContentView.getText().toString().length() <= 20) {
                            textNumber = mContentView.getText().toString().length();
                            textNum.setText(textNumber + "/20");
                        } else {
                            mContentView.setText(s.subSequence(0, 20));
                            ToastUtil.showShort(AddTextActivity.this, "最多可输入20个字");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                mContentView.setHint("请输入内容(内容小于20字)");
                break;
            case 4:
                titleRightBtn2.setVisibility(View.VISIBLE);
                titleTextV.setText("回复评价");
                mContentView.addTextChangedListener(new MaxLengthWatcher(200, mContentView));
                break;
        }
    }

    private void onClickConfrim() {
        switch (mBuild.type) {
            case 3:
                String remingContent = mContentView.getText().toString();
                if (remingContent.length() > 20) {
                    ToastUtil.showShort("内容不能多于20个字");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("content", remingContent);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }



    public static class Build {
        //1 群名称 2群公告 3记事本内容 4回复评价 5医生集团加入申请
        private int type;
        private String content;
        private String title;
        private static Build INSTANCE;
        private int maxLen = 0;
        private int inputType;
        private OnAddTextClickListener listener;
        private WeakReference<Activity> activity;

        private Build(Activity activity) {
            INSTANCE = this;
            this.activity = new WeakReference<>(activity);
        }

        public static Build getInstance() {
            return INSTANCE;
        }

        public void startActivity() {
            if (activity.get() != null) {
                Intent intent = new Intent(activity.get(), AddTextActivity.class);
                activity.get().startActivity(intent);
            }
        }

        public void startActivityForResult(int requestCode) {
            if (activity.get() != null) {
                Intent intent = new Intent(activity.get(), AddTextActivity.class);
                activity.get().startActivityForResult(intent, requestCode);
            }
        }

        public Build setMaxLen(int maxLen) {
            this.maxLen = maxLen;
            return this;
        }

        public Build setListener(OnAddTextClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Build setTitle(String title) {
            this.title = title;
            return this;
        }

        public Build setInputType(int inputType) {
            this.inputType = inputType;
            return this;
        }

        public Build setContent(String content) {
            this.content = content;
            return this;
        }
    }

    public interface OnAddTextClickListener {
        void onConfrimClick(View v, String content, AddTextActivity activity);
    }
}
