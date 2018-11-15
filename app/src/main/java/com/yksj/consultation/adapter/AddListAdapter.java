package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/26.
 */
public class AddListAdapter extends BaseAdapter {
    private Context context;
    private Activity mActivity;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private String type;//0 医生 1患者
    public List<JSONObject> mData = null;

    public AddListAdapter(Context context, String type) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.type = type;
        mActivity = (Activity) context;
        mData = new ArrayList<>();

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_addgroup, null);
            holder = new ViewHolder();

            holder.headView = (ImageView) convertView.findViewById(R.id.image_addnum);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.project = (TextView) convertView.findViewById(R.id.project);
            holder.choice = (ImageView) convertView.findViewById(R.id.image_choice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String mName = mData.get(position).optString("CUSTOMER_NICKNAME");
        String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("CLIENT_ICON_BACKGROUND");
        if (HStringUtil.isEmpty(mName)) {
            holder.name.setText("无姓名");
        } else {
            holder.name.setText(mName);
        }
        Picasso.with(context).load(headview).error(R.drawable.default_head_mankind).placeholder(R.drawable.default_head_mankind).into(holder.headView);
        final boolean checked = mData.get(position).optBoolean("isChecked");
        if (0 == mData.get(position).optInt("num")) {//未选择
            if (checked) {
                holder.choice.setImageResource(R.drawable.icon_checked);
            } else {
                holder.choice.setImageResource(R.drawable.icon_check);
            }
        } else {//已选择
            holder.choice.setImageResource(R.drawable.icon_gray_choice);
        }
        holder.choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (0 == mData.get(position).optInt("num")) {//未选择
                        if (mData.get(position).optBoolean("isChecked")) {
                            holder.choice.setImageResource(R.drawable.icon_check);
                            mData.get(position).put("isChecked", false);
                        } else {
                            holder.choice.setImageResource(R.drawable.icon_checked);
                            mData.get(position).put("isChecked", true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public TextView project;
        public ImageView choice;
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

    public void addAll(List<JSONObject> datas) {
        this.mData.addAll(datas);
        notifyDataSetChanged();
    }
}
