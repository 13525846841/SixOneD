package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.sonDoc.R;

import org.json.JSONObject;

/**
 * Created by ${chen} on 2017/4/17.
 */
public class CreateTmpPlanAdapter extends BaseQuickAdapter<JSONObject, BaseViewHolder> {
    public Context context;

    public CreateTmpPlanAdapter() {
        super(R.layout.item_create_temp);
    }

    @Override
    protected void convert(BaseViewHolder helper, JSONObject item) {
        if (helper.getAdapterPosition() == 0){
            helper.getView(R.id.close_iv).setVisibility(View.GONE);
        }
        SuperTextView mTimeStv = helper.getView(R.id.time_stv);
        String timeHint = helper.getAdapterPosition() == 0 ? "首次" : "距上次";
        mTimeStv.setLeftString(timeHint);

        SuperTextView mActionStv = helper.getView(R.id.action_stv);
        mActionStv.setRightString(item.optString("template_sub_content"));

        String time = "";
        if ("10".equals(item.optString("template_sub_timetype"))) {
            time = item.optString("timetype_count") + "天";
        } else if ("20".equals(item.optString("template_sub_timetype"))) {
            time = item.optString("timetype_count") + "周";
        } else if ("30".equals(item.optString("template_sub_timetype"))) {
            time = item.optString("timetype_count") + "月";
        } else if ("40".equals(item.optString("template_sub_timetype"))) {
            time = item.optString("timetype_count") + "年";
        }
        mTimeStv.setRightString(time);

        helper.addOnClickListener(R.id.time_stv);
        helper.addOnClickListener(R.id.action_stv);
        helper.addOnClickListener(R.id.close_iv);
    }
}
