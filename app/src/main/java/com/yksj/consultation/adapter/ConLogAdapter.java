package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/28.
 */
public class ConLogAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, String>> conlog;
    private List<JSONObject> mData = null;

    public ConLogAdapter(Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_conlog, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.config_time);
            holder.text = (TextView) convertView.findViewById(R.id.config_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            String log_content = mData.get(position).getString("LOG_CONTENT");
            holder.text.setText(log_content);
            String log_time = mData.get(position).getString("LOG_TIME");
            holder.time.setText(TimeUtil.getFormatTime(log_time));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView time;
        public TextView text;
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
