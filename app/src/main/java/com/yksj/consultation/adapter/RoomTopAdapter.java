package com.yksj.consultation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.onClickMsgeListener;

/**
 * Created by ${chen} on 2016/11/24.
 * 我的工作室上面三个固定按钮的gridview的适配器
 *
 */
public class RoomTopAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private String[] NAME = {"预约就诊","在线会诊"};
    onClickMsgeListener clickdeleteMsgeListener;
    public boolean ischange = false;

    public RoomTopAdapter(Context context){
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
    }
//    public interface onClickMsgeListener{
//        void onClickMsg(View view,int positon);
//    }
    public void setonClickMsgeListener(onClickMsgeListener attentionListener){
        this.clickdeleteMsgeListener = attentionListener;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return NAME[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder= new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_myroom_utils, null);
            holder.name = (TextView) convertView.findViewById(R.id.utils_name);
//            holder.utils = (TextView) convertView.findViewById(R.id.true_false);
//            holder.utils_bg = (ImageView) convertView.findViewById(R.id.image_bg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final View view = convertView;
        final int pos = position;
        holder.name.setText(NAME[position]);
        holder.utils.setText("未开通");
        clickdeleteMsgeListener.onClickMsg(view,pos);

//        if (ischange){
//            holder.utils.setText("未开通");
//            holder.utils_bg.setSelected(false);
//        }else {
//            holder.utils.setText("已开通");
//            holder.name.setTextColor(Color.WHITE);
//            holder.utils.setTextColor(Color.WHITE);
//            holder.utils_bg.setSelected(true);
//        }
        return convertView;
    }


//    public void isChange(int changeLoge){
//        if (0==changeLoge){
//            ischange = false;
//            notifyDataSetChanged();
//        }else if (1==changeLoge){
//            ischange = true;
//            notifyDataSetChanged();
//        }
//    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public TextView utils;
        public ImageView utils_bg;
    }

}
