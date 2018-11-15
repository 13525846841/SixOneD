package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.AtyFollowUpPlanAdapter3;
import com.yksj.consultation.im.FUTemplateActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrdersDetails2;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

;

/**
 * 病历
 */
public class AtyFollowUpPlan3 extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {

    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private AtyFollowUpPlanAdapter3 adapter;
    private List<JSONObject> mList;
    private String customer_id = DoctorHelper.getId();
    private LinearLayout add_followplan;
    private ImageView addView;
    private String mCustonerId;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aty_follow_up_plan3);
        initView();

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("病历");
        titleLeftBtn.setOnClickListener(this);
        mCustonerId = getIntent().getStringExtra("customer_id");
//        addView = (ImageView) findViewById(R.id.main_listmenuP);
//        addView.setVisibility(View.VISIBLE);
//        addView.setImageResource(R.drawable.addtemp);
//        addView.setOnClickListener(this);
//        add_followplan = (LinearLayout) findViewById(R.id.add_followplan);
//        findViewById(R.id.tianjiaplan).setOnClickListener(this);
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_follow_up__pulllist));
        mListView = mPullRefreshListView.getRefreshableView();
        adapter = new AtyFollowUpPlanAdapter3(this);
        mListView.setAdapter(adapter);
        mPullRefreshListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(this);
        mEmptyView = findViewById(R.id.empty_view_famous);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "queryPatientCaseList");
        map.put("customer_id", mCustonerId);
        ApiService.OKHttpCaseList(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("添加失败");
            }
            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("1".equals(obj.optString("code"))) {
                        mList = new ArrayList<>();
                        JSONArray array = obj.getJSONArray("result");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            mList.add(item);
                        }
                        adapter.onBoundData(mList);

                        if (mList.size() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mPullRefreshListView.setVisibility(View.GONE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            mPullRefreshListView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.tianjiaplan:
            case R.id.main_listmenuP:
                intent = new Intent(this, FUTemplateActivity.class);
                intent.putExtra("customer_id",mCustonerId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AtyOrdersDetails2.class);
        intent.putExtra("CONID", adapter.datas.get(position - 1).optString("CASE_DISCUSSION_ID"));
        intent.putExtra("MAINCASE", adapter.datas.get(position - 1).optString("MEDICAL_NAME"));
        startActivity(intent);
    }

}
