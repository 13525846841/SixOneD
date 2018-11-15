package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.basic.BaseTabActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.doctor.constant.OrderType;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;

import butterknife.BindView;

/**
 * 图文咨询，电话咨询等内容详情界面
 */
public class MyOrderActivity extends BaseTabActivity {
    public static final String TITLE = "TITLE";

    @BindView(R.id.view_pager)
    ViewPager mViewpager;

    private BaseTabPagerAdpater mPagerAdapter;
    @ServiceType.Type private String mServiceType;

    public static Intent getCallingIntent(Context context, String title, @ServiceType.Type String serviceType) {
        Intent intent = new Intent(context, MyOrderActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(Constant.Station.SERVICE_TYPE_ID, serviceType);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_aty_my_order;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        String title = getIntent().getStringExtra(TITLE);
        mServiceType = getIntent().getStringExtra(Constant.Station.SERVICE_TYPE_ID);
        setTitle(title);
        initView();
    }

    private void initView() {
        mPagerAdapter = new BaseTabPagerAdpater(getSupportFragmentManager());
        mViewpager.setAdapter(mPagerAdapter);
        String[] titles = {" 待处理", "已完成"};
        mTabLayout.setViewPager(mViewpager, titles, this,
                MyOrderFragment.newInstance(mServiceType, OrderType.PROCESS),
                MyOrderFragment.newInstance(mServiceType, OrderType.FINISH));
    }
}
