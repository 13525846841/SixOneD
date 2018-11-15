package com.yksj.consultation.agency.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseFragment;
import com.library.base.widget.ScrollableHelper;
import com.yksj.consultation.agency.AgencyInfoActivity;
import com.yksj.consultation.agency.constant.AgencyCategroy;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.constant.AgencyType;
import com.yksj.consultation.agency.view.AgencyCategroySubView;
import com.yksj.consultation.bean.AgencyBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.Request;

/**
 * 机构类型子页面
 */
public class AgencyCategroySubFragment extends BaseFragment implements ScrollableHelper.ScrollableContainer, AgencyCategroySubView.IPresenter {

    private String mCategroy;
    private String mType;
    private String mAreaCode;
    private AgencyCategroySubView mView;
    private int mPageIndex = 1;

    public static AgencyCategroySubFragment newInstance(String categroy, @AgencyType.Type String type, String areaCode) {
        Bundle args = new Bundle();
        args.putString(AgencyConst.CATEGROY_EXTRA, categroy);
        args.putString(AgencyConst.TYPE_EXTRA, type);
        args.putString(AgencyConst.AREA_CODE_EXTRA, areaCode);

        AgencyCategroySubFragment fragment = new AgencyCategroySubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyCategroySubView(getContext(), this);
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        mCategroy = getArguments().getString(AgencyConst.CATEGROY_EXTRA);
        mType = getArguments().getString(AgencyConst.TYPE_EXTRA);
        mAreaCode = getArguments().getString(AgencyConst.AREA_CODE_EXTRA);
    }

    /**
     * 刷新数据
     * @param areaCode
     */
    public void refreshData(String areaCode) {
        if (AgencyType.NEAR.equals(mType)) {//根据地区ID查询数据
            this.mAreaCode = areaCode;
        }
        requestData(false);
    }

    @Override
    public View getScrollableView() {
        return mView.getScrollableView();
    }

    @Override
    public void toAgencyInfo(@NotNull AgencyBean bean) {
        Intent intent = AgencyInfoActivity.getCallingIntent(getContext(), bean.id, getCategroy());
        startActivity(intent);
    }

    @NotNull
    @Override
    public String getCategroy() {
        return getArguments().getString(AgencyConst.CATEGROY_EXTRA);
    }

    @Override
    public void requestData(boolean isMore) {
        String center;
        if (AgencyCategroy.EXPERIENCE.equals(mCategroy)) {
            center = "1";
        } else if (AgencyCategroy.EXPAND.equals(mCategroy)) {
            center = "2";
        } else if (AgencyCategroy.REHABILITATION.equals(mCategroy)) {
            center = "3";
        } else if (AgencyCategroy.INTEREST.equals(mCategroy)) {
            center = "4";
        } else {
            center = "";
        }
        if (!isMore) {
            mPageIndex = 1;
        }
        ApiService.agencyList(mAreaCode,
                center,
                mType,
                mPageIndex,
                new ApiCallbackWrapper<ResponseBean<List<AgencyBean>>>() {
                    @Override
                    public void onResponse(ResponseBean<List<AgencyBean>> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            List<AgencyBean> result = response.result;
                            mPageIndex++;
                            if (isMore) {
                                mView.addData(result);
                            } else {
                                mView.setNewData(result);
                            }
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        mView.error(isMore);
                    }
                });
    }
}
