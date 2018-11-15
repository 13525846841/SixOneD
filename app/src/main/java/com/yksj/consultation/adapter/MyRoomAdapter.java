package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/21.工具箱的适配器
 */
public class MyRoomAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    public boolean isOpen;
    private List<JSONObject> mList =null;

    public MyRoomAdapter(Context context){
        this.context =context;
        this.mInflater = LayoutInflater.from(context);
        mList= new ArrayList<JSONObject>();
    }

    //设置背景颜色，文字的变化
    public void setBackground(boolean isOpen){
        this.isOpen = isOpen;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_myroom_utils, null);
            holder.name = (TextView) convertView.findViewById(R.id.utils_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mList.get(position).optString("TOOL_NAME"));
        return convertView;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position==getCount()-1){
//            return 0;
//        }else{
//            return 1;
//        }
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size()+1;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        int type = getItemViewType(position);
//        ViewHolder holder;
//        if (convertView == null) {
//            holder= new ViewHolder();
//            switch (type){
//                case 0:
//                    convertView = mInflater.inflate(R.layout.item_myroom_add, null);
//                    holder.addUtilsview = (ImageView) convertView.findViewById(R.id.addHeadview);
//                    break;
//                case 1:
//                    convertView = mInflater.inflate(R.layout.item_myroom_utils, null);
//                    holder.name = (TextView) convertView.findViewById(R.id.utils_name);
//                    holder.utils = (TextView) convertView.findViewById(R.id.true_false);
//                    holder.utils_bg = (ImageView) convertView.findViewById(R.id.image_bg);
//                    break;
//            }
//            convertView.setTag(holder);
//        } else{
//            holder = (ViewHolder) convertView.getTag();
//        }
//        switch (type){
//            case 1:
//                if (isOpen ==true){
//                    holder.utils_bg.setSelected(true);
//                    holder.utils.setText("开通");
//                    holder.utils.setTextColor(Color.WHITE);
//                    holder.name.setTextColor(Color.WHITE);
//                }else{
//                    holder.utils.setText(未开通");
//                }
//
//                holder.name.setText(mList.get(position).optString("TOOL_NAME"));
//                if (mList.get(position).optString("USED_FLAG").equals("1")){
//                    holder.utils_bg.setSelected(true);
//                    holder.utils.setText("已开通");
//                    holder.utils.setTextColor(Color.WHITE);
//                    holder.name.setTextColor(Color.WHITE);
//                }
//              break;
//            default:
//                break;
//        }
//        return convertView;
//    }

    /**
     * 存放控件
     */
    public static class ViewHolder {
        public TextView name;
        public TextView utils;
        public ImageView addUtilsview;
        public ImageView utils_bg;
    }
    public void onBoundData(List<JSONObject> datas) {
        if (mList != null) {
            mList.clear();
            mList.addAll(datas);
            notifyDataSetChanged();
        }
    }
}
