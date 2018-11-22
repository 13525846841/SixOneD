package com.yksj.consultation.sonDoc.consultation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.FollowTemplateBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.im.FUTemplateActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 随访计划
 */
public class FollowPlanListActivity extends BaseListActivity {

    private String mSickId;

    public static Intent getCallingIntent(Context context, String sickId) {
        Intent intent = new Intent(context, FollowPlanListActivity.class);
        intent.putExtra("sick_id", sickId);
        return intent;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("随访计划");
        mSickId = getIntent().getStringExtra("sick_id");
        setRight(R.drawable.addtemp, this::onAddPlan);
    }

    @Override
    public boolean getLoadMoreEnable() {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
        FollowTemplateBean bean = (FollowTemplateBean) adapter.getItem(i);
        startActivity(SeeTemplateActivity.getCallingIntent(this, bean.followId));
    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new BaseQuickAdapter<FollowTemplateBean, BaseViewHolder>(R.layout.item_follow_plan) {

            @Override
            protected void convert(BaseViewHolder helper, FollowTemplateBean item) {
                helper.setText(R.id.create_time_tv, getFormatTime(item));
                helper.setText(R.id.plan_name_tv, item.name);
            }

            private String getFormatTime(FollowTemplateBean item){
                try {
                    Date date = new SimpleDateFormat("yyyyMMddhhmmss").parse(item.time);
                    return new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", mSickId);//47324
        map.put("flag", "0");
        ApiService.OKHttpFindFollowUpPlAN(map, new ApiCallbackWrapper<String>() {

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        List<FollowTemplateBean> result = new ArrayList<>();
                        JSONArray array = obj.getJSONArray("follows");
                        for (int i = 0; i < array.length(); i++) {
                            String json = array.getString(i);
                            FollowTemplateBean templateBean = GsonSerializer.deserialize(json, FollowTemplateBean.class);
                            result.add(templateBean);
                        }
                        getAdapter().setNewData(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getRefreshLayout().finishRefresh();
            }
        }, this);
    }

    /**
     * 添加随访计划
     * @param view
     */
    private void onAddPlan(View view) {
        startActivity(FUTemplateActivity.getCallingIntent(this, mSickId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData(false, 1);
    }

}
