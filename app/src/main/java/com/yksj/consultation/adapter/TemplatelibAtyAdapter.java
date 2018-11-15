package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.FollowTemplateBean;
import com.yksj.consultation.sonDoc.R;


/**
 * Created by ${chen} on 2017/4/20.
 */
public class TemplatelibAtyAdapter extends BaseQuickAdapter<FollowTemplateBean, BaseViewHolder> {

    public TemplatelibAtyAdapter() {
        super(R.layout.item_futemp);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowTemplateBean item) {
        helper.setText(R.id.plan_name, item.name);
    }
}
