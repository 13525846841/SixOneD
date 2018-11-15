package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yksj.consultation.sonDoc.R;

import butterknife.BindView;

public abstract class BaseRecyclerActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    protected BaseQuickAdapter mAdapter;
    protected int mPageIndex;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_base_recycler;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, 1, getResources().getColor(R.color.divider)));
        mAdapter = createAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.autoRefresh();
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                requestData(false);
            }
        });
    }

    public abstract BaseQuickAdapter createAdapter();

    public abstract void requestData(boolean isMore);

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }
}
