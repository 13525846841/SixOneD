package com.yksj.consultation.sonDoc.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.library.base.base.BaseFragment;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.SubCommentAdapter;
import com.yksj.consultation.bean.CommentSubTab;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.doctor.ReplyActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.bean.CommentBean;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by ${chen} on 2016/11/16.
 */
public class SubCommentFeagment extends BaseFragment {

    private static final String TAB_EXTRA = "tab_extra";
    /**
     * 评论类型 //10全部 20 已回复 30 未回复
     */
    public static final int ALL = 10, ALREADY = 20, NOT = 30;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private View mEmptyView;
    public static final String ID = "id";
    public static final String SITE = "site";
    private String site_id = "";
    private CommentSubTab mTab;
    private SubCommentAdapter mAdapter;

    public static SubCommentFeagment newInstance(CommentSubTab commentSubTab) {

        Bundle args = new Bundle();
        args.putSerializable(TAB_EXTRA, commentSubTab);

        SubCommentFeagment fragment = new SubCommentFeagment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.assess_fragment_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        mTab = (CommentSubTab) getArguments().getSerializable(TAB_EXTRA);
        mEmptyView = view.findViewById(R.id.empty_layout);

        mRefreshLayout
                .setEnableLoadMore(false)
                .setOnRefreshListener(refreshLayout -> requestData())
                .autoRefresh();
        mRecyclerView.setAdapter(mAdapter = new SubCommentAdapter());
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(0,15));
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener(){
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                int itemViewId = view.getId();
                switch (itemViewId) {
                    case R.id.txt_reply:
                        Intent intentShop=new Intent(getActivity(), ReplyActivity.class);
                        intentShop.putExtra("id",mAdapter.getData().get(position).EVALUATE_ID);
                        startActivity(intentShop);
                        break;

                }
            }
        });

        if (getActivity().getIntent().hasExtra(SITE)) {
            site_id = getActivity().getIntent().getStringExtra(SITE);
            getSiteData();
        } else {
            requestData();
        }
    }

    /**
     * 已回复数据
     */
    public void requestData() {
        ApiService.OkHttpMyComment(DoctorHelper.getId(), String.valueOf(mTab.tag), new ApiCallbackWrapper<ResponseBean<List<CommentBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<CommentBean>> response) {
                super.onResponse(response);
                if (response.code == 1) {// MMP
                    List<CommentBean> result = response.result;
                    mAdapter.setNewData(result);
                    mRefreshLayout.finishRefresh();
                    mEmptyView.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                    mRefreshLayout.setVisibility(!result.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    ToastUtils.showShort(response.message);
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    /**
     * 工作站评价
     */
    public void getSiteData() {
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("op", "querySiteCommentInfo"));
        valuePairs.add(new BasicNameValuePair("site_id", site_id));//10全部 20 已回复 30 未回复

        ApiService.doGetConsultationBuyStudioServlet(valuePairs, new ApiCallbackWrapper<String>() {

            @Override
            public void onResponse(String response) {
                super.onResponse(response);

            }
        }, this);
    }
}