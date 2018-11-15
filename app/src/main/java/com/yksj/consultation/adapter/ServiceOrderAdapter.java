package com.yksj.consultation.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.ServiceOrderBean;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.ViewHelper;
import com.yksj.healthtalk.utils.TimeUtil;

public class ServiceOrderAdapter extends BaseQuickAdapter<ServiceOrderBean, BaseViewHolder> {
    private String mServiceType;
    private String mType;

    public ServiceOrderAdapter(String serviceType, String type) {
        super(R.layout.item_service_order);
        mServiceType = serviceType;
        mType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceOrderBean item) {
        ViewHelper.setTextForView(helper.getView(R.id.darkbacktext), item.getStatus(), true);
        helper.setGone(R.id.darkbacktext, !mServiceType.equals(ServiceType.TY));
        helper.getView(R.id.title).setVisibility(mType.equals("2") ? View.VISIBLE : View.GONE);
        ViewHelper.setTextForView(helper.getView(R.id.title), String.format("距离结束还有%s", item.LASTTIME), false);
        ViewHelper.setTextForView(helper.getView(R.id.tv_order_number), String.format("订单号: %s", item.PAY_ID), true);
        ViewHelper.setTextForView(helper.getView(R.id.tv_order_money), String.format("服务费用: %s元", item.SERVICE_GOLD), true);
        ViewHelper.setTextForView(helper.getView(R.id.tv_order_time), String.format("服务开始时间: %s", TimeUtil.getFormatDate(item.SERVICE_START)), true);
        ViewHelper.setTextForView(helper.getView(R.id.end_time), String.format("服务结束时间: %s", TimeUtil.getFormatDate(item.SERVICE_END)), true);
        ViewHelper.setTextForView(helper.getView(R.id.tv_circle), String.format("服务周期: %s", item.cycle), true);

        ServiceOrderBean.UserInfo userInfo = item.info;
        ViewHelper.setTextForView(helper.getView(R.id.tv_sex), userInfo.getUserSex(), true);
        ViewHelper.setTextForView(helper.getView(R.id.tv_year), String.format("%s岁", userInfo.AGE), true);
        String nameText = TextUtils.isEmpty(userInfo.CUSTOMER_NICKNAME) ? userInfo.CUSTOMER_ACCOUNTS : userInfo.CUSTOMER_NICKNAME;
        ViewHelper.setTextForView(helper.getView(R.id.name_tv), nameText, true);
        //图片展示
        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + userInfo.CLIENT_ICON_BACKGROUND;
        ImageLoader.load(url).into((ImageView) helper.getView(R.id.det_img_head));
    }
}
