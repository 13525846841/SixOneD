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
import com.yksj.consultation.adapter.UnionMemberListAdapte;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.UnionMemberBean;
import com.yksj.consultation.bean.UnionMemberListBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.StationHomeActivity;
import com.yksj.consultation.widget.SearchBarLayout;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

/**
 * 医生联盟(专家团)
 */
public class UnionMemberListActivity extends BaseListActivity {

    private static final String ID_EXTRA = "id_extra";

    @BindView(R.id.search_layout)
    SearchBarLayout mSearchLay;

    //搜索的关键字
    private String mSearchKey;
    // 联盟ID
    private String mUnionId;

    public static Intent getCallingIntent(Context context, String unionId){
        Intent intent = new Intent(context, UnionMemberListActivity.class);
        intent.putExtra(ID_EXTRA, unionId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_search_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("专家团");
        mUnionId = getIntent().getStringExtra(ID_EXTRA);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(8), Color.WHITE));
        mSearchLay.setOnSearchChangeListener(new SearchBarLayout.OnSearchChangeListener() {
            @Override
            public void onChanged(@NotNull String str) {
                mSearchKey = str;
                mRefreshLayout.autoRefresh();
            }
        });
    }

    /**
     * recyclerview item点击
     * @param adapter
     * @param view
     * @param i
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
        UnionMemberBean item = (UnionMemberBean) adapter.getItem(i);
        startActivity(StationHomeActivity.getCallingIntent(
                this
                , StationType.STATION_HOME_RECOMMEND
                , item.SITE_ID));
    }

    /**
     * 创建recyclervuew适配器
     * @return
     */
    @Override
    protected BaseQuickAdapter createAdapter() {
        return new UnionMemberListAdapte();
    }

    /**
     * 获取数据
     * @param isMore
     * @param pageIndex
     */
    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.OkHttpUnionMember(mUnionId, mSearchKey, pageIndex, createSimpleCallback(UnionMemberListBean.class));
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
                    List<UnionMemberBean> result = ((UnionMemberListBean) response.result).list;
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
