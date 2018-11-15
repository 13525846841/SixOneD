package com.yksj.consultation.agency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.agency.constant.AgencyCategroy;
import com.yksj.consultation.agency.view.AgencyCategroySubView;
import com.yksj.consultation.bean.AgencyBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 我的机构
 */
public class AgencySelfActivity extends BaseTitleActivity implements AgencyCategroySubView.IPresenter {

    private AgencyCategroySubView mView;
    private int mPageIndex;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, AgencySelfActivity.class);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyCategroySubView(this, this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("我的机构");
    }

    @Override
    public void toAgencyInfo(@NotNull AgencyBean bean) {
        Intent intent = AgencyInfoActivity.getCallingIntent(this, bean.id, AgencyCategroy.SELF);
        startActivity(intent);
    }

    @NotNull
    @Override
    public String getCategroy() {
        return AgencyCategroy.SELF;
    }

    @Override
    public void requestData(boolean isMore) {
        if (!isMore) {
            mPageIndex = 1;
        }
        ApiService.agencySelf(DoctorHelper.getId(), mPageIndex,
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
                });
    }
}
