package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.AtyFollowUpPlanAdapter2;
import com.yksj.consultation.im.FUTemplateActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderRecord;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.utils.HStringUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 会诊
 */
public class AtyFollowUpPlan2 extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {

    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private AtyFollowUpPlanAdapter2 adapter;
    private List<JSONObject> mList;
    private String customer_id = DoctorHelper.getId();
    private LinearLayout add_followplan;
    private ImageView addView;
    private String mCustonerId;
    public static final String CONTENT = "content";
    String content = "";
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aty_follow_up_plan2);
        initView();

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("会诊记录");
        if (getIntent().hasExtra(CONTENT)) {
            content = getIntent().getStringExtra(CONTENT);
        }
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
        adapter = new AtyFollowUpPlanAdapter2(this);
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
        try {
            if (!HStringUtil.isEmpty(content)) {
                JSONArray array = new JSONArray(content);
                mList = new ArrayList<>();
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


//        Map<String, String> map = new HashMap<>();
//        map.put("customer_id", mCustonerId);//47324
//        map.put("flag", "0");
//        ApiService.OKHttpFindFollowUpPlAN(map, new ApiCallbackWrapper<String>(this) {
//
//            @Override
//            public void onError(Request request, Exception e) {
//                ToastUtil.showShort("添加失败");
//            }
//
//            @Override
//            public void onResponse(String content) {
//                try {
//                    JSONObject obj = new JSONObject(content);
//                    if ("0".equals(obj.optString("code"))) {
//                        mList = new ArrayList<>();
//                        JSONArray array = obj.getJSONArray("follows");
//                        JSONObject item;
//                        for (int i = 0; i < array.length(); i++) {
//                            item = array.getJSONObject(i);
//                            mList.add(item);
//                        }
//                        adapter.onBoundData(mList);
//
//                        if (mList.size() == 0) {
//                            add_followplan.setVisibility(View.VISIBLE);
//                            mPullRefreshListView.setVisibility(View.GONE);
//                        } else {
//                            add_followplan.setVisibility(View.GONE);
//                            mPullRefreshListView.setVisibility(View.VISIBLE);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, this);
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
                intent.putExtra("customer_id", mCustonerId);
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
        Intent intent = new Intent(AtyFollowUpPlan2.this, AtyOrderRecord.class);
        JSONObject object = adapter.datas.get(position);
        intent.putExtra("CONID", object.optInt("CONSULTATION_ID"));
        intent.putExtra("PERSONID", mCustonerId);
        startActivity(intent);
    }

}