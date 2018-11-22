package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.bean.FollowTemplateSubBean;
import com.yksj.consultation.sonDoc.R;

/**
 * Created by ${chen} on 2017/4/26.
 */
public class SeeTemplateAdapter extends BaseQuickAdapter<FollowTemplateSubBean, BaseViewHolder> {

    public SeeTemplateAdapter() {
        super(R.layout.item_see_temp);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowTemplateSubBean item) {
        String time = "";
        if ("10".equals(item.followSubTimeType)) {
            time = item.timeTypeCount + "天";
        } else if ("20".equals(item.followSubTimeType)) {
            time = item.timeTypeCount + "周";
        } else if ("30".equals(item.followSubTimeType)) {
            time = item.timeTypeCount + "月";
        } else if ("40".equals(item.followSubTimeType)) {
            time = item.timeTypeCount + "年";
        }
        if (item.followSeq == 0) {
            ((SuperTextView) helper.getView(R.id.time_stv)).setRightString(time);
        }
        ((SuperTextView) helper.getView(R.id.time_stv)).setLeftString(item.followSeq == 0 ? "首次" : "距上次");
        ((SuperTextView) helper.getView(R.id.time_stv)).setRightString(time);
        ((SuperTextView) helper.getView(R.id.action_stv)).setRightString(item.followContent);
    }
}
