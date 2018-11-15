package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/4/26.
 */
public class SeeTemplateAdapter extends SimpleBaseAdapter<JSONObject> {
    public Context context;

    public SeeTemplateAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemResource() {
        return R.layout.item_seetemp;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {

        TextView content = holder.getView(R.id.template_content);
        TextView times = holder.getView(R.id.times);
        TextView template_time = holder.getView(R.id.template_time);


        if ("0".equals(datas.get(position).optString("TEMPLATE_SEQ"))){
            times.setText("首次");
        } else {
            times.setText("距上次");
        }
        content.setText(datas.get(position).optString("FOLLOW_CONTENT"));

        if ("10".equals(datas.get(position).optString("FOLLOW_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"天");
        }else if ("20".equals(datas.get(position).optString("FOLLOW_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"周");
        }else if ("30".equals(datas.get(position).optString("FOLLOW_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"月");
        }else if ("40".equals(datas.get(position).optString("FOLLOW_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"年");
        }

       // template_time.setText(TimeUtil.getTimeStr(datas.get(position).optString("FOLLOW_TIME")));


        return convertView;
    }
}
