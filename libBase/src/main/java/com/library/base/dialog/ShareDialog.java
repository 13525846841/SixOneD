package com.library.base.dialog;

import android.os.Bundle;

import com.library.base.R;
import com.library.base.base.BaseDialog;
import com.library.base.base.ViewHolder;

/**
 * 底部分享dialog
 */
public class ShareDialog extends BaseDialog {

    private OnShareClickListener mListener;

    public static ShareDialog newInstance() {

        Bundle args = new Bundle();

        ShareDialog fragment = new ShareDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createContentLayoutRes() {
        return R.layout.dialog_share;
    }

    @Override
    public void convertView(ViewHolder holder, BaseDialog dialog) {
         setupParams(dialog);
        registerListener(holder);
    }

    private void setupParams(BaseDialog dialog) {
        dialog.setShowBottom(true);
    }

    private void registerListener(ViewHolder holder) {
        if (mListener == null) {
            return;
        }
        holder.setOnClickListener(R.id.wechat, v -> mListener.onWechatClick(ShareDialog.this));
        holder.setOnClickListener(R.id.wechat_pyq, v -> mListener.onWechatPyqClick(ShareDialog.this));
        holder.setOnClickListener(R.id.qq, v -> mListener.onQQClick(ShareDialog.this));
        holder.setOnClickListener(R.id.qq_zone, v -> mListener.onQQZoneClick(ShareDialog.this));
        holder.setOnClickListener(R.id.sin, v -> mListener.onSinClick(ShareDialog.this));
        holder.setOnClickListener(R.id.cancel, v -> dismiss());
    }

    public ShareDialog setListener(OnShareClickListener listener){
        this.mListener = listener;
        return this;
    }

    public interface OnShareClickListener{
        void onWechatClick(ShareDialog dialog);
        void onWechatPyqClick(ShareDialog dialog);
        void onQQClick(ShareDialog dialog);
        void onQQZoneClick(ShareDialog dialog);
        void onSinClick(ShareDialog dialog);
    }
}
