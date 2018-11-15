package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.constant.Constant;

import java.util.ArrayList;

/**
 * Created by ${chen} on 2017/7/7.
 */
public class InviteMemFragment extends BaseFragment implements ViewPager.OnPageChangeListener, android.widget.RadioGroup.OnCheckedChangeListener {
    private ViewPager mPager;
    private RadioGroup mGroup;
    private String mStationId;

    public static InviteMemFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(Constant.Station.STATION_ID, id);

        InviteMemFragment fragment = new InviteMemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.invite_mamber, container, false);
        mGroup = (RadioGroup) view.findViewById(R.id.radio_group1);
        mGroup.setOnCheckedChangeListener(this);
        mPager = (ViewPager) view.findViewById(R.id.viewpager1);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getChildFragmentManager());
        mPager.setAdapter(mAdpater);
        mPager.setOnPageChangeListener(this);
        ArrayList<Fragment> mlList = new ArrayList<Fragment>();

        mStationId = getArguments().getString(Constant.Station.STATION_ID);

        //邀请的
        Fragment seviceing = InviteMenListFragment.newInstance("0", mStationId);
        mlList.add(seviceing);

        //申请的
        Fragment serviceFragment = InviteMenListFragment.newInstance("1", mStationId);
        mlList.add(serviceFragment);

        mAdpater.bindFragment(mlList);
        mPager.setCurrentItem(0, false);
        return view;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton mButton = (RadioButton) mGroup.getChildAt(position);
        mButton.setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) mPager.setCurrentItem(i, false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
