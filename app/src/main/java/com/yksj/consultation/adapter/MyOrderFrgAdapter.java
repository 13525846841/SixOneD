package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2017/4/6.
 */
public class MyOrderFrgAdapter extends BaseAdapter {
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    public Context context;
    public List<JSONObject> list = new ArrayList<JSONObject>();
    public String type;
    public String serviceTypeId;
    public MyOrderFrgAdapter(List<JSONObject> list,Context context,String type,String serviceTypeId){
        this.serviceTypeId = serviceTypeId;
        this.context = context;
        this.list = list;
        this.type = type;
        this.mInflater = LayoutInflater.from(context);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_order, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name_tv);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.imageview = (ImageView) convertView.findViewById(R.id.det_img_head);
            holder.age = (TextView) convertView.findViewById(R.id.tv_year);
            holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
            holder.time_long = (TextView) convertView.findViewById(R.id.tv_circle);
            holder.order_number = (TextView) convertView.findViewById(R.id.tv_order_number);
            holder.service_money = (TextView) convertView.findViewById(R.id.tv_order_money);
            holder.startTime = (TextView) convertView.findViewById(R.id.tv_order_time);
            holder.endTime = (TextView) convertView.findViewById(R.id.end_time);
            holder.darkbacktext = (TextView) convertView.findViewById(R.id.darkbacktext);
            holder.rl_experience = (RelativeLayout) convertView.findViewById(R.id.rl_experience);
            holder.darkReason = (TextView) convertView.findViewById(R.id.tv_dark_fail_reason);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            if ("1".equals(list.get(position).optString("isBack"))) {
                holder.darkbacktext.setVisibility(View.VISIBLE);
                holder.darkbacktext.setText("正在申请退款中");
            } else if ("2".equals(list.get(position).optString("isBack"))) {
                holder.darkbacktext.setVisibility(View.VISIBLE);
                holder.darkbacktext.setText("退款成功");
            } else if ("3".equals(list.get(position).optString("isBack"))){
                holder.darkbacktext.setVisibility(View.VISIBLE);
                holder.darkbacktext.setText("退款失败");
            }else if ("0".equals(list.get(position).optString("isBack"))){
                holder.darkbacktext.setVisibility(View.GONE);
            }

            if ("9".equals(serviceTypeId)) {
                holder.darkbacktext.setVisibility(View.GONE);
            }


            if ("1".equals(type)){
                holder.title.setVisibility(View.GONE);
            }else {
                holder.title.setText("距离结束还有"+list.get(position).optString("LASTTIME"));
            }

            holder.order_number.setText("订单号: "+ list.get(position).optString("PAY_ID"));
            holder.service_money.setText("服务费用: "+ list.get(position).optString("SERVICE_GOLD")+ "元");
            holder.startTime.setText("服务开始时间: "+ TimeUtil.getFormatDate(list.get(position).optString("SERVICE_START")));
            holder.endTime.setText("服务结束时间: "+ TimeUtil.getFormatDate(list.get(position).optString("SERVICE_END")));
            holder.time_long.setText("服务周期: " + list.get(position).optString("cycle"));

        try {
            if (HStringUtil.isEmpty(list.get(position).getJSONObject("info").optString("CUSTOMER_SEX"))){
                holder.tv_sex.setVisibility(View.GONE);
            }else {
                if (list.get(position).getJSONObject("info").optString("CUSTOMER_SEX").equals("W")){
                    holder.tv_sex.setText("女");
                }else if (list.get(position).getJSONObject("info").optString("CUSTOMER_SEX").equals("M")){
                    holder.tv_sex.setText("男");
                }
            }
            if (HStringUtil.isEmpty(list.get(position).getJSONObject("info").optString("AGE"))){
                holder.age.setVisibility(View.GONE);
            }else {
                holder.age.setText(list.get(position).getJSONObject("info").optString("AGE")+"岁");
            }
            if (HStringUtil.isEmpty(list.get(position).getJSONObject("info").optString("CUSTOMER_NICKNAME"))){
                holder.name.setText(list.get(position).getJSONObject("info").optString("CUSTOMER_ACCOUNTS"));
            }else {
                holder.name.setText(list.get(position).getJSONObject("info").optString("CUSTOMER_NICKNAME"));
            }

            //图片展示
            String url= AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW+list.get(position).getJSONObject("info").optString("CLIENT_ICON_BACKGROUND");
            Picasso.with(context).load(url).placeholder(R.drawable.default_head_patient).into(holder.imageview);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
    public final class ViewHolder {
        public TextView title ;
        public ImageView imageview ;
        public TextView name ;
        public TextView age ;//职称
        public TextView tv_sex ;//科室

        public TextView time_long ;//周期
        public TextView order_number;//订单号
        public TextView service_money ;//服务费用
        public TextView startTime ;//开始时间
        public TextView endTime;//结束时间
        public TextView darkReason;//退款失败原因
        private TextView darkbacktext;
        private RelativeLayout rl_experience;
    }
    public void onBoundData(List<JSONObject> datas) {
        if (list != null) {
            list.clear();
            list.addAll(datas);
            notifyDataSetChanged();
        }
    }

}
