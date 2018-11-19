package com.yksj.consultation.sonDoc.consultation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.TmpPlanAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
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
 * 模板库
 */
public class TemplateLibDetailAty extends BaseTitleActivity {

    private RecyclerView mRecyclerView;
    private TmpPlanAdapter adapter;
    private String templateId;
    private List<JSONObject> mList;
    private String mTemplateName;

    public static Intent getCallingIntent(Context context, String templetId){
        Intent intent = new Intent(context, TemplateLibDetailAty.class);
        intent.putExtra("template_id", templetId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_template_lib_detail_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        templateId = getIntent().getStringExtra("template_id");
        setTitle("模板库");
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(8)));
        adapter = new TmpPlanAdapter();
        mRecyclerView.setAdapter(adapter);
        findViewById(R.id.add_lib).setOnClickListener(this);

        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_lib:
                AddLib();
                break;
        }
    }

    private void AddLib() {
        Map<String, String> map = new HashMap<>();
        map.put("template_id", templateId);
        map.put("customer_id", DoctorHelper.getId());
        ApiService.OKHttpsetPrivateTemplate(map, new ApiCallbackWrapper<String>(true) {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("查询失败");
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        ToastUtil.showShort(obj.optString("message"));
                        ActivityUtils.finishActivity(TemplatelibAty.class);//关闭模版库界面
                        finish();//关闭当前界面
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("template_id", templateId);
        ApiService.OKHttpFindSubFollowTemplate(map, new ApiCallbackWrapper<String>(true) {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("查询失败");
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        mList = new ArrayList<>();
                        JSONObject object = obj.getJSONObject("template");
                        JSONArray array = object.optJSONArray("subs");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            mList.add(item);
                        }
                        adapter.setNewData(mList);
                        mTemplateName = object.optJSONObject("template").optString("TEMPLATE_NAME");
                        setTitle(mTemplateName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }


}
