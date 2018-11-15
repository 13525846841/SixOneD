package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ${chen} on 2017/5/22.
 */
public class PaySelectAdapter extends SimpleBaseAdapter<JSONObject> {

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private Context context;
    private List<JSONObject> list = new ArrayList<JSONObject>();
    public int  type;
    public PaySelectAdapter(Context context) {
        super(context);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.pay_select_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {

        TextView textView = holder.getView(R.id.pay_name);
        TextView payBalance = holder.getView(R.id.pay_balance);

        ImageView imageView = holder.getView(R.id.image);

        type = Integer.parseInt(datas.get(position).optString("type"));



        if (1==type){
            textView.setText("余额支付");
            payBalance.setText(datas.get(position).optString("yellow_boy"));
            imageView.setImageResource(R.drawable.yu_e2);
        }else if (2==type){
            textView.setText("微信支付");
            imageView.setImageResource(R.drawable.weixin);
        }else if (3==type){
            textView.setText("支付宝支付");
            imageView.setImageResource(R.drawable.zhifubao);
        }else if (4==type){
            textView.setText("银联支付");
            imageView.setImageResource(R.drawable.back2);
        }
//        textView.setText(datas.get(position).optString("TOOL_NAME"));
//        String url= AppContext.getHttpUrls().URL_QUERYHEADIMAGE_NEW+datas.get(position).optString("");
//        Picasso.with(context).load(url).placeholder(R.drawable.default_head_patient).into(imageview);

        return convertView;
    }

    public int PayType(int position){
        return Integer.parseInt(datas.get(position).optString("type"));
    }
}