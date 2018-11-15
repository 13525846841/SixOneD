package com.yksj.consultation.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.StationListAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationBean;
import com.yksj.consultation.bean.StationListBean;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.event.EStationChange;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 工作站列表
 */
public class StationListActivity extends BaseTitleActivity {
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private StationListAdapter mAdapter;
    private boolean mHasAddStation;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_stations_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setRefreshHeader(new SimpleRefreshHeader(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData());
        mRefreshLayout.autoRefresh();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new StationListAdapter());
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setTitle("工作站");
        setRight("创建", this::onRightClick);
    }

    /**
     * 点击创建工作站
     * @param v
     */
    private void onRightClick(View v) {
        if (DoctorHelper.isExpert()) {
            if (mHasAddStation) {
                Intent intent = new Intent(StationListActivity.this, StationCreateActivity.class);
                startActivity(intent);
            } else {
                ToastUtils.showShort("只能创建一个工作站！");
            }
        } else {
            ToastUtils.showShort("只有专家才能创建工作站！");
        }
    }

    /**
     * 加载数据
     */
    private void requestData() {
        ApiService.OKHttpStationList(DoctorHelper.getId(), new ApiCallbackWrapper<ResponseBean<StationListBean>>() {
            @Override
            public void onResponse(ResponseBean<StationListBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    StationListBean result = response.result;
                    mAdapter.getData().clear();
                    handleCreate(result.create);
                    handleJoin(result.join);
                    handleRecommend(result.recommend);
                }
                mRefreshLayout.finishRefresh();
            }
        });
    }

    /**
     * 推荐的工作站
     * @param recommends
     */
    private void handleRecommend(List<StationBean> recommends) {
        for (int i = 0; i < recommends.size(); i++) {
            StationBean stationBean = recommends.get(i);
            stationBean.stationType = StationBean.RECOMMEND_TYPE;
            stationBean.hasHead = i == 0;
        }
        mAdapter.addData(recommends);
    }

    /**
     * 加入的工作站
     * @param joins
     */
    private void handleJoin(List<StationBean> joins) {
        for (int i = 0; i < joins.size(); i++) {
            StationBean stationBean = joins.get(i);
            stationBean.stationType = StationBean.JOIN_TYPE;
            stationBean.hasHead = i == 0;
        }
        mAdapter.addData(joins);
    }

    /**
     * 创建的工作站
     * @param creates
     */
    private void handleCreate(List<StationBean> creates) {
        //有创建的工作站，就不能在创建工作站
        if (creates.isEmpty()) {
            mHasAddStation = true;
        }
        for (int i = 0; i < creates.size(); i++) {
            StationBean stationBean = creates.get(i);
            stationBean.stationType = StationBean.CREATE_TYPE;
            stationBean.hasHead = i == 0;
        }
        mAdapter.addData(creates);
    }

    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        StationBean stationBean = mAdapter.getData().get(position);
        switch (stationBean.stationType) {
            case StationBean.CREATE_TYPE:
                startActivity(StationHomeActivity.getCallingIntent(
                        StationListActivity.this
                        , StationType.STATION_HOME_CREATE
                        , stationBean.SITE_ID));
                break;
            case StationBean.JOIN_TYPE:
                startActivity(StationHomeActivity.getCallingIntent(
                        StationListActivity.this
                        , StationType.STATION_HOME_JOIN
                        , stationBean.SITE_ID));
                break;
            case StationBean.RECOMMEND_TYPE:
                startActivity(StationHomeActivity.getCallingIntent(
                        StationListActivity.this
                        , StationType.STATION_HOME_RECOMMEND
                        , stationBean.SITE_ID));
                break;
        }
    }

    /**
     * 刷新工作站成员列表
     * @param event
     */
    @Subscribe
    public void onRefreshEvent(EStationChange event) {
        mRefreshLayout.autoRefresh();
    }
}
