package com.yksj.consultation.adapter;

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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/18.
 */
public class GroupDataAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    public int lastPosition = 0;
    private List<JSONObject> mData = null;

    public GroupDataAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {
            return 2;
        } else if (position == getCount() - 2) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
        int type = getItemViewType(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_group_data_end, null);
                    holder.addHeadview = (ImageView) convertView.findViewById(R.id.addHeadview);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_group_data, null);
                    // holder = new ViewHolder();
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.headView = (ImageView) convertView.findViewById(R.id.chat_head);
                    break;
                case 2:
                    convertView = mInflater.inflate(R.layout.item_group_data_reduce, null);
                    holder.addHeadview = (ImageView) convertView.findViewById(R.id.addHeadview);
                    break;
                default:
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        if (position < mData.size() - 2) {
            String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("BIG_ICON_BACKGROUND");
            if (HStringUtil.isEmpty(mData.get(position).optString("REAL_NAME"))) {
                holder.name.setText(mData.get(position).optString("CUSTOMER_NICKNAME"));
            } else {
                holder.name.setText(mData.get(position).optString("REAL_NAME"));
            }
            Picasso.with(context).load(headview).error(R.drawable.default_head_mankind).placeholder(R.drawable.default_head_mankind).into(holder.headView);
        }
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public ImageView addHeadview;
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
