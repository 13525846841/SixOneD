package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import butterknife.OnClick;

/**
 * 我的服务
 */
public class DoctorServiceActivity extends BaseTitleActivity {

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, DoctorServiceActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doctor_service;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("我的服务");
    }

    /**
     * 图文咨询
     * @param v
     */
    @OnClick(R.id.service_1_st)
    public void onService1(View v) {
        if (isOpenService()) {
            Intent intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.TW);
            startActivity(intent);
        }
    }

    /**
     * 电话咨询
     * @param v
     */
    @OnClick(R.id.service_2_st)
    public void onService2(View v) {
        if (isOpenService()) {
            Intent intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.DH);
            startActivity(intent);
        }
    }

    /**
     * 包月咨询
     * @param v
     */
    @OnClick(R.id.service_3_st)
    public void onService3(View v) {
        if (isOpenService()) {
            Intent intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.BY);
            startActivity(intent);
        }
    }

    /**
     * 视频咨询
     * @param v
     */
    @OnClick(R.id.service_4_st)
    public void onService4(View v) {
        if (isOpenService()) {
            Intent intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.SP);
            startActivity(intent);
        }
    }

    /**
     * 门诊预约
     * @param v
     */
    @OnClick(R.id.service_5_st)
    public void onService5(View v) {
        if (isOpenService()) {
            Intent intent = DoctorSeeServiceActivity.getCallingIntent(this, "3", "门诊预约");
            startActivity(intent);
        }
    }

    /**
     * 在线会诊
     * @param v
     */
    @OnClick(R.id.service_6_st)
    public void onService6(View v) {
        if (isOpenService()) {
            Intent intent = new Intent(this, OnLineConsultActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 服务是否可打开
     * @return
     */
    private boolean isOpenService() {
        String reviewStatus = DoctorHelper.getReviewStatus();
        if (!reviewStatus.equals("888")) {
            ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
            return false;
        } else {
            return true;
        }
    }
}
