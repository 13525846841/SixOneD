package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.listener.OnClickAssessListener;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/16.
 */
public class    AssessAdapter extends BaseAdapter {
    private Context context;
    private Activity mActivity;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private List<JSONObject> mData = null;
    private OnClickAssessListener itemsOnClick;

    public AssessAdapter(Context context, OnClickAssessListener itemsOnClick) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mActivity = (Activity) context;
        mData = new ArrayList<>();
        this.itemsOnClick = itemsOnClick;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_assess, null);
            holder = new ViewHolder();

            holder.headView = (ImageView) convertView.findViewById(R.id.image_head);
            holder.name = (TextView) convertView.findViewById(R.id.assess_name);
            holder.time = (TextView) convertView.findViewById(R.id.assess_time);
            holder.text = (TextView) convertView.findViewById(R.id.tv_text);
            holder.reply = (LinearLayout) convertView.findViewById(R.id.ll_reply);
            holder.mBar = (RatingBar) convertView.findViewById(R.id.rb_speed_assess);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final int pos = position;
        final int id = 1;
        holder.mBar.setRating(Float.parseFloat(mData.get(position).optString("EVALUATE_LEVEL")));

        if (HStringUtil.isEmpty(mData.get(position).optString("EVALUATE_CONTENT"))){
            holder.text.setText("");
        }else {
            holder.text.setText(mData.get(position).optString("EVALUATE_CONTENT"));
        }

        holder.name.setText(mData.get(position).optString("COMMENTER_NAME"));
        holder.time.setText(TimeUtil.format(mData.get(position).optString("EVALUATE_TIME")));
        holder.mBar.setStepSize(1f);
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemsOnClick.onStarClick(v,pos);
            }
        });
        //图片展示
        String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("BIG_ICON_BACKGROUND");
        Picasso.with(context).load(headview).placeholder(R.drawable.waterfall_default).into(holder.headView);
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public TextView text;
        public TextView time;
        private RatingBar mBar;// 评价星级
        public LinearLayout reply;
    }

    public void onBoundData(List<JSONObject> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mData.clear();
        notifyDataSetChanged();
    }
}
