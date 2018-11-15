package com.yksj.consultation.adapter;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.UnionMemberBean;
import com.yksj.consultation.sonDoc.R;
import com.library.base.utils.ResourceHelper;

public class UnionMemberListAdapte extends BaseQuickAdapter<UnionMemberBean, BaseViewHolder> {
    public UnionMemberListAdapte() {
        super(R.layout.item_union_member);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnionMemberBean item) {
        String avatarUrl = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.SITE_BIG_PIC;
        ImageLoader.load(avatarUrl).into(helper.getView(R.id.iv_avatar));
//        String coverUrl = AppContext.getmRepository().URL_QUERYHEADIMAGE_NEW + item.SITE_BIG_PIC;
        String coverUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531300878&di=02977db15b8c468c7b9f000853ed1de6&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.taopic.com%2Fuploads%2Fallimg%2F140731%2F235004-140I10KK371.jpg";
        ImageLoader.load(coverUrl).into(helper.getView(R.id.iv_cover));
        helper.setText(R.id.tv_title, String.format("%s%s", item.DOCTOR_NAME, item.OFFICE_NAME));
        helper.setText(R.id.tv_desc, item.SITE_NAME);
        helper.setText(R.id.tv_station_hospital, item.SITE_HOSPOTAL);
        helper.setText(R.id.tv_other_info, new SpanUtils()
                .append(String.valueOf(item.FOLLOW_COUNT))
                .setForegroundColor(ResourceHelper.getColor(R.color.color_blue))
                .append(" 关注")
                .append("  ·  ")
                .append(String.valueOf(item.VISIT_TIME))
                .setForegroundColor(ResourceHelper.getColor(R.color.color_blue))
                .append(" 浏览")
                .create());

    }
}
