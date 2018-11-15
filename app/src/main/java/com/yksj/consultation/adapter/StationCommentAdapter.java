package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.StationCommentBean;

public class StationCommentAdapter extends BaseQuickAdapter<StationCommentBean, BaseViewHolder> {

    public StationCommentAdapter() {
        super(R.layout.item_station_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, StationCommentBean item) {
        helper.setText(R.id.tv_name, String.format("%s：", item.CUSTOMER_NAME));
        helper.setText(R.id.tv_content, item.EVALUATE_CONTENT);
    }
}
