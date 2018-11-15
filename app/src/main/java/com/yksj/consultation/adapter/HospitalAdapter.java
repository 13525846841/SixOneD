package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.HospitalBean;
import com.yksj.consultation.sonDoc.R;

public class HospitalAdapter extends BaseQuickAdapter<HospitalBean, BaseViewHolder> {

    public HospitalAdapter() {
        super(R.layout.item_hospital);
    }

    @Override protected void convert(BaseViewHolder helper, HospitalBean item) {
        helper.setText(R.id.hospital_tv, item.UNIT_NAME);
    }
}
