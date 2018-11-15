package com.yksj.consultation.sonDoc.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.adapter.AddListPAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

;

/**
 * Created by ${chen} on 2016/11/26.
 */
public class AddListFragmentP extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {

    private String TYPE = "7";
    private String groupId = "";
    private PullToRefreshListView mRefreshableView;
    private View mEmptyView;
    private AddListPAdapter mAdapter;
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
        mAdapter = new AddListPAdapter(mActivity, TYPE);
        mListView.setAdapter(mAdapter);
        // mRefreshableView.setOnLastItemVisibleListener(this);
        getMyFriends(searchContent);
    }

    /**
     * 刷新列表
     *
     * @param content
     */
    public void refreshData(String content) {
        searchContent = content;
        pagesize = 1;
        getMyFriends(content);
    }

    /**
     * 获取id
     */
    public List<JSONObject> getIdsData() {
        return mAdapter.mData;
    }

    private int pagesize = 1;

    /**
     * 加载医生友 (搜素好友)
     *
     * @param searchContent 查询key
     */
    private void getMyFriends(String searchContent) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId());
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "findMyPatient");
        BasicNameValuePair param2 = new BasicNameValuePair("NAME", searchContent);
        BasicNameValuePair param3 = new BasicNameValuePair("PAGESIZE", pagesize + "");
        BasicNameValuePair param4 = new BasicNameValuePair("PAGENUM", "20");
        BasicNameValuePair param5 = new BasicNameValuePair("statement", "5");
        BasicNameValuePair param6 = new BasicNameValuePair("group_id", groupId);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        params.add(param5);
        params.add(param6);
        ApiService.OKHttpFindMyPatient(params, new ApiCallbackWrapper<String>() {

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mRefreshableView.onRefreshComplete();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mRefreshableView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            if (pagesize == 1) {
                                mAdapter.removeAll();
                            }
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
                                mAdapter.addAll(mList);
                            } else if (count == 0 && pagesize == 1) {
                                mAdapter.removeAll();
                                mEmptyView.setVisibility(View.VISIBLE);
                                mRefreshableView.setVisibility(View.GONE);
                            } else if (count == 0 && pagesize > 1) {
                                ToastUtil.showShort("没有更多了");
                                mEmptyView.setVisibility(View.GONE);
                                mRefreshableView.setVisibility(View.VISIBLE);
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
        pagesize = 1;
        mAdapter.removeAll();
        getMyFriends(searchContent);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pagesize++;
        getMyFriends(searchContent);
    }
}
