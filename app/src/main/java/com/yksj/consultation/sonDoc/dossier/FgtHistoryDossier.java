package com.yksj.consultation.sonDoc.dossier;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import okhttp3.Request;
import com.yksj.consultation.adapter.MyDossAdapter;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseDiscussDetailsActivity;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyCaseDetails;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.LogUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FgtHistoryDossier extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    //    private LookHistoryAdapter mAdapter;
    private MyDossAdapter mAdapter;
    private int pagesize1 = 1, pagesize2 = 1, pagesize3 = 1;
    private List<Map<String, String>> mDatas;
    private Map<String, String> mMap;
    private int mType;
    private int one = 1;
    private View nullView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bun = getArguments();
        int type = bun.getInt("DOSSIER");
        View mView = inflater.inflate(R.layout.fragment_fgt_history_dossier, container, false);
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.dossier_list);
        nullView = mView.findViewById(R.id.dossier_list_null);
        mListView = mPullToRefreshListView.getRefreshableView();
//        mAdapter = new LookHistoryAdapter(getActivity());
        switch (type) {
            case 112:
                mAdapter = new MyDossAdapter(getActivity(), 1);
                break;
            case 113:
            case 114:
                mAdapter = new MyDossAdapter(getActivity(), 0);
                break;
        }
        mListView.setEmptyView(nullView);
        mListView.setAdapter(mAdapter);
        mPullToRefreshListView.setOnRefreshListener(this);
        if (one == 1) {
            switch (type) {
                case 112:
                    mType = 112;
                    initDataShare1();
                    break;
                case 113:
                    mType = 113;
                    initDataMyShare1();
                    break;
                case 114:
                    mType = 114;
                    initDataFocus1();
                    break;
            }
            one = 2;
        }
        mListView.setOnItemClickListener(this);
        return mView;
    }

    //新我关注的
    private void initDataFocus1() {
        ApiService.OKHttpMedicalCaseDiscussionFocus(pagesize3, new MyApiCallback<JSONObject>(getActivity()) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    if ("1".equals(response.optString("code"))) {
                        mDatas = new ArrayList<Map<String, String>>();
                        JSONArray array = response.optJSONArray("result");
                        JSONObject itme = null;
                        for (int i = 0; i < array.length(); i++) {
                            itme = array.optJSONObject(i);
                            mMap = new HashMap<String, String>();
                            mMap.put("TALK", "0");
                            mMap.put("MEDICAL_RECORD_ID", itme.optString("MEDICAL_RECORD_ID"));
                            mMap.put("MEDICAL_NAME", itme.optString("MEDICAL_NAME"));
                            mMap.put("RELATION_TIME", itme.optString("RELATION_TIME"));
                            mMap.put("OFFICE_NAME", itme.optString("OFFICE_NAME"));
                            mMap.put("NUMS", itme.optString("NUMS"));
                            mDatas.add(mMap);
                        }
                        mAdapter.addAll(mDatas);
                    }
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullToRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshListView.onRefreshComplete();
            }
        }, this);
    }

    //新我上传的
    private void initDataMyShare1() {
        ApiService.OKHttpMedicalCaseDiscussionMy(pagesize2, new MyApiCallback<JSONObject>(getActivity()) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    if ("1".equals(response.optString("code"))) {
                        mDatas = new ArrayList<Map<String, String>>();
                        JSONArray array = response.optJSONArray("result");
                        JSONObject itme = null;
                        for (int i = 0; i < array.length(); i++) {
                            itme = array.optJSONObject(i);
                            mMap = new HashMap<String, String>();
                            mMap.put("TALK", "0");
                            mMap.put("MEDICAL_RECORD_ID", itme.optString("MEDICAL_RECORD_ID"));
                            mMap.put("MEDICAL_NAME", itme.optString("MEDICAL_NAME"));
                            mMap.put("SHARE_TIME", itme.optString("SHARE_TIME"));
                            mMap.put("OFFICE_NAME", itme.optString("OFFICE_NAME"));
                            mMap.put("NUMS", itme.optString("NUMS"));
                            mDatas.add(mMap);
                        }
                        mAdapter.addAll(mDatas);
                    }
                }
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullToRefreshListView.setRefreshing();
            }
        }, this);
    }

    //新同行共享病历讨论
    private void initDataShare1() {
        ApiService.OKHttpMedicalCaseDiscussion(pagesize1, new MyApiCallback<JSONObject>(getActivity()) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    if ("1".equals(response.optString("code"))) {
                        mDatas = new ArrayList<Map<String, String>>();
                        JSONArray array = response.optJSONArray("result");
                        JSONObject itme = null;
                        for (int i = 0; i < array.length(); i++) {
                            itme = array.optJSONObject(i);
                            mMap = new HashMap<String, String>();
                            mMap.put("MEDICAL_RECORD_ID", itme.optInt("MEDICAL_RECORD_ID") + "");
                            mMap.put("MEDICAL_NAME", itme.optString("MEDICAL_NAME"));
                            mMap.put("RECORD_TIME", itme.optString("RECORD_TIME"));
                            mMap.put("OFFICE_NAME", itme.optString("OFFICE_NAME"));
                            mDatas.add(mMap);
                        }
                        mAdapter.addAll(mDatas);
                    }
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullToRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshListView.onRefreshComplete();
            }
        }, this);
    }

    @Override
    public void onStart() {
        mAdapter.removeAll();
        Bundle bun = getArguments();
        int type = bun.getInt("DOSSIER");
        if (one == 1) {
            switch (type) {
                case 112:
                    mType = 112;
                    initDataShare1();
                    break;
                case 113:
                    mType = 113;
                    initDataMyShare1();
                    break;
                case 114:
                    mType = 114;
                    initDataFocus1();
                    break;
            }
            one = 2;
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        mAdapter.removeAll();
        if (one == 2) {
            one = 1;
        }
        super.onStop();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        switch (mType) {
            case 112:
                pagesize1 = 1;
                mAdapter.removeAll();
                initDataShare1();
                break;
            case 113:
                pagesize2 = 1;
                mAdapter.removeAll();
                initDataMyShare1();
                break;
            case 114:
                pagesize3 = 1;
                mAdapter.removeAll();
                initDataFocus1();
                break;
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        switch (mType) {
            case 112:
                ++pagesize1;
                initDataShare1();
                break;
            case 113:
                ++pagesize2;
                initDataMyShare1();
                break;
            case 114:
                ++pagesize3;
                initDataFocus1();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = null;
        boolean flag = false;
        Map map = mAdapter.datas.get(i - 1);
        String str = map.get("MEDICAL_RECORD_ID").toString();
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if ("TALK".equals(key)) {
                flag = true;
            } else {
            }
        }
        if (flag) {
            intent = new Intent(getActivity(), CaseDiscussDetailsActivity.class);
            intent.putExtra("recordId", str);
            LogUtil.d("TAG", "我的病历列表可讨论" + str);
        } else {
            intent = new Intent(getActivity(), AtyCaseDetails.class);
            intent.putExtra("caseId", Integer.parseInt(str));
//            intent = new Intent(getActivity(), AtyOrderDetails.class);
//            LogUtil.d("TAG","我的病历列表"+str);
//            intent.putExtra("CONID", Integer.parseInt(str));
//            intent.putExtra("STATE",99);
        }
        startActivity(intent);
    }
}
