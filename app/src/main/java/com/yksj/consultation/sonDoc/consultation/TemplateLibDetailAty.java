package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import okhttp3.Request;;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.TemplateLibDetailAtyAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.business.LoginBusiness;
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

/**
 * 模板库
 */
public class TemplateLibDetailAty extends BaseActivity {

    private ListView mListView;
    private TemplateLibDetailAtyAdapter adapter;
    private String templateId;
    private List<JSONObject> mList;
    private TextView title;
    private String mTemplateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_lib_detail_aty);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("模板库");
        titleLeftBtn.setOnClickListener(this);
        title = (TextView) findViewById(R.id.template_title);
        mListView = (ListView) findViewById(R.id.followuplist);
        adapter = new TemplateLibDetailAtyAdapter(this);
        mListView.setAdapter(adapter);
        findViewById(R.id.add_lib).setOnClickListener(this);

        if (getIntent().hasExtra("template_id"))
            templateId = getIntent().getStringExtra("template_id");

        initData();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.add_lib:
                AddLib();
                break;
        }
    }

    private String customer_id = LoginBusiness.getInstance().getLoginEntity().getId();


    private void AddLib() {
        Map<String, String> map = new HashMap<>();
        map.put("template_id", templateId);
        map.put("customer_id", customer_id);
        ApiService.OKHttpsetPrivateTemplate(map, new ApiCallbackWrapper<String>(this) {
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
                        finish();
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
        ApiService.OKHttpFindSubFollowTemplate(map, new ApiCallbackWrapper<String>(this) {
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
                        adapter.onBoundData(mList);
                        mTemplateName = object.optJSONObject("template").optString("TEMPLATE_NAME");
                        title.setText(mTemplateName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }


}
