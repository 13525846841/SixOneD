package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;

import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/7/5.
 */
public class InvitedStationAdapter extends SimpleBaseAdapter<JSONObject> {

    public InvitedStationAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_station;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        ImageView custHaed = holder.getView(R.id.det_img_head);
        TextView workName = holder.getView(R.id.tv_work_name);
        TextView workDocName = holder.getView(R.id.tv_name_);
        TextView consultnum = holder.getView(R.id.tv_doc_show);


        String url= AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW+datas.get(position).optString("SITE_BIG_PIC");
        Picasso.with(context).load(url).placeholder(R.drawable.default_head_doctor).into(custHaed);


        workName.setText(datas.get(position).optString("SITE_NAME"));
        workDocName.setText(datas.get(position).optString("DOCTOR_REAL_NAME"));
        consultnum.setText("简介: "+datas.get(position).optString("SITE_DESC"));


        return convertView;
    }
}
