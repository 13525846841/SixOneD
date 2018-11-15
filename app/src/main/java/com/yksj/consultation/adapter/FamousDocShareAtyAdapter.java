package com.yksj.consultation.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.consultation.main.FamousDocShareAty;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.utils.Bimp;
import com.yksj.healthtalk.utils.ImageItem;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.CommentItem;
import com.yksj.healthtalk.views.CommentListView;
import com.yksj.healthtalk.views.User;
import com.yksj.healthtalk.views.mvp.presenter.CirclePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2017/4/25.
 * 名医分享适配器
 */
public class FamousDocShareAtyAdapter extends SimpleBaseAdapter<JSONObject> {

    public FamousDocShareAty context;
    //    public static boolean flag = true;
    public static final String IMAGEKEY = "image";//图片标志
    public MyShareOnClickListener listener;

    private CirclePresenter presenter;

    public FamousDocShareAtyAdapter(Context context, MyShareOnClickListener itemsOnClick) {
        super(context);
        this.context = (FamousDocShareAty) context;
        this.listener = itemsOnClick;
    }

    public void setCirclePresenter(CirclePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_share;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getItemView(final int position, final View convertView, SimpleBaseAdapter<JSONObject>.ViewHolder holder) {

        TextView name = holder.getView(R.id.text_doc_name);
        TextView office = holder.getView(R.id.text_share_office);
        TextView time = holder.getView(R.id.text_share_time);
        TextView delete = holder.getView(R.id.text_share_delete);
        TextView zanNumber = holder.getView(R.id.text_good_number);
        ImageView snsBtn = holder.getView(R.id.snsBtn);
        ImageView image_zan = holder.getView(R.id.image_zan);
        ImageView imageView = holder.getView(R.id.share_doctor_head);
        CommentListView commentList = holder.getView(R.id.commentList);
        LinearLayout digCommentBody = holder.getView(R.id.digCommentBody);
        LinearLayout caseImgLayout = holder.getView(R.id.fgt_case_img_layout);

        final TextView infoContent = holder.getView(R.id.text_content);//内容
        final TextView expand = holder.getView(R.id.text_expand);


        List<CommentItem> commentsDatas = null;

//        String headurl = AppContext.getmRepository().URL_QUERYHEADIMAGE_NEW + datas.get(position).optString("BIG_ICON_BACKGROUND");
//        Picasso.with(context).load(headurl).placeholder(R.drawable.default_head_doctor).into(imageView);
        boolean hasComment = false;
        try {
            hasComment = datas.get(position).getJSONArray("comment").length() > 0;
            commentsDatas = new ArrayList<>();
            for (int i = 0; i < datas.get(position).getJSONArray("comment").length(); i++) {
                JSONObject obj = datas.get(position).getJSONArray("comment").getJSONObject(i);
                CommentItem item = new CommentItem();
                item.setId(obj.optString("COMMENT_ID"));
                item.setContent(obj.optString("COMMENT_CONTENT"));
                item.setUser(new User(obj.optString("CUSTOMER_ID"), obj.optString("CUSTOMER_NAME"), obj.optString("CUSTOMER_ID")));
                item.setToReplyUser(new User(obj.optString("COMMENT_CUSTOMER_ID"), obj.optString("COMMENT_CUSTOMER_NAME"), obj.optString("CUSTOMER_ID")));
                commentsDatas.add(item);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hasComment) {//处理评论列表
            final List<CommentItem> finalCommentsDatas = commentsDatas;
            commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                @Override
                public void onItemClick(int commentPosition) {
                    CommentItem commentItem = finalCommentsDatas.get(commentPosition);
                    if (LoginBusiness.getInstance().getLoginEntity().getId().equals(commentItem.getUser().getId())) {//复制或者删除自己的评论

//                        InputDialog dialog = new InputDialog(context, presenter, commentItem, circlePosition);
//                        dialog.show();

                    } else {//回复别人的评论
                        context.setFlag(commentItem,position);
                        context.updateEditTextBodyVisible(View.VISIBLE);
//                        if (presenter != null) {
//                            CommentConfig config = new CommentConfig();
//                            config.circlePosition = circlePosition;
//                            config.commentPosition = commentPosition;
//                            config.commentType = CommentConfig.Type.REPLY;
//                            config.replyUser = commentItem.getUser();
//                            presenter.showEditTextBody(config);
//                        }
                    }

                }
            });
            commentList.setDatas(commentsDatas);
            digCommentBody.setVisibility(View.VISIBLE);

        } else {
            digCommentBody.setVisibility(View.GONE);
        }

        name.setText(datas.get(position).optString("CUSTOMER_NICKNAME"));
        office.setText(datas.get(position).optString("OFFICE_NAME"));
        zanNumber.setText(datas.get(position).optString("PRAISE_COUNT"));

        time.setText(TimeUtil.getTimeStr(datas.get(position).optString("PUBLIC_TIME")));
        String str = datas.get(position).optString("SHARE_CONTENT");

        infoContent.setText(str);
        if (str.length() > 50) {
            expand.setVisibility(View.VISIBLE);
        } else {
            expand.setVisibility(View.GONE);
        }
        boolean show = datas.get(position).optBoolean("show");
        if (show) {
            expand.setText("收回");
            infoContent.setEllipsize(null);//展开
            infoContent.setMaxLines(Integer.MAX_VALUE);
        } else {
            expand.setText("展开");
            infoContent.setEllipsize(TextUtils.TruncateAt.END);//缩回
            infoContent.setLines(2);
        }
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!datas.get(position).optBoolean("show")) {
                    try {
                        datas.get(position).put("show", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expand.setText("收回");
                    infoContent.setEllipsize(null);//展开
                    infoContent.setMaxLines(Integer.MAX_VALUE);
                } else {
                    try {
                        datas.get(position).put("show", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expand.setText("展开");
                    infoContent.setEllipsize(TextUtils.TruncateAt.END);//缩回
                    infoContent.setLines(2);
                }
            }
        });

        String shareId = datas.get(position).optString("SHARE_ID");

        final String finalShareId = shareId;
        if (DoctorHelper.getId().equals(datas.get(position).optString("CUSTOMER_ID"))) {
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {//删除
            @Override
            public void onClick(View v) {
                listener.onStarClick(finalShareId, R.id.text_share_delete, position);
            }
        });

        snsBtn.setOnClickListener(new View.OnClickListener() {//评论
            @Override
            public void onClick(View v) {
                listener.onStarClick(finalShareId, R.id.snsBtn, position);
            }
        });
        image_zan.setOnClickListener(new View.OnClickListener() {//点赞
            @Override
            public void onClick(View v) {
                if (datas.get(position).optInt("ISLIKE") > 0) {
                    ToastUtil.showShort(context, "已点过赞");
                } else {
                    listener.onStarClick(finalShareId, R.id.image_zan, position);
                }
            }
        });
        ArrayList<ImageItem> imagesList = new ArrayList<ImageItem>();//图片list类
        Bimp.dataMap.put(IMAGEKEY, imagesList);
        Bimp.imgMaxs.put(IMAGEKEY, 12);

        List<String> smallUrls = null;//小图片
        List<String> bigUrls = new ArrayList<>();//大图片
//        String pics = datas.get(position).optString("PICTURE_PATH");

//        String icons = datas.get(position).optString("PICTURE_PATH");

//        smallUrls = Arrays.asList(pics.split(","));
//        bigUrls = Arrays.asList(icons.split(","));
        int count = 0;
        caseImgLayout.setVisibility(View.GONE);
        try {

            count = datas.get(position).getJSONArray("picture").length();
            if (count > 0) {
                caseImgLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < count; i++) {
                    JSONObject obj = datas.get(position).getJSONArray("picture").getJSONObject(i);
                    bigUrls.add(obj.optString("PICTURE_PATH"));
                }
                String[] arrays = null;//病历图片
                //图片key集合
                arrays = new String[bigUrls.size()];
                for (int t = 0; t < bigUrls.size(); t++) {
                    arrays[t] = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + bigUrls.get(t);
                }
                caseImgLayout.removeAllViews();
                for (int i = 0; i < bigUrls.size(); i++) {
                    final int index = i;
                    View view = LayoutInflater.from(context).inflate(R.layout.aty_applyform_gallery, caseImgLayout, false);
                    ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                    String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + bigUrls.get(i);
                    Picasso.with(context).load(url).placeholder(R.drawable.waterfall_default).into(img);
                    final String[] finalArray = arrays;
                    img.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageGalleryActivity.class);
                            intent.putExtra(ImageGalleryActivity.URLS_KEY, finalArray);
                            intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                            intent.putExtra("type", 1);// 0,1单个,多个
                            intent.putExtra("position", index);
                            context.startActivity(intent);
                        }
                    });
                    caseImgLayout.addView(view);
                }
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    public interface MyShareOnClickListener extends View.OnClickListener {
        void onStarClick(String shareId, int id, int pos);
    }


}
