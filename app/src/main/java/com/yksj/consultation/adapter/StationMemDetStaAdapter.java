package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.widget.ExpandableDescView;
import com.yksj.consultation.bean.StationBean;
import com.yksj.consultation.sonDoc.R;

public class StationMemDetStaAdapter extends BaseQuickAdapter<StationBean, BaseViewHolder> {

    public StationMemDetStaAdapter() {
        super(R.layout.item_station_mem_det_sta);
    }

    @Override
    protected void convert(BaseViewHolder helper, StationBean item) {
        helper.setText(R.id.tv_name, item.SITE_NAME);
        ExpandableDescView descView = helper.getView(R.id.desc_view);
        descView.setContent(item.SITE_DESC);
    }
}
