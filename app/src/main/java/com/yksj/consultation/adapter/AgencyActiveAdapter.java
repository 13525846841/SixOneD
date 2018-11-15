package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.agency.constant.AgencyCategroy;
import com.yksj.consultation.agency.constant.AgencyCategroy.Categroy;
import com.yksj.consultation.bean.AgencyActiveBean;
import com.yksj.consultation.sonDoc.R;

public class AgencyActiveAdapter extends BaseQuickAdapter<AgencyActiveBean, BaseViewHolder> {

    private String categroy;

    public AgencyActiveAdapter(@Categroy String categroy) {
        super(R.layout.item_agency_active);
        this.categroy = categroy;
    }

    @Override
    protected void convert(BaseViewHolder helper, AgencyActiveBean item) {
        // 是否可编辑
        helper.setVisible(R.id.active_alter, AgencyCategroy.SELF.equals(categroy));
        helper.setText(R.id.active_name, item.ACTIV_TITLE);
        helper.setText(R.id.active_time, item.ACTIV_TIME_DESC);
        helper.setText(R.id.active_content, item.ACTIV_DESC);
    }
}
