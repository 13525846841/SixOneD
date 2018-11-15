package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.station.StationMemberChoiceAty;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/7/5.
 */
public class StationMemberChoiceAdapter extends SimpleBaseAdapter<JSONObject> {
    private StationMemberChoiceAty activity;

    public StationMemberChoiceAdapter(Context context) {
        super(context);
        activity = (StationMemberChoiceAty) context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_station_member_choice;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        ImageView custHaed = holder.getView(R.id.det_img_head);
        TextView custName = holder.getView(R.id.tv_station_pro);
        TextView consultnum = holder.getView(R.id.tv_station_show);
        CheckBox choice = holder.getView(R.id.checkbox);
        RelativeLayout relativeLayout = holder.getView(R.id.rl_item);


        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + datas.get(position).optString("CLIENT_ICON_BACKGROUND");
        Picasso.with(context).load(url).placeholder(R.drawable.default_head_doctor).into(custHaed);


        custName.setText(datas.get(position).optString("DOCTOR_REAL_NAME"));
        if (HStringUtil.isEmpty(datas.get(position).optString("INTRODUCTION"))) {
            consultnum.setVisibility(View.VISIBLE);
        } else {
            consultnum.setText("简介: " + datas.get(position).optString("INTRODUCTION"));
        }
        if (datas.get(position).optBoolean("isChecked")) {
            choice.setChecked(true);
        } else {
            choice.setChecked(false);
        }
        choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    int count = getCount();
                    for (int i = 0; i < count; i++) {
                        try {
                            if (i != position) {
                                datas.get(i).put("isChecked", false);
                            } else {
                                datas.get(i).put("isChecked", isChecked);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }
}
