package com.yksj.consultation.basic;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import java.util.List;

import butterknife.BindView;
import okhttp3.Request;

/**
 * 抽象类  主要作为基类实现列表Activity 有下拉刷新  加载出错 搜索等
 * Created by lmk on 15/10/21.
 */
public abstract class BaseListActivity extends BaseTitleActivity {

    @BindView(R.id.refresh_layout)
    public SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    public FrameLayout mEmptyLayout;

    public BaseQuickAdapter mAdapter;
    public int mPageIndex = 1;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_base_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPageIndex = 1;
            requestData(false, mPageIndex);
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.autoRefresh();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = createAdapter());

        mAdapter.setEnableLoadMore(getLoadMoreEnable());
        if (getLoadMoreEnable()) {
            mAdapter.setOnLoadMoreListener(() -> requestData(true, mPageIndex), mRecyclerView);
            mAdapter.disableLoadMoreIfNotFullPage();
        }
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public boolean getLoadMoreEnable(){
        return true;
    }

    /**
     * 数据回调内部实现了简单的数据处理逻辑
     * @param <Result>
     * @return
     */
    public <Result> ApiCallbackWrapper createSimpleCallback(Class<Result> item) {
        return new ApiCallbackWrapper<String>() {
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                if (TextUtils.isEmpty(json) || isDestroyed()) {
                    return;
                }
                ResponseBean<List<Result>> response = serializerData(json, item);
                if (response != null && response.isSuccess()) {
                    mPageIndex++;
                    List<Result> result = response.result;
                    if (mAdapter.isLoading()) {
                        if (result == null || result.isEmpty()) {
                            ToastUtils.showShort("没有更多数据");
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.addData(result);
                            mAdapter.loadMoreComplete();
                        }
                    } else {
                        if (result == null || result.isEmpty()) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyLayout.setVisibility(View.GONE);
                            mAdapter.setNewData(result);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                } else {
                    mRefreshLayout.finishRefresh();
                    mAdapter.loadMoreComplete();
                    ToastUtils.showShort("数据加载错误");
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                mAdapter.loadMoreComplete();
                mRefreshLayout.finishRefresh();
            }
        };
    }

    /**
     * json转实体
     * @param json json数据
     * @param item 集合数据类型
     * @param <Result>
     * @return
     */
    public <Result> ResponseBean<List<Result>> serializerData(String json, Class<Result> item) {
        return GsonSerializer.fromJsonArrar(json, item);
    }

    /**
     * 列表点击时间监听
     * @param adapter
     * @param view
     * @param i
     */
    public abstract void onItemClick(BaseQuickAdapter adapter, View view, int i);

    /**
     * 创建RecyclerView的适配器
     * @return
     */
    protected abstract BaseQuickAdapter createAdapter();

    /**
     * 请求数据 回调可以使用 SIMPLE_CALLBACK
     * @param isMore
     * @param pageIndex
     */
    protected abstract void requestData(boolean isMore, int pageIndex);
}
