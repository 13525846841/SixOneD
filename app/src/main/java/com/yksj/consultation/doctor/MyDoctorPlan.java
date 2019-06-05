package com.yksj.consultation.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.PlanAdapter;
import com.yksj.consultation.plan.AddBabyActivity;
import com.yksj.consultation.plan.PlanListActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.DocPlanEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 医教计划
 */
public class MyDoctorPlan extends BaseTitleActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mLv;
    private PlanAdapter adapter;
    private List<DocPlanEntity> data;
    private DocPlanEntity dpEntity;
    private View footview;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_my_doctor_plan;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }


    private void initView() {
        setTitle("医教计划");
        setRight("添加计划", this::onAddPlanClick);
        mLv = (ListView) findViewById(R.id.list);
        footview = View.inflate(this, R.layout.doc_plan_foot, null);
        mLv.addFooterView(footview);
        adapter = new PlanAdapter(this);
        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(this);
        footview.findViewById(R.id.tianjia).setOnClickListener(this::onAddPlanClick);
    }

    private String id = "4198";
    private String customer_id = DoctorHelper.getId();

    /**
     * 医教联盟计划列表
     */
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", customer_id);
        ApiService.OKHttpGetPlanList(map, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                data = new ArrayList<DocPlanEntity>();
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        JSONArray array = obj.getJSONArray("childrens");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            dpEntity = new DocPlanEntity();
                            dpEntity.setCHILDREN_NAME(item.optString("CHILDREN_NAME"));
                            dpEntity.setCHILDREN_ID(item.optString("CHILDREN_ID"));
                            dpEntity.setCUSTOMER_ID(item.optString("CUSTOMER_ID"));
                            dpEntity.setCHILDREN_BIRTHDAY(item.optString("CHILDREN_BIRTHDAY"));
                            dpEntity.setCHILDREN_REMARK(item.optString("CHILDREN_REMARK"));
                            dpEntity.setCHILDREN_HIGHT(item.optString("CHILDREN_HIGHT"));
                            dpEntity.setCHILDREN_WEIGHT(item.optString("CHILDREN_WEIGHT"));
                            dpEntity.setCHILDREN_SEX(item.optString("CHILDREN_SEX"));
                            dpEntity.setHEAD_PORTRAIT_ICON(item.optString("HEAD_PORTRAIT_ICON"));
                            data.add(dpEntity);
                        }
                        adapter.onBoundData(data);
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    /**
     * 添加计划
     * @param v
     */
    private void onAddPlanClick(View v) {
        Intent intent = new Intent(MyDoctorPlan.this, AddBabyActivity.class);
        intent.putExtra(AddBabyActivity.TYPE, "add");
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == parent.getChildCount() - 1) {
            return;
        } else {
            Intent intent = new Intent(this, PlanListActivity.class);
            intent.putExtra("CHILDREN_ID", adapter.datas.get(position).getCHILDREN_ID());
            startActivity(intent);
        }
    }
}
