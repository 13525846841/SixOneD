package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yksj.consultation.basic.BaseTabActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.doctor.MyConsultationSubFragment;
import com.yksj.consultation.sonDoc.R;

import butterknife.BindView;

/**
 * Created by HEKL on 2015/9/15.
 * Used for 会诊订单_
 */
public class MyConsultationActivity extends BaseTabActivity {

    @BindView(R.id.view_pager) ViewPager mViewPager;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, MyConsultationActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_myorders;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("在线会诊");
        int personType;
        String position = "0";
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            position = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition();
        }
        if ("0".equals(position)) {
            personType = 0;
        } else {
            personType = 1;
        }
        String[] titles = {"待接单", "待处理", "已完成"};
        getTabLayout().setViewPager(mViewPager, titles, this,
                MyConsultationSubFragment.newInstance(0, personType),
                MyConsultationSubFragment.newInstance(1, personType),
                MyConsultationSubFragment.newInstance(2, personType));
    }
}
