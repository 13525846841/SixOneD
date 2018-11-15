package com.library.base.dialog;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.view.View;

import com.library.base.R;
import com.library.base.base.BaseDialog;
import com.library.base.base.ViewHolder;
import com.library.base.utils.ResourceHelper;

public class MessageDialog extends BaseDialog {

    private static final String TITLE_EXTRA = "title_extra";
    private static final String MESSAGE_EXTRA = "message_extra";
    private SimpleMessageDialogListener mListener;
    private String mPositiveStr = "确定";
    private @ColorRes int mPositiveColor = R.color.dialog_button;

    public static MessageDialog newInstance(String title, String message) {

        Bundle args = new Bundle();
        args.putString(TITLE_EXTRA, title);
        args.putString(MESSAGE_EXTRA, message);

        MessageDialog fragment = new MessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createContentLayoutRes() {
        return R.layout.dialog_confrim;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        String title = getArguments().getString(TITLE_EXTRA);
        holder.setText(R.id.title, TextUtils.isEmpty(title) ? "提示" : title);
        String message = getArguments().getString(MESSAGE_EXTRA);
        holder.setText(R.id.message, TextUtils.isEmpty(message) ? "---" : message);
        holder.setOnClickListener(R.id.confirm_btn, this::onOkClick);
        holder.setText(R.id.confirm_btn, mPositiveStr);
        holder.setTextColor(R.id.confirm_btn, ResourceHelper.getColor(mPositiveColor));

        setMargin(ResourceHelper.getDimens(R.dimen.dialog_marging));
        setOutCancel(false);
    }

    public MessageDialog addListener(SimpleMessageDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    public MessageDialog setPositive(String str,@ColorRes int color) {
        this.mPositiveStr = str;
        this.mPositiveColor = color;
        return this;
    }

    private void onOkClick(View v) {
        if (mListener != null) {
            mListener.onPositiveClick(this, v);
        }
        dismissAllowingStateLoss();
    }

    public static interface SimpleMessageDialogListener {
        public void onPositiveClick(MessageDialog dialog, View v);
    }
}
