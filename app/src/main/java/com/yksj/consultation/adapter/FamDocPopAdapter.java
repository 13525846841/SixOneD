package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 病例讨论 科室的适配器
 */
public class FamDocPopAdapter extends SimpleBaseAdapter<JSONObject> {
    private Context context;
    private List<JSONObject> list = new ArrayList<JSONObject>();

    public FamDocPopAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return  R.layout.item_pop_pro;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
            TextView name = holder.getView(R.id.pop_place);

        if (!HStringUtil.isEmpty(datas.get(position).optString("OFFICE_NAME"))){
            name.setText( datas.get(position).optString("OFFICE_NAME"));
        }

            return convertView;
    }
}
