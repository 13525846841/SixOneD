package com.yksj.consultation.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.bean.CommentBean;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * Created by ${chen} on 2016/11/16.
 */
public class SubCommentAdapter extends BaseQuickAdapter<CommentBean, BaseViewHolder> {

    public SubCommentAdapter() {
        super(R.layout.item_assessed);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean item) {
        helper.setRating(R.id.rb_speed_assess, Float.parseFloat(item.EVALUATE_LEVEL), 1);
        helper.setText(R.id.tv_text,item.EVALUATE_CONTENT);
        helper.setText(R.id.assess_name, item.COMMENTER_NAME);
        helper.setText(R.id.assess_time, TimeUtil.format(item.EVALUATE_TIME));
        helper.setGone(R.id.ll_reply, TextUtils.isEmpty(item.REPLY_CONTENT));
        helper.setText(R.id.assess_text, item.REPLY_CONTENT);
      helper.addOnClickListener(R.id.txt_reply);

        //图片展示
        String avatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + item.BIG_ICON_BACKGROUND;
        ImageLoader.load(avatarPath).into((ImageView) helper.getView(R.id.image_head));
    }
}
