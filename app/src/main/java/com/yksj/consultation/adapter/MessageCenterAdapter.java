package com.yksj.consultation.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.MessageCenterBean;
import com.yksj.consultation.sonDoc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MessageCenterAdapter extends BaseQuickAdapter<MessageCenterBean, BaseViewHolder> {

    public MessageCenterAdapter() {
        super(R.layout.item_message_center);
    }

    @Override
    protected void convert(BaseViewHolder holder, MessageCenterBean item) {
        String picturePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.INFO_PICTURE;
        ImageView avatarView = holder.getView(R.id.iv_avatar);
        ImageLoader
                .load(picturePath)
                .placeholder(R.drawable.waterfall_default)
                .into(avatarView);
        holder.setText(R.id.tv_title, item.INFO_NAME);

        try {
            long time = new SimpleDateFormat("yyyyMMddHHmmss").parse(item.PUBLISH_TIME).getTime();
            holder.setText(R.id.tv_time, TimeUtils.getFriendlyTimeSpanByNow(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
