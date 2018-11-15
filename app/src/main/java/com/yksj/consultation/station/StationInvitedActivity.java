package com.yksj.consultation.station;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yksj.consultation.adapter.InvitedStationAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;

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


/**
 * 医生集团邀请管理
 */
public class StationInvitedActivity extends BaseTitleActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {
    private InvitedStationAdapter adapter;
    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private List<JSONObject> list = null;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_invite_station_list_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("邀请管理");
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_station__pulllist));
        mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setOnRefreshListener(this);
        adapter = new InvitedStationAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        initData();
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "queryInviteSiteList");//class_id
        map.put("customer_id", LoginBusiness.getInstance().getLoginEntity().getId());
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    list = new ArrayList<>();
                    if (HttpResult.SUCCESS.equals(obj.optString("code"))) {

                        if (!HStringUtil.isEmpty(obj.optString("result"))) {
                            JSONArray array = obj.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                list.add(jsonobject);
                            }
                            adapter.onBoundData(list);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullRefreshListView.onRefreshComplete();
                super.onAfter();
            }
        }, this);
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
        Intent intent = new Intent(this, StationInvitedDetailActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, adapter.datas.get(position - 1).optString("SITE_ID"));
        startActivity(intent);
    }
}
