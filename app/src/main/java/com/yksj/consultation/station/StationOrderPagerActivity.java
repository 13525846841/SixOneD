package com.yksj.consultation.station;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.basic.BaseTabActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;

/**
 * 图文咨询，电话咨询等内容详情界面
 */
public class StationOrderPagerActivity extends BaseTabActivity {

    private ViewPager viewpager;
    private BaseTabPagerAdpater mPagerAdapter;

    private String mServiceType = "";
    private String mStationId = "";
    private int mType;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_aty_station_ex_order;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("订单处理");
        initView();
    }

    private void initView() {
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mServiceType = getIntent().getStringExtra(Constant.Station.SERVICE_TYPE_ID);
        mType = getIntent().getIntExtra(Constant.Station.STATION_HOME_TYPE, StationType.STATION_HOME_NORMAL);

        viewpager = (ViewPager) findViewById(R.id.my_plan_frag);

        mPagerAdapter = new BaseTabPagerAdpater(getSupportFragmentManager());
        viewpager.setAdapter(mPagerAdapter);

        setupTab();
    }

    private void setupTab() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        String[] titles = mType == StationType.STATION_HOME_CREATE ?
                new String[]{"待分配", "待服务", "服务中", "已完成"} :
                new String[]{"待抢单", "待服务", "服务中", "已完成"};
        Fragment fragment;
        // 待分配|待抢单
        fragment = mType == StationType.STATION_HOME_CREATE ?
                StationOrderSubFragment.newInstance(mServiceType, Constant.StationOrderStatus.ZZFP, mStationId) :
                StationOrderSubFragment.newInstance(mServiceType, Constant.StationOrderStatus.QDSUCESS, mStationId);
        fragments.add(fragment);
        // 待服务
        fragment = StationOrderSubFragment.newInstance(mServiceType, Constant.StationOrderStatus.QD, mStationId);
        fragments.add(fragment);
        // 服务中
        fragment = StationOrderSubFragment.newInstance(mServiceType, Constant.StationOrderStatus.FWZ, mStationId);
        fragments.add(fragment);
        // 已完成
        fragment = StationOrderSubFragment.newInstance(mServiceType, Constant.StationOrderStatus.OVER, mStationId);
        fragments.add(fragment);
        mPagerAdapter.bindFragment(fragments);
        mTabLayout.setViewPager(viewpager, titles);
        //加载页面全部数据
        viewpager.setOffscreenPageLimit(fragments.size());
    }
}
