package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.ResourceHelper;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * Created by ${chen} on 2017/7/5.
 */
public class LectureAdapter extends BaseQuickAdapter<LectureBean, BaseViewHolder> {

    public LectureAdapter() {
        super(R.layout.item_station_lectu_member);
    }

    @Override
    protected void convert(BaseViewHolder holder, LectureBean item) {
        String imagePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.SMALL_PIC;
        ImageLoader.load(imagePath).into(holder.getView(R.id.iv_cover));
        holder.setText(R.id.tv_title, item.COURSE_NAME);
        holder.setText(R.id.tv_name, item.COURSE_UP_NAME);
        holder.setText(R.id.tv_time, TimeUtil.getTimeStr(item.COURSE_UP_TIME));
        holder.setText(R.id.tv_price, item.isFree() ? "免费" : "付费");
        holder.setTextColor(R.id.tv_price,
                item.isFree() ? ResourceHelper.getColor(R.color.color_text_gray) : ResourceHelper.getColor(R.color.red_text));
    }
}
