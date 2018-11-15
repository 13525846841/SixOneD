package com.yksj.consultation.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.CommentPicture;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;
import java.util.List;

public class DoctorShareListPictureAdapter extends BaseQuickAdapter<CommentPicture, BaseViewHolder> {

    //最大图片数量
    private static final int MAX_PICTURE_SIZE = 6;

    public DoctorShareListPictureAdapter() {
        super(R.layout.item_doctor_share_picture, new ArrayList<>(MAX_PICTURE_SIZE));
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentPicture item) {
        ImageView pictureView = helper.getView(R.id.iv_picture);
        String pictureUrl = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.PICTURE_PATH;
        ImageLoader.load(pictureUrl).into(pictureView);
    }

    /**
     * 获取图片地址
     * @return
     */
    public List<String> getPicturePaths() {
        int count = getData().size();
        final List<String> pictures = new ArrayList<>(count);
        for (int j = 0; j < count; j++) {
            String picturePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + getItem(j).PICTURE_PATH;
            pictures.add(picturePath);
        }
        return pictures;
    }
}
