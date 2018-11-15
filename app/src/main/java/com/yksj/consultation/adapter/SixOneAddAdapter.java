package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/17.
 */
//六一班的适配器
public class SixOneAddAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    public List<JSONObject> mData = null;

    public SixOneAddAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_sixone_add, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.doc_name);
            holder.headView = (ImageView) convertView.findViewById(R.id.image);
            holder.pro = (TextView) convertView.findViewById(R.id.doc_room);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.buy_or_not);
            holder.checkBox.setButtonDrawable(R.drawable.icon_gray_choice);
            holder.checkBox.setChecked(true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int friendFlag = mData.get(position).optInt("status");
        if (friendFlag == 0) {
            holder.checkBox.setChecked(false);
            holder.checkBox.setButtonDrawable(R.drawable.choice_or_not);
        } else {
            holder.checkBox.setChecked(true);
            holder.checkBox.setButtonDrawable(R.drawable.icon_gray_choice);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    mData.get(position).put("isChecked", isChecked);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        String mName = mData.get(position).optString("CUSTOMER_NICKNAME");
        String mPro = mData.get(position).optString("OFFICE_NAME");
        String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("BIG_ICON_BACKGROUND");
        holder.name.setText(mName);
        holder.pro.setText(mPro);
        Picasso.with(context).load(headview).error(R.drawable.default_head_mankind).placeholder(R.drawable.default_head_mankind).into(holder.headView);
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public TextView pro;
        public CheckBox checkBox;
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
