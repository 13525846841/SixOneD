package com.yksj.consultation.agency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.EventManager;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.view.AgencyAddActiveView;
import com.yksj.consultation.bean.AgencyActiveBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.EAgencyRefresh;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

public class AgencyAddActiveActivity extends BaseTitleActivity implements AgencyAddActiveView.IPresenter {

    private AgencyAddActiveView mView;
    private String mAgencyId;
    private boolean isAlter; // 是否是修改

    public static Intent getCallingIntent(Context context, String agencyId) {
        Intent intent = new Intent(context, AgencyAddActiveActivity.class);
        intent.putExtra(AgencyConst.ID_EXTRA, agencyId);
        return intent;
    }

    public static Intent getCallingIntent(Context context, String agencyId, AgencyActiveBean active) {
        Intent intent = new Intent(context, AgencyAddActiveActivity.class);
        intent.putExtra(AgencyConst.ACTIVE_EXTRA, active);
        intent.putExtra(AgencyConst.ID_EXTRA, agencyId);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyAddActiveView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("添加活动");
        mAgencyId = getIntent().getStringExtra(AgencyConst.ID_EXTRA);
        AgencyActiveBean activeBean = getIntent().getParcelableExtra(AgencyConst.ACTIVE_EXTRA);
        isAlter = activeBean != null;
        // 不为null说明市修改
        activeBean = activeBean == null ? new AgencyActiveBean() : activeBean;
        activeBean.AGENCY_ID = mAgencyId;
        mView.bindData(activeBean);
    }

    @Override
    public void submitActive(AgencyActiveBean active) {
        if (!checkParams(active)) {
            return;
        }
        if (isAlter) {// 修改
            ApiService.agencyActiveAlter(active.AGENCY_ID,
                    active.ACTIV_CODE,
                    active.ACTIV_TITLE,
                    active.ACTIV_TIME_DESC,
                    active.ACTIV_DESC,
                    new ApiCallbackWrapper<ResponseBean>(true) {
                        @Override
                        public void onResponse(ResponseBean response) {
                            super.onResponse(response);
                            if (response.isSuccess()) {
                                EventManager.post(new EAgencyRefresh());
                                finish();
                            }
                            ToastUtils.showShort(response.message);
                        }
                    });
        } else {// 添加
            ApiService.agencyActiveSubmit(active.AGENCY_ID,
                    active.ACTIV_CODE,
                    active.ACTIV_TITLE,
                    active.ACTIV_TIME_DESC,
                    active.ACTIV_DESC,
                    new ApiCallbackWrapper<ResponseBean>(true) {
                        @Override
                        public void onResponse(ResponseBean response) {
                            super.onResponse(response);
                            if (response.isSuccess()) {
                                EventManager.post(new EAgencyRefresh());
                                finish();
                            }
                            ToastUtils.showShort(response.message);
                        }
                    });
        }
    }

    private boolean checkParams(AgencyActiveBean active) {
        if (!TextUtils.isEmpty(active.ACTIV_TITLE)) {
            if (!TextUtils.isEmpty(active.ACTIV_TIME_DESC)) {
                if (!TextUtils.isEmpty(active.ACTIV_DESC)) {
                    return true;
                } else {
                    ToastUtils.showShort("请输入活动介绍");
                }
            } else {
                ToastUtils.showShort("请输入活动时间");
            }
        } else {
            ToastUtils.showShort("请输入活动名称");
        }
        return false;
    }
}
