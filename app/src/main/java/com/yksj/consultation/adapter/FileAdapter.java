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
 * Created by ${chen} on 2016/11/29.
 * 群文件的适配器
 */
public class FileAdapter  extends BaseAdapter{
    private Context context;
    private LayoutInflater mInflater;
    public int lastPosition = 0;

    public FileAdapter(Context context){
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 5;
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
            convertView = mInflater.inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.file = (ImageView) convertView.findViewById(R.id.file);
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
        public ImageView file;

    }
}
