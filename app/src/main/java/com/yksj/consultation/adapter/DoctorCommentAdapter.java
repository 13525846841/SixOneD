package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.DoctorCommentListBean;
import com.yksj.consultation.bean.DoctorCommentListBean.CommentListBean;
import com.yksj.consultation.sonDoc.R;

public class DoctorCommentAdapter extends BaseQuickAdapter<CommentListBean, BaseViewHolder> {

    public DoctorCommentAdapter() {
        super(R.layout.item_doctor_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentListBean item) {
        helper.setText(R.id.evaluate_con, item.COMMENT_RESULT);
        helper.setText(R.id.evaluate_name, item.REAL_NAME);
        helper.setText(R.id.evaluate_name, item.REAL_NAME);
//        holder.evaluateStar.setRating(Float.parseFloat(map.get("SERVICE_LEVEL")));
    }
}
