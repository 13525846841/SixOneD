package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.TemplateOnClickListener;
import com.yksj.healthtalk.utils.TimeUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ${chen} on 2017/7/26.
 */
public class TemplateLibDetailAtyAdapter extends SimpleBaseAdapter<JSONObject> {
    private TemplateOnClickListener templateOnClickListener;
    public Context context;
    public List<Map<String,String>> list ;

    public TemplateLibDetailAtyAdapter(Context context){
        super(context);
        this.context = context;
        list= new ArrayList<>();
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
        return R.layout.item_addtemp;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {

        TextView content = holder.getView(R.id.template_content);
        TextView time = holder.getView(R.id.times);
        TextView template_time = holder.getView(R.id.template_time);


        if ("0".equals(datas.get(position).optString("TEMPLATE_SEQ"))){
            time.setText("首次");
        }else {
            time.setText("距上次");
        }

        if ("10".equals(datas.get(position).optString("TEMPLATE_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"天");
        }else if ("20".equals(datas.get(position).optString("TEMPLATE_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"周");
        }else if ("30".equals(datas.get(position).optString("TEMPLATE_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"月");
        }else if ("40".equals(datas.get(position).optString("TEMPLATE_SUB_TIMETYPE"))){
            template_time.setText(datas.get(position).optString("TIMETYPE_COUNT")+"年");
        }

        content.setText(TimeUtil.getTimeStr(datas.get(position).optString("TEMPLATE_SUB_CONTENT")));

        return convertView;
    }
}
