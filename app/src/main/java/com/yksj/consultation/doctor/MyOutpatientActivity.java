package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yksj.consultation.basic.BaseTabActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.order.MyOutpatientSubFragment;

import butterknife.BindView;

/**
 * 我的门诊预约订单
 */
public class MyOutpatientActivity extends BaseTabActivity {

    @BindView(R.id.view_pager) ViewPager mViewPager;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, MyOutpatientActivity.class);
        return intent;
    }

    @Override public int createLayoutRes() {
        return R.layout.activity_my_outpatient;
    }

    @Override public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("门诊预约");
        String[] titls = {"已预约", "历史"};
        getTabLayout().setViewPager(mViewPager, titls, this,
                MyOutpatientSubFragment.newInstance("0"),
                MyOutpatientSubFragment.newInstance("1"));
    }
}
