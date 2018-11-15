package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.StationListAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationBean;
import com.yksj.consultation.bean.StationListBean;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.StationHomeActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.List;

import butterknife.BindView;

public class DoctorStationListActivity extends BaseTitleActivity {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyLayout;

    private StationListAdapter mAdapter;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, DoctorStationListActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doctor_station_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("工作站");
        initializeView();
    }

    private void initializeView() {
        mRefreshLayout.setRefreshHeader(new SimpleRefreshHeader(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData());
        mRefreshLayout.autoRefresh();
        mRecyclerView.setAdapter(mAdapter = new StationListAdapter());
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(BaseQuickAdapter adapter, View view, int i) {
        StationBean stationBean = mAdapter.getData().get(i);
        switch (stationBean.stationType) {
            case StationBean.CREATE_TYPE:
                startActivity(StationHomeActivity.getCallingIntent(DoctorStationListActivity.this
                        , StationType.STATION_HOME_CREATE
                        , stationBean.SITE_ID));
                break;
            case StationBean.JOIN_TYPE:
                startActivity(StationHomeActivity.getCallingIntent(DoctorStationListActivity.this
                        , StationType.STATION_HOME_JOIN
                        , stationBean.SITE_ID));
                break;
        }
    }

    /**
     * 请求数据
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
                }
                mRefreshLayout.finishRefresh();
            }
        });
    }

    /**
     * 创建的工作站
     * @param creates
     */
    private void handleCreate(List<StationBean> creates) {
        for (int i = 0; i < creates.size(); i++) {
            StationBean stationBean = creates.get(i);
            stationBean.stationType = StationBean.CREATE_TYPE;
            stationBean.hasHead = i == 0;
        }
        mAdapter.addData(creates);
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
}
