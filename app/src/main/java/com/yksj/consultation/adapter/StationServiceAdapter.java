package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.DoctorServiceBean;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;

import java.util.List;


public class StationServiceAdapter extends BaseQuickAdapter<DoctorServiceBean, BaseViewHolder> {

    public StationServiceAdapter(List<DoctorServiceBean> datas) {
        super(R.layout.item_station_price_list, datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorServiceBean item) {
        if (ServiceType.TW.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.item_name, "图文咨询");
            helper.setImageResource(R.id.image_addnum, R.drawable.ic_service_tw);
        } else if (ServiceType.BY.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.item_name, "包月咨询");
            helper.setImageResource(R.id.image_addnum, R.drawable.ic_service_by);
        } else if (ServiceType.DH.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.item_name, "电话咨询");
            helper.setImageResource(R.id.image_addnum, R.drawable.ic_service_dh);
        } else if (ServiceType.SP.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.item_name, "视频咨询");
            helper.setImageResource(R.id.image_addnum, R.drawable.ic_service_sp);
        } else if (ServiceType.MZ.equals(item.SERVICE_TYPE_ID)) {
            helper.setText(R.id.item_name, "门诊预约");
            helper.setImageResource(R.id.image_addnum, R.drawable.ic_service_mz);
        }
        helper.setText(R.id.picandcul_price, item.ORDER_ON_OFF == 1 ? String.format("%s元/次", item.SERVICE_PRICE) : "未开通");
    }
}
