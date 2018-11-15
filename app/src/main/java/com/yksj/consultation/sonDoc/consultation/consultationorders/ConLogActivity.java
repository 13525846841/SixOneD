package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.ConLogAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生端 接诊日志
 * Created by HEKL on 16/11/11.
 */
public class ConLogActivity extends BaseActivity implements View.OnClickListener {

    private int state;//医生身份状态
    private List<JSONObject> logLists = null;
    private ListView mListView;
    private ConLogAdapter mAdapter;
    private static final int ADD_LOG = 10001;
    private View mEmptyView;
    private String conId = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_conlog);
        initView();
        getData();
    }

    private void initView() {
        initializeTitle();
        if (getIntent().hasExtra("conId")) {
            conId = getIntent().getStringExtra("conId");
        }
        titleTextV.setText("接诊日志");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setText("添加");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        mEmptyView = findViewById(R.id.empty_view);
        mListView = (ListView) findViewById(R.id.mListview_text);
        mAdapter = new ConLogAdapter(this);
        mListView.setAdapter(mAdapter);
        logLists = new ArrayList<>();
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            state = LoginBusiness.getInstance().getLoginEntity().getRoldid();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://添加
                intent = new Intent(this, AddConLogActivity.class);
                intent.putExtra(AddConLogActivity.CONID,conId);
                startActivityForResult(intent, ADD_LOG);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logLists = null;
    }


    /**
     * 加载日志列表
     */
    private void getData() {

        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());
        map.put("op", "queryLogList");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    if (logLists.size() > 0) {
                        logLists.clear();
                    }
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(obj.optString("code"))) {
                            JSONArray array = obj.getJSONArray("result");
                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    logLists.add(object);
                                }
                                mListView.setVisibility(View.VISIBLE);
                                mEmptyView.setVisibility(View.GONE);
                                mAdapter.onBoundData(logLists);
                            } else {
                                mAdapter.removeAll();
                                mListView.setVisibility(View.GONE);
                                mEmptyView.setVisibility(View.VISIBLE);
                            }


                        } else {
                            ToastUtil.showShort(obj.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOG && resultCode == RESULT_OK) {
            getData();
        }
    }
}
