package com.yksj.consultation.sonDoc.doctor;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yksj.consultation.adapter.DoctorCommentAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.DoctorCommentListBean;
import com.yksj.consultation.bean.DoctorCommentListBean.CommentListBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.constant.Constant;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import java.util.List;

/**
 * 医生评论列表界面
 * Created by lmk on 15/10/21.
 */
public class DoctorCommentListActivity extends BaseListActivity {

    private String mDoctorId;

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setTitle("全部评论");
    }

    @Override
    public void initialize(Bundle bundle) {
        mDoctorId = getIntent().getStringExtra(Constant.USER_ID);
        super.initialize(bundle);
    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new DoctorCommentAdapter();
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.OkHttpDoctorHomeCommentList(pageIndex, mDoctorId, createSimpleCallback(DoctorCommentListBean.class));
    }

    @Override
    public <Result> ApiCallbackWrapper createSimpleCallback(Class<Result> item) {
        return new ApiCallbackWrapper<String>() {
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                ResponseBean<DoctorCommentListBean> response = GsonSerializer.fromJsonObject(json, DoctorCommentListBean.class);
                if (response.isSuccess()) {
                    mPageIndex++;//加载成功页面角标自增
                    List<CommentListBean> result = response.result.commentList;
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {

    }
}
