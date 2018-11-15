package com.library.base.dialog;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.view.View;

import com.library.base.R;
import com.library.base.base.BaseDialog;
import com.library.base.base.ViewHolder;
import com.library.base.utils.ResourceHelper;

public class ConfirmDialog extends BaseDialog {

    private static final String TITLE_EXTRA = "title_extra";
    private static final String MESSAGE_EXTRA = "message_extra";

    private SimpleConfirmDialogListener mListener;
    private String mPositiveText = "确认";
    private @ColorRes int mPositiveColor = R.color.dialog_button;
    private String mNegativeText = "取消";
    private @ColorRes int mNegativeColor = R.color.dialog_button;

    public static ConfirmDialog newInstance(String title, String message) {

        Bundle args = new Bundle();
        args.putString(TITLE_EXTRA, title);
        args.putString(MESSAGE_EXTRA, message);

        ConfirmDialog fragment = new ConfirmDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createContentLayoutRes() {
        return R.layout.dialog_message;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
        String title = getArguments().getString(TITLE_EXTRA);
        holder.setText(R.id.title, TextUtils.isEmpty(title) ? "提示" : title);
        String message = getArguments().getString(MESSAGE_EXTRA);
        holder.setText(R.id.message, TextUtils.isEmpty(message) ? "---" : message);
        holder.setOnClickListener(R.id.ok, this::onOkClick);
        holder.setOnClickListener(R.id.cancel, this::onCancelClick);
        holder.setText(R.id.ok, mPositiveText);
        holder.setTextColor(R.id.ok, ResourceHelper.getColor(mPositiveColor));
        holder.setText(R.id.cancel, mNegativeText);
        holder.setTextColor(R.id.cancel, ResourceHelper.getColor(mNegativeColor));

        // 设置左右边距
        setMargin(ResourceHelper.getDimens(R.dimen.dialog_marging));
        setOutCancel(false);
    }

    public ConfirmDialog addListener(SimpleConfirmDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    public ConfirmDialog setActionText(String positive, String negative) {
        this.mPositiveText = positive;
        this.mNegativeText = negative;
        return this;
    }

    public ConfirmDialog setPositive(String text, @ColorRes int color) {
        this.mPositiveText = text;
        this.mPositiveColor = color != 0 ? color : R.color.dialog_button;
        return this;
    }

    public ConfirmDialog setNegative(String text, @ColorRes int color) {
        this.mNegativeText = text;
        this.mNegativeColor = color != 0 ? color : R.color.dialog_button;
        return this;
    }

    private void onOkClick(View v) {
        if (mListener != null) {
            mListener.onPositiveClick(this, v);
        }
        dismissAllowingStateLoss();
    }

    private void onCancelClick(View v) {
        if (mListener != null) {
            mListener.onNegativeClick(this, v);
        }
        dismissAllowingStateLoss();
    }

    public interface OnConfrimDialogListener {
        void onPositiveClick(ConfirmDialog dialog, View v);

        void onNegativeClick(ConfirmDialog dialog, View v);
    }

    public static class SimpleConfirmDialogListener implements OnConfrimDialogListener {
        @Override
        public void onPositiveClick(ConfirmDialog dialog, View v) {

        }

        @Override
        public void onNegativeClick(ConfirmDialog dialog, View v) {

        }
    }
}
