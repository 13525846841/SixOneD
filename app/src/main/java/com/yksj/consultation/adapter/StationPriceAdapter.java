package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.doctor.constant.ServiceType;

import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/7/13.
 */
public class StationPriceAdapter extends SimpleBaseAdapter<JSONObject> {

    public StationPriceAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_station_price_list;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {

        TextView textView = holder.getView(R.id.item_name);
        TextView textprice = holder.getView(R.id.picandcul_price);
        ImageView icon = holder.getView(R.id.image_addnum);

        if (ServiceType.TW.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
            textView.setText("图文咨询");
            icon.setImageResource(R.drawable.picandcul);
        } else if (ServiceType.BY.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
            textView.setText("包月咨询");
            icon.setImageResource(R.drawable.consul);
        } else if (ServiceType.DH.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
            textView.setText("电话咨询");
            icon.setImageResource(R.drawable.phone);
        } else if (ServiceType.SP.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
            textView.setText("视频咨询");
            icon.setImageResource(R.drawable.video);
        } else if (ServiceType.MZ.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
            textView.setText("门诊预约");
            icon.setImageResource(R.drawable.addnum);
        }

        if ("1".equals(datas.get(position).optString("ORDER_ON_OFF"))) {
            if (ServiceType.MZ.equals(datas.get(position).optString("SERVICE_TYPE_ID"))) {
                textprice.setText("已开通");
            } else {
                textprice.setText(datas.get(position).optString("SERVICE_PRICE") + "元/次");
            }

        } else if ("0".equals(datas.get(position).optString("ORDER_ON_OFF"))) {
            textprice.setText("未开通");
        }
        return convertView;
    }
}
