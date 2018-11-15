package com.yksj.consultation.adapter;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.bean.DoctorServiceBean;

import java.util.ArrayList;

public class StationMemDetStaServiceAdapter extends BaseQuickAdapter<DoctorServiceBean, BaseViewHolder> {

    public StationMemDetStaServiceAdapter() {
        super(R.layout.item_station_mem_det_service, new ArrayList<DoctorServiceBean>());
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorServiceBean item) {
        helper.setText(R.id.tv_title, new SpanUtils()
                .append("向")
                .append(StringUtils.reverse(item.SITE_NAME))
                .setForegroundColor(Utils.getApp().getResources().getColor(R.color.color_blue))
                .append("咨询")
                .create());
        helper.setText(R.id.tv_price, String.format("￥:%s/次", item.SERVICE_PRICE));
        helper.setText(R.id.tv_buy_count, String.format("共%s次购买", item.ORDER_NUM));

        if (ServiceType.TW.equals(item.SERVICE_TYPE_ID)) {
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_tw);
        } else if (ServiceType.BY.equals(item.SERVICE_TYPE_ID)) {
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_by);
        } else if (ServiceType.DH.equals(item.SERVICE_TYPE_ID)) {
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_dh);
        } else if (ServiceType.SP.equals(item.SERVICE_TYPE_ID)) {
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_sp);
        } else if (ServiceType.MZ.equals(item.SERVICE_TYPE_ID)) {
            helper.setImageResource(R.id.iv_type, R.drawable.ic_service_mz);
        }
    }
}
