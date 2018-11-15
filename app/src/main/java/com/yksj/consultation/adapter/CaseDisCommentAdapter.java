package com.yksj.consultation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.CommentBean;

import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;
import org.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 病历讨论评论适配器
 * Created by lmk on 2015/9/17.
 */
public class CaseDisCommentAdapter extends SimpleBaseAdapter<CommentBean> {

    private ImageLoader mInstance;
    DisplayImageOptions options;
    public CaseDisCommentAdapter(Context context) {
        super(context);
        mInstance=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder(context)
                .cacheInMemory()//设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.default_head_mankind)//设置图片Uri为空或是错误的时候显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .displayer(new RoundedBitmapDisplayer(10))//是否设置为圆角，弧度为多少
                .build();
    }

    @Override
    public int getItemResource() {
        return R.layout.case_dis_comment_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        CommentBean cb=datas.get(position);
        TextView name=(TextView) holder.getView(R.id.case_dis_comment_item_name);
        ImageView imageView=(ImageView) holder.getView(R.id.case_dis_comment_item_img);
        TextView content=(TextView) holder.getView(R.id.case_dis_comment_item_content);
        name.setText(cb.REAL_NAME);
        content.setText(cb.COMMENT_CONTENT);
        imageView.setImageResource(R.drawable.default_head_female);
        mInstance.displayImage(cb.CLIENT_ICON_BACKGROUND,imageView,options);
        return convertView;
    }
}
