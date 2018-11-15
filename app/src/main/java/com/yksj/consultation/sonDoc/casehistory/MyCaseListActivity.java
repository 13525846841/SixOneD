package com.yksj.consultation.sonDoc.casehistory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.ExpertCaseAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.CaseBean;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.List;

import butterknife.BindView;

/**
 * 专家端点击共享病历时跳转到我的病历界面
 * Created by lmk on 15/10/14.
 */
public class MyCaseListActivity extends BaseTitleActivity {
    private ExpertCaseAdapter mAdapter;
    private int mPageIndex = 1;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    @Override
    public int createLayoutRes() {
        return R.layout.search_result_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    //初始化视图
    private void initView() {
        setTitle("我的病历");
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData(false));
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> requestData(true));
        mRefreshLayout.autoRefresh();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new ExpertCaseAdapter());
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    /**
     * 病例条目点击
     *
     * @param adapter
     * @param view
     * @param position
     */
    private void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(MyCaseListActivity.this, CaseShowActivity.class);
        intent.putExtra("recordId", mAdapter.getData().get(position).MEDICAL_RECORD_ID);
        startActivity(intent);
    }

    /**
     * 请求数据
     *
     * @param isMore 是否是加载更多
     */
    private void requestData(boolean isMore) {
        if (!isMore) mPageIndex = 1;
        ApiService.OkHttpCaseList(mPageIndex, new ApiCallbackWrapper<ResponseBean<List<CaseBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<CaseBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<CaseBean> result = response.result;
                    mPageIndex++;
                    if (isMore) {
                        if (!result.isEmpty()) {
                            mAdapter.addData(result);
                            mRefreshLayout.finishLoadMore();
                        } else {
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    } else {
                        mRefreshLayout.setVisibility(!result.isEmpty() ? View.VISIBLE : View.GONE);
                        mEmptyView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                        if (!result.isEmpty()) mAdapter.setNewData(result);
                        mRefreshLayout.finishRefresh();
                    }
                } else {
                    ToastUtils.showShort(response.message);
                }
            }
        });
    }
}
