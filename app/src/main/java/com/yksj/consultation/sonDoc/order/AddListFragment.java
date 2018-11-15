package com.yksj.consultation.sonDoc.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.adapter.AddListAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
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
 * Created by ${chen} on 2016/11/26.
 */
public class AddListFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {

    private String TYPE = "7";
    private String groupId = "";
    private PullToRefreshListView mRefreshableView;
    private View mEmptyView;
    private AddListAdapter mAdapter;
    private String searchContent = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addgroup_fragment_layout, null);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getString("type");
        groupId = getArguments().getString("groupId");
    }

    private void initView(View view) {
        mRefreshableView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
        mEmptyView = view.findViewById(R.id.empty_view);
        ListView mListView = mRefreshableView.getRefreshableView();
        mRefreshableView.setOnRefreshListener(this);
        mAdapter = new AddListAdapter(mActivity, TYPE);
        mListView.setAdapter(mAdapter);
        getMyFriends(searchContent, TYPE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 刷新列表
     *
     * @param content
     */
    public void refreshData(String content) {
        searchContent = content;
        getMyFriends(content, TYPE);

    }

    /**
     * 获取id
     */
    public List<JSONObject> getIdsData() {
        return mAdapter.mData;
    }


    /**
     * 加载医生友 (搜素好友)
     *
     * @param searchContent 查询key
     * @param type          0 医生好友 1 患者好友列表
     */
    private void getMyFriends(String searchContent, String type) {

        Map<String, String> map = new HashMap<>();
        map.put("content", searchContent);
        map.put("customer_id", DoctorHelper.getId());
        map.put("op", "queryFriendsList");
        map.put("type", type);
        map.put("statement", "5");
        map.put("group_id", groupId);
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>() {

            @Override
            public void onAfter() {
                super.onAfter();
                mRefreshableView.onRefreshComplete();
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mRefreshableView.setRefreshing();
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            JSONArray array = object.getJSONArray("result");
                            List<JSONObject> mList = new ArrayList<JSONObject>();
                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    obj.put("isChecked", false);
                                    mList.add(obj);
                                }
                                mRefreshableView.setVisibility(View.VISIBLE);
                                mEmptyView.setVisibility(View.GONE);
                                mAdapter.onBoundData(mList);
                            } else {
                                mAdapter.removeAll();
                                mEmptyView.setVisibility(View.VISIBLE);
                                mRefreshableView.setVisibility(View.GONE);
                            }
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getMyFriends(searchContent, TYPE);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getMyFriends(searchContent, TYPE);
    }
}
