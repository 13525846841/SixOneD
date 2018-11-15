package com.yksj.consultation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.plan.AddBabyActivity;
import com.yksj.healthtalk.entity.DocPlanEntity;

/**
 * Created by tidus on 2016/11/7.
 * Used for 医教联盟计划列表适配器
 */
public class PlanAdapter extends SimpleBaseAdapter<DocPlanEntity> {
    private Context context;
    public PlanAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.my_plan_list_item;
    }

    @Override
    public View getItemView(int position, View convertView, SimpleBaseAdapter<DocPlanEntity>.ViewHolder holder) {
         TextView name = holder.getView(R.id.tv_plansnames);
         ImageView image = holder.getView(R.id.icon);
         TextView modify = holder.getView(R.id.modify);
         final DocPlanEntity dpEntity = datas.get(position);
         String babyName=dpEntity.getCHILDREN_NAME() + "的医教计划";
         name.setText(babyName);
         modify.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context, AddBabyActivity.class);
                 intent.putExtra(AddBabyActivity.TYPE,"modify");
                 intent.putExtra("content",dpEntity);
                 context.startActivity(intent);
             }
         });
         //图片展示
         String url= AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW+dpEntity.getHEAD_PORTRAIT_ICON();
         Picasso.with(context).load(url).placeholder(R.drawable.waterfall_default).into(image);
         Picasso.with(context).invalidate(url);

         return convertView;
    }

}
