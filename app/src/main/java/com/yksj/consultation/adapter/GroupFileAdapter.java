package com.yksj.consultation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.GroupFileBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * Created by hekl on 17/1/6.
 * Used for
 */
public class GroupFileAdapter extends BaseQuickAdapter<GroupFileBean, BaseViewHolder> {

    public GroupFileAdapter() {
        super(R.layout.item_group_file);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupFileBean item) {
        if (item.fileType.equals("30")) {
            helper.setImageResource(R.id.image_view, R.drawable.file_mp4);
        } else if (item.fileType.equals("20")) {
            String imagePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + item.filePath;
            ImageLoader.load(imagePath).into(helper.getView(R.id.image_view));
        } else {
            helper.setImageResource(R.id.image_view, R.drawable.file_word);
        }
        helper.setText(R.id.file_name, item.fileName);
        String time = TimeUtil.format(item.time);
        helper.setText(R.id.upload_time, time);
    }
}
