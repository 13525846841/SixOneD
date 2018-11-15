package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.adapter.AppMenListAdapter;
import com.yksj.consultation.adapter.InvitingMenListAdapter;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.doctor.DoctorHomeActivity;
import com.yksj.consultation.sonDoc.R;
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
 * Created by ${chen} on 2017/7/7.
 * 邀请成员 申请成员   列表进入查看
 */
public class InviteMenListFragment2 extends BaseFragment implements PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener, AdapterView.OnItemClickListener {
    private String TYPE = "7";
    private int PAGE_NUMBER = 1; //页数
   // private QueryDoctorAdapter adapter;
    private InvitingMenListAdapter adapter;
    private AppMenListAdapter mAdapter;
    private PullToRefreshListView mRefreshableView;
    private JSONArray mArray ;
    private List<JSONObject> mList;
    private View mEmptyView;
    private List<JSONObject> list = null;
    private ListView mListView;
    private String siteId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_order_fragment_layout, null);
        initView(view);
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getString("type");
        siteId = getArguments().getString("mStationId");
    }


    private void initView(View view) {
        mRefreshableView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
        mEmptyView = view.findViewById(R.id.empty_view);
        mListView = mRefreshableView.getRefreshableView();
        mRefreshableView.setOnRefreshListener(this);
        adapter = new InvitingMenListAdapter(mActivity);
        mAdapter = new AppMenListAdapter(mActivity);

        mListView.setOnItemClickListener(this);
        mRefreshableView.setOnLastItemVisibleListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        PAGE_NUMBER = 1;
        if("0".equals(TYPE)){
            mListView.setAdapter(adapter);
            initInviteData();//邀请中数据加载
        }else if ("1".equals(TYPE)){
            mListView.setAdapter(mAdapter);
            initApplyData();//申请中数据加载
        }
    }

    /**
     * 申请中数据加载
     */
    private void initApplyData() {
        Map<String,String> map=new HashMap<>();
        map.put("op", "queryApplyList");
        map.put("site_id", siteId);//mStationId
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(mActivity){
            @Override
            public void onError(Request request, Exception e) {
            }
            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    list = new ArrayList<>();
                    if (HttpResult.SUCCESS.equals(obj.optString("code"))){

                        if (!HStringUtil.isEmpty(obj.optString("result"))){
                            JSONArray array = obj.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                list.add(jsonobject);
                            }
                            mAdapter.onBoundData(list);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mRefreshableView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mRefreshableView.onRefreshComplete();
                super.onAfter();
            }
        },this);
    }

    /**
     * 邀请中数据加载
     */
    private void initInviteData() {
        Map<String,String> map=new HashMap<>();
        map.put("op", "queryInviteList");
        map.put("site_id", siteId);//mStationId
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(mActivity){
            @Override
            public void onError(Request request, Exception e) {
            }
            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    list = new ArrayList<>();
                    if (HttpResult.SUCCESS.equals(obj.optString("code"))){

                        if (!HStringUtil.isEmpty(obj.optString("result"))){
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
                mRefreshableView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mRefreshableView.onRefreshComplete();
                super.onAfter();
            }
        },this);
    }


    @Override
    public void onLastItemVisible() {

    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if("0".equals(TYPE)){
            Intent intent = new Intent(mActivity, DoctorHomeActivity.class);
            intent.putExtra(Constant.Station.DOCTOR_HOME_TYPE, DoctorHomeType.DOCTOR_HOME_INVITE);
            intent.putExtra(Constant.Station.STATION_ID,siteId);
            intent.putExtra(Constant.Station.USER_ID,adapter.datas.get(position-1).optString("CUSTOMER_ID"));
            startActivity(intent);
        }else if ("1".equals(TYPE)){
            Intent intent = new Intent(mActivity, DoctorHomeActivity.class);
            intent.putExtra(Constant.Station.DOCTOR_HOME_TYPE,DoctorHomeType.DOCTOR_HOME_APPLY);
            intent.putExtra(Constant.Station.STATION_ID,siteId);//mStationId
            intent.putExtra(Constant.Station.USER_ID,mAdapter.datas.get(position).optString("APPLY_ID"));
            intent.putExtra("reason",mAdapter.datas.get(position).optString("APPLY_DESC"));
            startActivity(intent);
        }

    }
}
