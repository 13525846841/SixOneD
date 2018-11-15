package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.bean.UnionIncidentBean;
import com.yksj.consultation.sonDoc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UnionIncidentAdapter extends BaseMultiItemQuickAdapter<UnionIncidentBean, BaseViewHolder> {
    public static final int LEFT_TYPE =  1;
    public static final int RIGHT_TYPE = LEFT_TYPE + 1;

    public UnionIncidentAdapter() {
        super(new ArrayList<>());
        addItemType(LEFT_TYPE, R.layout.item_union_incodent_left);
        addItemType(RIGHT_TYPE, R.layout.item_union_incodent_right);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return position % 2 == 0 ? LEFT_TYPE : RIGHT_TYPE;
    }

    @Override
    protected void convert(BaseViewHolder helper, UnionIncidentBean item) {
        helper.setText(R.id.tv_time, fromTime(item.EVENT_TIME));
        helper.setText(R.id.tv_incident, item.EVENT_TITLE);
        String imageUrl = "http://c.hiphotos.baidu.com/image/h%3D300/sign=45252389291f95cab9f594b6f9167fc5/72f082025aafa40f99d4e82aa764034f78f01932.jpg";
        ImageLoader.load(imageUrl).into(helper.getView(R.id.iv_cover));
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    private String fromTime(String time){
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(time);
            return new SimpleDateFormat("yyyy.MM.dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
