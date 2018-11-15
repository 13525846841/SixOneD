package com.yksj.consultation.agency.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseFragment;
import com.library.base.widget.ScrollableHelper;
import com.yksj.consultation.agency.AgencyAddActiveActivity;
import com.yksj.consultation.agency.constant.AgencyCategroy.Categroy;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.constant.AgencyInfoType;
import com.yksj.consultation.agency.view.AgencyActiveView;
import com.yksj.consultation.bean.AgencyActiveBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.EAgencyRefresh;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 机构详情子页面活动
 */
public class AgencyActiveFragment extends BaseFragment implements ScrollableHelper.ScrollableContainer, AgencyActiveView.IPresenter {

    private AgencyActiveView mView;
    // 机构ID
    private String mInfoId;

    public static AgencyActiveFragment newInstance(String infoId, @Categroy String categroy) {

        Bundle args = new Bundle();
        args.putString(AgencyConst.ID_EXTRA, infoId);
        args.putString(AgencyConst.CATEGROY_EXTRA, categroy);

        AgencyActiveFragment fragment = new AgencyActiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyActiveView(getContext(), this);
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        mInfoId = getArguments().getString(AgencyConst.ID_EXTRA);
        requestActive();
    }

    @Override
    public View getScrollableView() {
        return mView.getScrollableView();
    }

    /**
     * 刷新活动列表
     * @param e
     */
    @Subscribe
    public void onRefreshData(EAgencyRefresh e){
        requestActive();
    }

    /**
     * 获取活动
     */
    private void requestActive() {
        ApiService.agencyInfo(mInfoId, AgencyInfoType.ACTIVE, new ApiCallbackWrapper<ResponseBean<List<AgencyActiveBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<AgencyActiveBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<AgencyActiveBean> result = response.result;
                    mView.bindData(result);
                }
            }
        });
    }

    @Override
    public void toActiveInfo(@NotNull AgencyActiveBean activeBean) {
    }

    @Override
    public void toAlterActive(@NotNull AgencyActiveBean activeBean) {
        Intent intent = AgencyAddActiveActivity.getCallingIntent(getContext(), mInfoId, activeBean);
        startActivity(intent);
    }

    @NotNull
    @Override
    public String getCategroy() {
        return getArguments().getString(AgencyConst.CATEGROY_EXTRA);
    }
}
