package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/4/13.
 * 会诊记录适配器
 */
public class AtyFollowUpPlanAdapter2 extends SimpleBaseAdapter<JSONObject> {

    public Context context;

    public AtyFollowUpPlanAdapter2(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_followup2;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        TextView textview = holder.getView(R.id.temp_content);
        TextView temp_doc_name = holder.getView(R.id.temp_time);
        textview.setText(datas.get(position).optString("CONSULTATION_NAME"));
        temp_doc_name.setText(TimeUtil.getTimeStr(datas.get(position).optString("CREATE_TIME").substring(0,8)));
        return convertView;
    }


}
