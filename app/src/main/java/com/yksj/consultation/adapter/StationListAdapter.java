package com.yksj.consultation.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.StationBean;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.HStringUtil;

import java.util.ArrayList;

public class StationListAdapter extends BaseMultiItemQuickAdapter<StationBean, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     */
    public StationListAdapter() {
        super(new ArrayList());
        addItemType(StationType.STATION_HOME_CREATE, R.layout.item_create_station);
        addItemType(StationType.STATION_HOME_JOIN, R.layout.item_join_station);
        addItemType(StationType.STATION_HOME_RECOMMEND, R.layout.item_recommend_station);
    }

    @Override
    protected void convert(BaseViewHolder helper, StationBean item) {
        helper.setGone(R.id.top_layout, item.hasHead);
        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.ICON_DOCTOR_PICTURE;
        Picasso.with(Utils.getApp()).load(url).placeholder(R.drawable.default_head_doctor).into((ImageView) helper.getView(R.id.head));
        if (HStringUtil.isEmpty(item.SITE_HOSPOTAL)) {
            helper.setGone(R.id.hos, false);
        } else {
            helper.setVisible(R.id.hos, true);
            helper.setText(R.id.hos, item.SITE_HOSPOTAL);
        }
        helper.setText(R.id.office, new SpanUtils()
                .append(item.DOCTOR_NAME)
                .setForegroundColor(Utils.getApp().getResources().getColor(R.color.color_blue))
                .append(item.OFFICE_NAME)
                .create());
        helper.setText(R.id.tv_look_count, new SpanUtils()
                .append(item.VISIT_TIME)
                .setForegroundColor(Utils.getApp().getResources().getColor(R.color.red_text))
                .append("次浏览")
                .create());
        helper.setText(R.id.tv_doctors_count, new SpanUtils()
                .append(item.MEMBER_NUM)
                .setForegroundColor(Utils.getApp().getResources().getColor(R.color.red_text))
                .append("名医生")
                .create());
        helper.setText(R.id.tv_station_name, item.SITE_NAME);
    }
}
