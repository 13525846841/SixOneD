package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.BillAdapper;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单明细
 */
public class AccountList extends BaseTitleActivity implements View.OnClickListener {

    private ListView mListView;
    public BillAdapper adapter;
    private List<JSONObject> mList = new ArrayList<>();
    private RelativeLayout mEmptyView;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_account_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("账单明细");
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.account_lv);
        adapter = new BillAdapper(mList,this);
        mListView.setAdapter(adapter);
        mEmptyView = (RelativeLayout) findViewById(R.id.load_faile_layout);
        initData();
    }

    private String customer_id = DoctorHelper.getId();
    private void initData() {
        Map<String,String> map=new HashMap<>();
        map.put("CUSTOMERID", customer_id);//customer_id／／116305
        map.put("PAGENUM", "1");
        map.put("PAGECOUNT", "5");
        ApiService.OKHttpACCOUNTCHANGE(map,new ApiCallbackWrapper<String>(this){
            @Override
            public void onResponse(String content) {
                super.onResponse(content);
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("1".equals(obj.optString("code"))) {
                        JSONArray array = obj.getJSONArray("result");
                        mList = new ArrayList<JSONObject>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonobject = array.getJSONObject(i);
                            mList.add(jsonobject);
                        }
                    }

                    adapter.onBoundData(mList);
                    if (mList.size()==0){
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }else {
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }
}
