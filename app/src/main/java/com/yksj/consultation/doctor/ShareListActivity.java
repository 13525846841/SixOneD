package com.yksj.consultation.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.ShareListAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.DoctorShareBean;
import com.yksj.consultation.bean.DoctorShareCommentBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.event.EShareSucees;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 名医分享列表
 */
public class ShareListActivity extends BaseTitleActivity implements ShareListAdapter.OnDoctorShareAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyLayout;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private int mPageIndex = 1;
    private ShareListAdapter mAdapter;
    private String mUserId;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doctor_share_list;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("名医分享");
        initializeView();
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        mUserId = getIntent().getStringExtra(Constant.USER_ID);
        if (TextUtils.isEmpty(mUserId) || DoctorHelper.getId().equals(mUserId)) {
            setRight(R.drawable.photo, v -> {
                Intent intent = new Intent(ShareListActivity.this, ShareSubmitActivity.class);
                startActivity(intent);
            });
        }
    }

    private void initializeView() {
        mRefreshLayout.setRefreshHeader(new SimpleRefreshHeader(this));
        mRefreshLayout.setOnRefreshListener(view -> requestData(false));
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL));
        mAdapter = new ShareListAdapter();
        mAdapter.setOnCommentClickListener(this);
        mAdapter.setOnLoadMoreListener(() -> requestData(true), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        //禁用动画，解决点赞刷新Item闪屏问题
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 发布分享成功事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefresh(EShareSucees event) {
        requestData(false);
    }

    @Override
    public void onCommentClick(View view, int position, final DoctorShareBean doctorShare, final DoctorShareCommentBean comment) {
        final int finalPosition = position;
        String contentHint = comment != null ? String.format("回复 %s", comment.CUSTOMER_NAME) : "";
        DialogManager.getInputDialog()
                .setContentHint(contentHint)
                .setOnCommentClickListener((dialog, view1, content) -> {
                    String shareId = doctorShare.SHARE_ID;
                    String customerId = comment != null ? comment.CUSTOMER_ID : "";
                    requestAddComment(customerId, content, shareId, finalPosition);
                    dialog.dismiss();
                })
                .show(getSupportFragmentManager());
    }

    @Override
    public void onDeleteClick(View view, int position, DoctorShareBean doctorShare) {
        String shareId = doctorShare.SHARE_ID;
        requestDelete(shareId, position);
    }

    @Override
    public void onLikeClick(View view, int position, DoctorShareBean doctorShare) {
        requestLike(doctorShare, position);
    }

    @Override
    public void onUserClick(View view, String doctorId) {
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.putExtra(Constant.Station.USER_ID, doctorId);
        startActivity(intent);
    }

    /**
     * 请求数据根据userid是否为空
     * @param isMore
     */
    private void requestData(boolean isMore) {
        if (TextUtils.isEmpty(mUserId))
            requestDataAll(isMore);
        else
            requestDataById(mUserId, isMore);
    }

    /**
     * 请求数据
     * @param isMore
     */
    private void requestDataAll(boolean isMore) {
        if (!isMore) {
            mPageIndex = 1;
        }
        ApiService.OkHttpShareList(mPageIndex, new ApiCallbackWrapper<ResponseBean<List<DoctorShareBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<DoctorShareBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<DoctorShareBean> result = response.result;
                    mPageIndex++;
                    if (isMore) {
                        if (result != null && result.isEmpty()) {
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.addData(response.result);
                            mAdapter.loadMoreComplete();
                        }
                    } else {
                        if (response.result.isEmpty()) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.setNewData(response.result);
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                }
            }
        });
    }

    /**
     * 根据医生Id请求数据
     * @param isMore
     * @param mUserId 获取的用户id
     */
    private void requestDataById(String mUserId, final boolean isMore) {
        if (!isMore) {
            mPageIndex = 1;
        }
        ApiService.OkHttpDoctorShareList(mPageIndex, mUserId, new ApiCallbackWrapper<ResponseBean<List<DoctorShareBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<DoctorShareBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<DoctorShareBean> result = response.result;
                    mPageIndex++;
                    if (isMore) {
                        if (result != null && result.isEmpty()) {
                            mAdapter.loadMoreEnd();
                        } else {
                            mAdapter.addData(response.result);
                            mAdapter.loadMoreComplete();
                        }
                    } else {
                        if (response.result.isEmpty()) {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter.setNewData(response.result);
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRefreshLayout.finishRefresh();
                    }
                }
            }
        });
    }

    /**
     * 删除分享
     */
    private void requestDelete(String shareId, final int pos) {
        final int finalPosition = pos;
        ApiService.OkHttpDoctorShareDel(shareId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mAdapter.remove(finalPosition);
                }
                ToastUtils.showShort(response.message);
            }
        });
    }

    /**
     * 点赞  1 点赞  2取消点赞
     */
    private void requestLike(final DoctorShareBean shareBean, final int pos) {
        ApiService.OkHttpDoctorShareLike(shareBean.SHARE_ID, shareBean.isLike(), new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    if (shareBean.isLike()) {//取消点赞
                        shareBean.likeChange(false);
                    } else {//点赞
                        shareBean.likeChange(true);
                    }
                    mAdapter.likeChange(pos);
                }
                ToastUtils.showShort(response.message);
            }
        });
    }

    /**
     * 添加评论
     * @param userId
     * @param content
     * @param shareId
     * @param position
     */
    private void requestAddComment(String userId, final String content, final String shareId, final int position) {
        ApiService.OkHttpDoctorShareCommentAdd(shareId, content, userId, new ApiCallbackWrapper<ResponseBean<DoctorShareCommentBean>>() {
            @Override
            public void onResponse(ResponseBean<DoctorShareCommentBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mAdapter.getItem(position).comment.add(response.result);
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtils.showShort(response.message);
            }
        });
    }
}
