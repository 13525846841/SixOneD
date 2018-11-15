package com.yksj.consultation.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.bean.StationDoctorBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;

/**
 * Created by ${chen} on 2017/7/7.
 */
public class QueryDoctorAdapter extends BaseQuickAdapter<StationDoctorBean, BaseViewHolder> {
    public QueryDoctorAdapter() {
        super(R.layout.item_invite_mem);
    }

    @Override
    protected void convert(BaseViewHolder helper, StationDoctorBean item) {
        ImageView custHaed = helper.getView(R.id.det_img_head);
        TextView custName = helper.getView(R.id.tv_doc_pro);
        TextView consultnum = helper.getView(R.id.tv_doc_place);

        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.BIG_ICON_BACKGROUND;
        ImageLoader.loadAvatar(url).into(custHaed);
        custName.setText(TextUtils.isEmpty(item.DOCTOR_REAL_NAME)? "匿名" : item.DOCTOR_REAL_NAME);
        consultnum.setText(String.format("简介: %s", item.INTRODUCTION));
        consultnum.setVisibility(TextUtils.isEmpty(item.INTRODUCTION) ? View.GONE : View.VISIBLE);
    }
}
