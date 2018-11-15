package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.TemplateOnClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${chen} on 2017/4/18.
 */
public class NewTemplateAtyAdapter extends SimpleBaseAdapter<JSONObject> {

    private TemplateOnClickListener templateOnClickListener;
    public List<Map<String, String>> list;

    public NewTemplateAtyAdapter(Context context, TemplateOnClickListener templateOnClickListener) {
        super(context);
        this.templateOnClickListener = templateOnClickListener;
        list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("template_seq", "0");
        map.put("timetype_count", "");
        map.put("template_sub_timetype", "");
        map.put("template_sub_content", "");
        list.add(map);
        list.get(0).put("time", "");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_addtemp;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        TextView textview = holder.getView(R.id.times);
        TextView template_time = holder.getView(R.id.template_time);
        TextView template_content = holder.getView(R.id.template_content);
        ImageView imageView = holder.getView(R.id.iv_delect);

        RelativeLayout times = holder.getView(R.id.rl_consul);
        RelativeLayout template = holder.getView(R.id.rl_add_template);
        final View view = convertView;
        final int pos = position;
        final int id = R.id.rl_consul;
        final int id1 = R.id.rl_add_template;

        if (position == 0) {
            imageView.setVisibility(View.GONE);
            textview.setText("首次");
        } else {
            imageView.setVisibility(View.VISIBLE);
            textview.setText("距上次");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contentBean.setExit(false);
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templateOnClickListener.onStarClick(view, pos, id);
            }
        });
        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templateOnClickListener.onStarClick(view, pos, id1);
            }
        });

        String count = list.get(position).get("timetype_count");
        String dataType = list.get(position).get("template_sub_timetype");
        if ("10".equals(dataType)) {
            template_time.setText(count + "天");
        } else if ("20".equals(dataType)) {
            template_time.setText(count + "周");
        } else if ("30".equals(dataType)) {
            template_time.setText(count + "月");
        } else if ("40".equals(dataType)) {
            template_time.setText(count + "年");
        } else {
            template_time.setText("");
        }
        template_content.setText(list.get(position).get("template_sub_content"));
        return convertView;
    }
}
