package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.QueryDoctorAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationDoctorBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.doctor.DoctorHomeActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctor.NavigateFragment;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.List;

import butterknife.BindView;

/**
 * 查询医生列表
 */
public class QueryDoctorActivity extends BaseTitleActivity implements NavigateFragment.SelectorResultListener {
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private FragmentManager manager;
    private NavigateFragment navFragment;
    private int mPageIndex = 1;//当前的页数
    private String areaCode = "", unitCode = "";
    private String siteId;
    private QueryDoctorAdapter mAdapter;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_invite_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        navFragment = new NavigateFragment();
        navFragment.setSelectorListener(this);
        transaction.add(R.id.navigationbar_layout, navFragment);
        transaction.commit();
    }

    private void initView() {
        setTitle("查询医生");
        siteId = getIntent().getStringExtra("site_id");
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData(false, areaCode, unitCode));
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> requestData(true, areaCode, unitCode));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mRecyclerView.setAdapter(mAdapter = new QueryDoctorAdapter());
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        StationDoctorBean item = mAdapter.getItem(position);
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, siteId);
        intent.putExtra(Constant.Station.USER_ID, item.CUSTOMER_ID);
        intent.putExtra(Constant.Station.DOCTOR_HOME_TYPE, DoctorHomeType.DOCTOR_HOME_INVITE);//邀请
        startActivity(intent);
    }

    @Override
    public void goNotifyLoadData(String areaCode, String unitCode) {
        mPageIndex = 1;
        this.areaCode = areaCode;
        this.unitCode = unitCode;
        requestData(false, areaCode, unitCode);
    }

    private void requestData(boolean isMore, String areaCode, String unitCode) {
        if (!isMore) mPageIndex = 1;
        ApiService.OkHttpStationQueryDoctor(areaCode, unitCode, mPageIndex, new ApiCallbackWrapper<ResponseBean<List<StationDoctorBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<StationDoctorBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mPageIndex++;
                    List<StationDoctorBean> result = response.result;
                    if (isMore) {
                        if (result.isEmpty()) {
                            ToastUtils.showShort("没有更多了");
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            mAdapter.addData(result);
                            mRefreshLayout.finishLoadMore();
                        }
                    } else {
                        mAdapter.setNewData(result);
                        mRefreshLayout.finishRefresh();
                    }
                }
            }
        });
    }
}
