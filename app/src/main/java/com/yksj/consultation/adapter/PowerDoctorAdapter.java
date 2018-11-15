package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

/**
 * Created by ${chen} on 2016/11/23.
 */
public class PowerDoctorAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;

    public PowerDoctorAdapter(Context context){
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_power_doc, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.doc_name);
            holder.headView = (ImageView) convertView.findViewById(R.id.image);
            holder.pos = (TextView) convertView.findViewById(R.id.doc_position);
            holder.place = (TextView) convertView.findViewById(R.id.doc_place);
            holder.goodat = (TextView) convertView.findViewById(R.id.doc_goodat);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
    /**
     * 存放控件
     */
    public final class ViewHolder {
        public ImageView headView;
        public TextView name;
        public TextView pos;
        public TextView place;
        public TextView goodat;
    }
}
