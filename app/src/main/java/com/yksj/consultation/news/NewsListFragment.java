package com.yksj.consultation.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseFragment;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yksj.consultation.adapter.MessageCenterAdapter;
import com.yksj.consultation.bean.MessageCenterBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.NewsConstant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.DynamicMessageListEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.FileUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 信息中心Fragment
 * Used for
 */

public class NewsListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    private int mPagesize = 1;// 页码
    private List<DynamicMessageListEntity> nfeList;
    private DynamicMessageListEntity dnlEntity;
    private HashMap<String, String> mAlreadyRead;// 已读

    public static final String TYPEID = "type_id";
    public static final String TYPENAME = "type_name";
    private String mTypeId = "";
    private String typename = "";

    @BindView(R.id.empty_layout)
    View mEmptyView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private MessageCenterAdapter mAdapter;

    public static NewsListFragment newInstance(String id, String name) {

        Bundle args = new Bundle();
        args.putString(NewsListFragment.TYPEID, id);
        args.putString(NewsListFragment.TYPENAME, name);

        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_center, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView(view);
    }

    private void initView(View view) {
        Bundle args = getArguments();
        mTypeId = args.getString(TYPEID);
        typename = args.getString(TYPENAME);
        mAlreadyRead = FileUtils.fatchReadedDynMes();
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(1), getResources().getColor(R.color.divider)));
        mAdapter = new MessageCenterAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        SimpleRefreshHeader refreshHeader = new SimpleRefreshHeader(getContext());
        mRefreshLayout.setRefreshHeader(refreshHeader);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestData(false);
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestData(true);
            }
        });
        mRefreshLayout.autoRefresh();
    }


    /**
     * 全部动态消息数据加载
     */
    private void requestData(final boolean isMore) {
        ApiService.OkHttpNews(mTypeId, new ApiCallbackWrapper<ResponseBean<List<MessageCenterBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<MessageCenterBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<MessageCenterBean> result = response.result;
                    if (isMore) {
                        mRefreshLayout.finishLoadMore();
                        mAdapter.addData(result);
                    } else {
                        if (result.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                            mAdapter.setNewData(result);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        MessageCenterBean item = mAdapter.getItem(i);
        String infoId = String.valueOf(item.INFO_ID);
        mAlreadyRead.put(infoId, infoId);
        FileUtils.updateReadedDynMesIds(mAlreadyRead);
        TextView textView = (TextView) view.findViewById(R.id.tv_title);
        textView.setTextColor(getResources().getColor(R.color.news_readed_color));
        Intent intent = new Intent(getActivity(), NewsInfoActivity.class);
        intent.putExtra(NewsConstant.INFO_ID, infoId);
        startActivity(intent);
    }
}
