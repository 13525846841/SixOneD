package com.yksj.consultation.agency.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.IntentUtils;
import com.library.base.base.BaseFragment;
import com.library.base.widget.ScrollableHelper;
import com.yksj.consultation.agency.AgencyInfoActivity;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.constant.AgencyInfoType;
import com.yksj.consultation.agency.view.AgencyDescView;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.AgencyBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.jetbrains.annotations.NotNull;

/**
 * 机构详情子页面简介
 */
public class AgencyDescFragment extends BaseFragment implements ScrollableHelper.ScrollableContainer, AgencyDescView.IPresenter {

    private AgencyDescView mView;
    private String mInfoId;

    public static AgencyDescFragment newInstance(String infoId, String categroy) {

        Bundle args = new Bundle();
        args.putString(AgencyConst.ID_EXTRA, infoId);
        args.putString(AgencyConst.CATEGROY_EXTRA, categroy);

        AgencyDescFragment fragment = new AgencyDescFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        mInfoId = getArguments().getString(AgencyConst.ID_EXTRA);
        requestDesc();
    }

    @Override
    public View createLayout() {
        return  mView = new AgencyDescView(getContext(), this);
    }

    @Override
    public View getScrollableView() {
        return mView;
    }

    /**
     * 获取简介
     */
    private void requestDesc(){
        ApiService.agencyInfo(mInfoId, AgencyInfoType.DESC, new ApiCallbackWrapper<ResponseBean<AgencyBean>>(true) {
            @Override
            public void onResponse(ResponseBean<AgencyBean> response) {
                super.onResponse(response);
                if (response.isSuccess()){
                    AgencyBean result = response.result;
                    mView.bindData(result);
                    String imgPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + result.avatar;
                    ((AgencyInfoActivity) getActivity()).setAvatar(imgPath);
                }
            }
        });
    }

    @Override
    public void callPhone(@NotNull String telephone) {
        Intent intent = IntentUtils.getDialIntent(telephone);
        startActivity(intent);
    }
}
