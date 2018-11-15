package com.library.base.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.library.base.R;

import butterknife.ButterKnife;

public class InputDialog extends DialogFragment {
    private EditText mContentView;
    private TextView mSendView;
    private String mContentHint;
    private OnInputClickListener mListener;
    private String mContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CommentDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_comment, container, false);
        mContentView = view.findViewById(R.id.circleEt);
        mSendView = view.findViewById(R.id.btn_send);
        mSendView.setOnClickListener(this::onClickSend);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSendView.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
                mContent = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mContentView, 0);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setupParams();
        if (!TextUtils.isEmpty(mContentHint)) {
            mContentView.setHint(mContentHint);
        }
    }

    private void setupParams() {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ScreenUtils.getScreenWidth();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    public InputDialog setContentHint(String hintStr){
        mContentHint = hintStr;
        if (mContentView != null){
            mContentView.setHint(hintStr);
        }
        return this;
    }

    /**
     * 点击发送
     *
     * @param v
     */
    public void onClickSend(View v) {
        if (mListener != null) {
            mListener.onClickSend(this, v, mContent);
        }
        dismiss();
        reset();
    }

    /**
     * 重置
     */
    public void reset() {
        mContentView.setText("");
        mSendView.setClickable(false);
        mContentView.setHint(mContentHint);
    }

    public InputDialog show(FragmentManager fm) {
        show(fm.beginTransaction(), String.valueOf(SystemClock.currentThreadTimeMillis()));
        return this;
    }

    public InputDialog setOnCommentClickListener(OnInputClickListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnInputClickListener {
        void onClickSend(InputDialog dialog, View view, String content);
    }
}
