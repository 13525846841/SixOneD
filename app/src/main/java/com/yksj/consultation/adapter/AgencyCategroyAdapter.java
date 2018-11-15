package com.yksj.consultation.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.AgencyBean;
import com.yksj.consultation.sonDoc.R;

public class AgencyCategroyAdapter extends BaseQuickAdapter<AgencyBean, BaseViewHolder> {
    public AgencyCategroyAdapter(int itemRes) {
        super(itemRes);
    }

    @Override
    protected void convert(BaseViewHolder helper, AgencyBean item) {
        String picturePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + item.avatar;
        ImageLoader.load(picturePath).into(helper.getView(R.id.agency_cover));
        helper.setText(R.id.agency_name, item.name);
        helper.setText(R.id.agency_type, item.typeToString());
        helper.setText(R.id.agency_address, TextUtils.isEmpty(item.address) ? "火星" : item.address);
    }
}
