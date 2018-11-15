package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zheng on 15/9/25.
 */
public class EvelateAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String ,String >> mData=new ArrayList<>();

    public EvelateAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    public void add(List<Map<String, String>> mData){
        this.mData=mData;
        notifyDataSetChanged();
    }

    public void removeAll(){
        this.mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.evaluate_list_item,null);
            TextView evaluateNameh = (TextView)view.findViewById(R.id.evaluate_name);
            TextView evaluateConh = (TextView)view.findViewById(R.id.evaluate_con);
            RatingBar evaluateStarh = (RatingBar)view.findViewById(R.id.evaluate_star);
            ViewHolder holder = new ViewHolder();
            holder.evaluateCon=evaluateConh;
            holder.evaluateName=evaluateNameh;
            holder.evaluateStar=evaluateStarh;
            view.setTag(holder);
        }
        ViewHolder holder=(ViewHolder)view.getTag();
        Map<String ,String > map=mData.get(i);
        String conStr=(String)map.get("COMMENT_RESULT");
        if(!"null".equals(conStr)){
            holder.evaluateCon.setText(map.get("COMMENT_RESULT"));
        }
        holder.evaluateName.setText(map.get("REAL_NAME"));
        holder.evaluateStar.setRating(Float.parseFloat(map.get("SERVICE_LEVEL")));
        return view;
    }
    public static class ViewHolder{
        TextView evaluateName;
        TextView evaluateCon;
        RatingBar evaluateStar;
    }
}
