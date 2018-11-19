package com.yksj.consultation.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.sonDoc.R;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by ${chen} on 2017/4/17.
 */
public class TmpPlanAdapter extends BaseQuickAdapter<JSONObject, BaseViewHolder> {
    public Context context;
    public List<Map<String, String>> list;

    public TmpPlanAdapter() {
        super(R.layout.item_temp);
    }

    @Override
    protected void convert(BaseViewHolder helper, JSONObject item) {
        SuperTextView mTimeStv = helper.getView(R.id.time_stv);
        String timeHint = "0".equals(item.optString("TEMPLATE_SEQ")) ? "首次" : "距上次";
        mTimeStv.setLeftString(timeHint);

        SuperTextView mActionStv = helper.getView(R.id.action_stv);
        mActionStv.setRightString(item.optString("TEMPLATE_SUB_CONTENT"));

        String time = "";
        if ("10".equals(item.optString("TEMPLATE_SUB_TIMETYPE"))) {
            time = item.optString("TIMETYPE_COUNT") + "天";
        } else if ("20".equals(item.optString("TEMPLATE_SUB_TIMETYPE"))) {
            time = item.optString("TIMETYPE_COUNT") + "周";
        } else if ("30".equals(item.optString("TEMPLATE_SUB_TIMETYPE"))) {
            time = item.optString("TIMETYPE_COUNT") + "月";
        } else if ("40".equals(item.optString("TEMPLATE_SUB_TIMETYPE"))) {
            time = item.optString("TIMETYPE_COUNT") + "年";
        }
        mTimeStv.setRightString(time);

        helper.addOnClickListener(R.id.time_stv);
        helper.addOnClickListener(R.id.action_stv);
    }
}
