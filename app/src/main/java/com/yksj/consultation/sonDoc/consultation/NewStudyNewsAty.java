package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;

/**
 * Created by HEKL on 16/5/20.
 * Used for
 */

public class NewStudyNewsAty extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private ViewPager mViewPager;//滑页
    private RadioGroup mRadioGroup;//页签

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_new_study_news);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("我的订单");
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getSupportFragmentManager());
        mViewPager.setAdapter(mAdpater);
        mViewPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> mList = new ArrayList<Fragment>();
        titleLeftBtn.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);

        // 待处理
        Fragment waitFragment = new CommonToolsFrg();
        Bundle handle = new Bundle();
        handle.putInt("typeList", 0);
        waitFragment.setArguments(handle);
        mList.add(waitFragment);
        // 已完成
        Fragment doneFragment = new CommonToolsFrg();
        Bundle done = new Bundle();
        done.putInt("typeList", 1);
        doneFragment.setArguments(done);
        mList.add(doneFragment);
        mAdpater.bindFragment(mList);
        //显示第一页
        mViewPager.setCurrentItem(0, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton mButton = (RadioButton) mRadioGroup.getChildAt(position);
        mButton.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) {
                mViewPager.setCurrentItem(i, false);
            }
        }
    }

}
