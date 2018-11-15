package com.yksj.consultation.sonDoc.friend;

import java.util.ArrayList;
import java.util.List;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.adapter.FindFriendListAdapter;
import com.yksj.consultation.comm.RootListActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * 按条件查找好友的结果列表
 *
 * @author lmk
 */
public class FriendSearchResultActivity extends RootListActivity implements OnClickListener,
        OnRefreshListener2<ListView> {

    public final int ISDUOMEINUM = 1;//用的账号查询
    public final int ISCUSTOMERENTITY = 2;//用的实体查询
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private FindFriendListAdapter mAdapter;
    private CustomerInfoEntity mInfoEntity;
    private String duomeiNum = "";
    private int isType;//搜索类型
    private int pageSize = 1;//第几页,默认加载第一页

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search_result_layout);
        initView();
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            titleTextV.setText(intent.getStringExtra("title"));//设置标题
        }
        if (intent.hasExtra("mCustomerInfoEntity")) {
            mInfoEntity = intent.getParcelableExtra("mCustomerInfoEntity");
            mInfoEntity.setId(SmartFoxClient.getLoginUserId());
            mInfoEntity.setFlag("-100000");//第一次加载
            isType = ISCUSTOMERENTITY;
        } else if (intent.hasExtra("duoMeiNum")) {
            duomeiNum = intent.getStringExtra("duoMeiNum");
            isType = ISDUOMEINUM;
        }
        loadData();
    }

    /**
     * 去网络加载数据
     * 如下情况  1-按账号查询   2-按组合好的实体查询
     *
     * @param which
     */
    private void loadData() {
        switch (isType) {
            case ISDUOMEINUM:
                ApiService.doHttpFriendExactSearch(SmartFoxClient.getLoginUserId(),
                        "20", String.valueOf(pageSize), duomeiNum, 0, objectHandler);
                break;
            case ISCUSTOMERENTITY:
                ApiService.doHttpRequestSearchFriends2(mInfoEntity, pageSize, objectHandler);
                break;
        }

    }

    private ObjectHttpResponseHandler objectHandler = new ObjectHttpResponseHandler() {
        @Override
        public Object onParseResponse(String content) {
            if (content != null && content.contains("error_message"))
                return content;
            if (isType == ISDUOMEINUM) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.has("CUSLIST"))
                        return FriendHttpUtil.jsonAnalysisFriendEntity(jsonObject.optString("CUSLIST"), false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } else
                return FriendHttpUtil.jsonAnalysisFriendEntity(content, false);
        }

        @Override
        public void onSuccess(Object response) {
            super.onSuccess(response);
            if (response != null && response instanceof List) {//解析成功
                ArrayList<CustomerInfoEntity> data = (ArrayList<CustomerInfoEntity>) response;
                if (pageSize == 1) {//第一次加载清空原有数据
                    mAdapter.removeAll();
                }
                if (data.size() != 0) {
                    pageSize++;
                    mAdapter.addAll(data);
                }
            } else if (response != null && response instanceof String) {//出现错误
                try {
                    JSONObject object = new JSONObject((String) response);
                    ToastUtil.showShort(object.optString("error_message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStart() {
            mPullToRefreshListView.setRefreshing();
            super.onStart();
        }

        @Override
        public void onFinish() {
            mPullToRefreshListView.onRefreshComplete();
            super.onFinish();
        }

    };

    //初始化界面
    private void initView() {
        initializeTitle();
        titleTextV.setText(R.string.knoweSearch);
        titleLeftBtn.setOnClickListener(this);
//        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.search_result_pulllist);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setOnRefreshListener(this);
        mAdapter = new FindFriendListAdapter(FriendSearchResultActivity.this);
        mAdapter.setOnClickFollowListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FriendHttpUtil.chatFromPerson(FriendSearchResultActivity.this, mAdapter.datas.get(position - 1));
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageSize = 1;
        loadData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public ListView getListView() {
        return mListView;
    }

    @Override
    public BaseAdapter getAdapter() {
        return mAdapter;
    }

}
