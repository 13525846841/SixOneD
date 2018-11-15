package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.UnionBean;
import com.yksj.consultation.sonDoc.R;

public class UnionListAdapter extends BaseQuickAdapter<UnionBean, BaseViewHolder> {

    public UnionListAdapter() {
        super(R.layout.item_union);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnionBean item) {
        String url  = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.BACKGROUND;
        ImageLoader.loadUnitCover(url).into(helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title, item.UNION_NAME);
        helper.setText(R.id.tv_desc, item.BE_GOOD);
        helper.setText(R.id.tv_station_num, String.format("工作站%s", item.SITE_COUNT));
    }
}
