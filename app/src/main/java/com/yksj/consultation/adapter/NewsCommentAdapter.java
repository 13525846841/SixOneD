package com.yksj.consultation.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.NewsCommentBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.ViewHelper;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * Created by ${chen} on 2016/11/28.
 */
public class NewsCommentAdapter extends BaseQuickAdapter<NewsCommentBean, BaseViewHolder> {

    public NewsCommentAdapter() {
        super(R.layout.item_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsCommentBean item) {
        ViewHelper.setTextForView(helper.getView(R.id.case_dis_comment_item_name), TextUtils.isEmpty(item.customer.CUSTOMER_NICKNAME) ?
                item.customer.CUSTOMER_ACCOUNTS : item.customer.CUSTOMER_NICKNAME);
        ViewHelper.setTextForView(helper.getView(R.id.case_dis_comment_item_time), TimeUtil.format(item.COMMENT_TIME));
        ViewHelper.setTextForView(helper.getView(R.id.case_dis_comment_item_img), item.COMMENT_CONTENT);
        ViewHelper.setTextForView(helper.getView(R.id.case_dis_comment_item_content), item.COMMENT_CONTENT);
        //图片展示
        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.customer.CLIENT_ICON_BACKGROUND;
        ImageLoader.loadAvatar(url).into((ImageView) helper.getView(R.id.iv_avatar));
    }
}
