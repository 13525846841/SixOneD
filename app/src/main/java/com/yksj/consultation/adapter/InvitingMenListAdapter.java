package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

/**
 * Created by ${chen} on 2017/7/14.
 *
 * 邀请中适配器
 */
public class InvitingMenListAdapter extends SimpleBaseAdapter<JSONObject>{
    private ImageLoader instance;
    private Context context;
    private Activity maActivity;
    private int color;
    private DisplayImageOptions mOptions;

    public InvitingMenListAdapter(Context context) {
        super(context);
        this.context = context;
        maActivity = (Activity) context;
        color = context.getResources().getColor(R.color.color_text_gray);
        instance = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createHeadDisplayImageOptions(maActivity);

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_invite_mem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {

        ImageView custHaed = holder.getView(R.id.det_img_head);
        TextView custName = holder.getView(R.id.tv_doc_pro);
        TextView consultnum = holder.getView(R.id.tv_doc_place);
        TextView textView = holder.getView(R.id.text);//邀请中 或者申请中 文字


        String url= AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW+datas.get(position).optString("BIG_ICON_BACKGROUND");
        Picasso.with(context).load(url).placeholder(R.drawable.default_head_doctor).into(custHaed);

        textView.setVisibility(View.VISIBLE);
        textView.setText("邀请中");


        if (!HStringUtil.isEmpty(datas.get(position).optString("DOCTOR_REAL_NAME"))){
            custName.setText(datas.get(position).optString("DOCTOR_REAL_NAME"));
        }else {
            custName.setText("匿名");
        }

        if (HStringUtil.isEmpty(datas.get(position).optString("INTRODUCTION"))){
            consultnum.setVisibility(View.GONE);
        }else {
            consultnum.setText("简介: "+datas.get(position).optString("INTRODUCTION"));
        }
        return convertView;
    }
}
