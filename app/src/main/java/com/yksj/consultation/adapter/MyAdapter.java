package com.yksj.consultation.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.yksj.consultation.sonDoc.R.id.hos;
import static com.yksj.consultation.sonDoc.R.id.info1;
import static com.yksj.consultation.sonDoc.R.id.info2;

/**
 * Created by hww on 18/4/10.
 * Used for
 */

public class MyAdapter extends BaseAdapter {
    public List<JSONObject> list;
    private LayoutInflater inflater;
    private Context ctx;

    public MyAdapter(Context ctx) {
        this.list = new ArrayList<>();
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_station_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.head);
            holder.name = (TextView) convertView.findViewById(R.id.office);
            holder.hos = (TextView) convertView.findViewById(hos);
            holder.info1 = (TextView) convertView.findViewById(info1);
            holder.info2 = (TextView) convertView.findViewById(info2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + list.get(position).optString("ICON_DOCTOR_PICTURE");
        Picasso.with(ctx).load(url).placeholder(R.drawable.default_head_doctor).into(holder.imageView);

        String stationName = list.get(position).optString("DOCTOR_NAME") + list.get(position).optString("OFFICE_NAME");
        int count = list.get(position).optString("DOCTOR_NAME").length();

        SpannableString spannableString = new SpannableString(stationName);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ctx.getResources().getColor(R.color.color_blue));
        spannableString.setSpan(colorSpan, 0, count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.name.setText(spannableString);
        if (HStringUtil.isEmpty(list.get(position).optString("SITE_HOSPOTAL"))){
            holder.hos.setVisibility(View.GONE);
        }else {
            holder.hos.setVisibility(View.VISIBLE);
            holder.hos.setText(list.get(position).optString("SITE_HOSPOTAL"));
        }



        String stationInfo1 = list.get(position).optString("VISIT_TIME") + "次浏览，";
        String stationInfo2 = list.get(position).optString("MEMBER_NUM") + "名医生";


        int count2 = list.get(position).optString("VISIT_TIME").length();
        int count3 = list.get(position).optString("VISIT_TIME").length();

        SpannableString spannableString2 = new SpannableString(stationInfo1);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(ctx.getResources().getColor(R.color.red_text));
        spannableString2.setSpan(colorSpan2, 0, count2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        SpannableString spannableString3 = new SpannableString(stationInfo2);
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(ctx.getResources().getColor(R.color.red_text));
        spannableString3.setSpan(colorSpan3, 0, count3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        holder.info1.setText(spannableString2);
        holder.info2.setText(spannableString3);
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView name;
        public TextView hos;
        public TextView info1;
        public TextView info2;
    }

    public void onBoundData(List<JSONObject> data) {
        this.list.clear();
        this.list.addAll(data);
        notifyDataSetChanged();
    }
}
