package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.bean.DoctorServiceBean;

import java.util.ArrayList;

public class StationMemDetDuctorServiceAdapter extends BaseQuickAdapter<DoctorServiceBean, BaseViewHolder> {

    public StationMemDetDuctorServiceAdapter() {
        super(R.layout.item_station_mem_det_doctor_service, new ArrayList<DoctorServiceBean>());
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorServiceBean item) {
        helper.setText(R.id.tv_price, String.format("￥:%s/次", item.SERVICE_PRICE));
        helper.setText(R.id.tv_buy_count, String.format("共%s次购买", item.ORDER_NUM));

        if (ServiceType.TW.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.tv_title, "图文咨询");
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_tw);
        } else if (ServiceType.BY.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.tv_title, "包月咨询");
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_by);
        } else if (ServiceType.DH.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.tv_title, "电话咨询");
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_dh);
        } else if (ServiceType.SP.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.tv_title, "视频咨询");
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_sp);
        } else if (ServiceType.MZ.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.tv_title, "门诊预约");
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_mz);
        }
    }
}
