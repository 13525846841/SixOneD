package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.DoctorToolsBean;

public class DoctorToolsListAdapter extends BaseQuickAdapter<DoctorToolsBean, BaseViewHolder> {

    public DoctorToolsListAdapter() {
        super(R.layout.item_doctor_tools);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorToolsBean item) {
        helper.setText(R.id.tv_tools_name, item.TOOL_NAME);
    }
}
