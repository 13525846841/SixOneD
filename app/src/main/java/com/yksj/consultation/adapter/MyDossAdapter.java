package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by zheng on 15/10/10.
 */
public class MyDossAdapter extends SimpleBaseAdapter<Map<String, String>> {
    private int type = 0;

    public MyDossAdapter(Context context, int type) {
        super(context);
        this.type = type;
    }

    @Override
    public int getItemResource() {
        return R.layout.case_dis_list_item2;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        final Map<String, String> map = datas.get(position);
        boolean flag = false;
        TextView mName = (TextView) holder.getView(R.id.case_name);
        TextView mOffice = (TextView) holder.getView(R.id.case_keshi);
        TextView mTime = (TextView) holder.getView(R.id.case_time);
//        LinearLayout talk = (LinearLayout) holder.getView(R.id.talk_ll);
//        TextView mTalkNum = (TextView) holder.getView(R.id.case_talk_num);
        String nameStr = "";
        if ("null".equals((String) map.get("MEDICAL_NAME"))) {
            nameStr = "暂无";
        } else {
            nameStr = (String) map.get("MEDICAL_NAME");
        }
        mName.setText(nameStr);
        String officeStr = "";
        if ("null".equals((String) map.get("OFFICE_NAME"))) {
            officeStr = "暂无";
        } else {
            officeStr = (String) map.get("OFFICE_NAME");
        }
        mOffice.setText(officeStr);
        String time = "";
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if ("TALK".equals(key)) {
//                talk.setVisibility(View.VISIBLE);
                flag = true;
            }
            if (type == 1) {
                if ("RECORD_TIME".equals(key)) {
                    time = (String) map.get("RECORD_TIME");
                } else if ("RELATION_TIME".equals(key)) {
                    time = (String) map.get("RELATION_TIME");
                }
            } else {
                if ("SHARE_TIME".equals(key)) {
                    time = (String) map.get("SHARE_TIME");
                } else if ("RELATION_TIME".equals(key)) {
                    time = (String) map.get("RELATION_TIME");
                }
            }
        }
        if ("null".equals(time) | "".equals(time)) {
            mTime.setVisibility(View.GONE);
        } else {
            mTime.setText(TimeUtil.getTimeStr(time.substring(0, 8)));
        }
        if (flag) {
//            mTalkNum.setText((String) map.get("NUMS"));
        }
        return convertView;
    }
}
