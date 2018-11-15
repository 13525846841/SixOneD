package com.yksj.consultation.adapter;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.DoctorShareCommentBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.SpannableClickable;

public class DoctorShareListCommentAdapter extends BaseQuickAdapter<DoctorShareCommentBean, BaseViewHolder> {

    private OnShareListCommentAdapterClickListener mListener;

    public DoctorShareListCommentAdapter() {
        super(R.layout.item_comment2);
    }

    public void setListener(OnShareListCommentAdapterClickListener listener){
        this.mListener = listener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final DoctorShareCommentBean item) {
        int nameColor = helper.itemView.getContext().getResources().getColor(R.color.color_blue);
        TextView commentContent = helper.getView(R.id.commentTv);
        SpannableStringBuilder commentStr;
        if (item.isReply()) {//回复评论
            commentStr = createReplyComment(item, nameColor);
        } else {//普通评论
            commentStr = createNormalComment(item, nameColor);
        }
        commentContent.setText(commentStr);
        commentContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 创建回复评论spannable
     *
     * @param item
     * @param nameColor
     * @return
     */
    private SpannableStringBuilder createReplyComment(DoctorShareCommentBean item, int nameColor) {
        return new SpanUtils()
                .append(item.CUSTOMER_NAME)
                .setClickSpan(new SpannableClickable() {
                    @Override
                    public void onClick(View widget) {
                        if (mListener != null) {
                            mListener.onUserClick(widget, item.CUSTOMER_ID);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(nameColor);
                    }
                })
                .append("回复")
                .append(item.COMMENT_CUSTOMER_NAME)
                .setClickSpan(new SpannableClickable() {
                    @Override
                    public void onClick(View widget) {
                        if (mListener != null) {
                            mListener.onUserClick(widget, item.COMMENT_CUSTOMER_ID);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(nameColor);
                    }
                })
                .append(String.format("：%s", item.COMMENT_CONTENT))
                .create();
    }

    /**
     * 创建不同评论spannable
     *
     * @param item
     * @param nameColor
     * @return
     */
    private SpannableStringBuilder createNormalComment(DoctorShareCommentBean item, int nameColor) {
        return new SpanUtils()
                .append(item.CUSTOMER_NAME)
                .setClickSpan(new SpannableClickable() {
                    @Override
                    public void onClick(View widget) {
                        if (mListener != null) {
                            mListener.onUserClick(widget, item.CUSTOMER_ID);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(nameColor);
                    }
                })
                .append(String.format("：%s", item.COMMENT_CONTENT))
                .create();
    }

    public interface OnShareListCommentAdapterClickListener{
        void onUserClick(View view, String doctorId);
    }
}
