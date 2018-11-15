package com.yksj.consultation.union;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.UnionListAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.UnionBean;
import com.yksj.consultation.bean.UnionListBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.widget.SearchBarLayout;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

public class UnionListActivity extends BaseListActivity {

    @BindView(R.id.search_layout)
    SearchBarLayout mSearchLay;

    //搜索的关键字
    private String mSearchKey;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, UnionListActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_search_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("医生联盟");
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(8), Color.WHITE));
        mSearchLay.setSearchHint("请输入医生联盟名称");
        mSearchLay.setOnSearchChangeListener(new SearchBarLayout.OnSearchChangeListener() {
            @Override
            public void onChanged(@NotNull String str) {
                mSearchKey = str;
                mRefreshLayout.autoRefresh();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
        UnionBean unionBean = (UnionBean) adapter.getItem(i);
        Intent intent = UnionHomeActivity.getCallingIntent(this, unionBean.UNION_ID);
        startActivity(intent);
    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new UnionListAdapter();
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.OkHttpUnionList(mSearchKey, pageIndex, createSimpleCallback(UnionListBean.class));
    }

    @Override
    public <Result> ApiCallbackWrapper createSimpleCallback(Class<Result> item) {
        return new ApiCallbackWrapper<String>() {
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                if (TextUtils.isEmpty(json)){
                    return;
                }
                ResponseBean<Result> response = GsonSerializer.fromJsonObject(json, item);
                if (response.isSuccess()) {
                    mPageIndex++;
                    List<UnionBean> result = ((UnionListBean) response.result).list;
                    if (mAdapter.isLoading()) {
                        if (result.isEmpty()) {
                            ToastUtils.showShort("没有更多数据");
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.addData(result);
                            mAdapter.loadMoreComplete();
                        }
                    } else {
                        if (result.isEmpty()) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyLayout.setVisibility(View.GONE);
                            mAdapter.setNewData(result);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                } else {
                    ToastUtils.showShort(response.message);
                }
            }
        };
    }
}
