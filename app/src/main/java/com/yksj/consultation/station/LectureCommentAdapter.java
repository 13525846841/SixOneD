package com.yksj.consultation.station;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.LectureCommentBean;
import com.yksj.consultation.sonDoc.R;

public class LectureCommentAdapter extends BaseQuickAdapter<LectureCommentBean, BaseViewHolder> {

    public LectureCommentAdapter() {
        super(R.layout.item_lecture_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, LectureCommentBean item) {
        String avatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.CLIENT_ICON_BACKGROUND;
        ImageLoader.loadAvatar(avatarPath).into(helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_comment_content, item.EVALUATION_TYPE);
        helper.setText(R.id.tv_name, item.CUSTOMER_NICKNAME);
        helper.setText(R.id.tv_time_rating, String.format("%s · %s", item.EVALUATE_TIME, item.EVALUATE_LEVEL));
    }
}
