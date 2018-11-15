package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.comm.BaseListFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.salon.HealthTopicListFragment;

import java.util.ArrayList;


/**
 * 健康话题主界面
 * 聊聊病情----改名为健康话题
 * @author lmk
 */
public class HealthTopicMainUi extends BaseViewPagerActivtiy implements OnClickListener, OnCheckedChangeListener {

    private ViewPager viewpager;
    private RadioGroup mChargeGroup;//关于医生话题和生活话题的RadioGroup,
    ArrayList<Fragment> mlList;//医生话题里面放的3个Fragment
    BaseListFragment currentFragment;//当前的Fragment
    HealthTopicListFragment friendFragment;//病友话题Fragment
    private FrameLayout friendlayout;
    private int currentPage = 0;

    //存放子fragment改变的关注状态

    @Override
    public int createLayoutRes() {
        return R.layout.health_topic_main_ui;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
        initViewPager();
    }

    /**
     * 初始化Viewpager
     */
    private void initViewPager() {
        mlList = new ArrayList<Fragment>();
        mlList.add(HealthTopicListFragment.newInstance(2));//全部
        mlList.add(HealthTopicListFragment.newInstance(0));//免费
        mlList.add(HealthTopicListFragment.newInstance(1));//收费

        BaseTabPagerAdpater mPagerAdapter = new BaseTabPagerAdpater(getSupportFragmentManager());
        mPagerAdapter.bindFragment(mlList);
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentPage = arg0;
                mChargeGroup.check(mChargeGroup.getChildAt(arg0).getId());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        leftRadio.setText(R.string.topic_of_doctor);
        rightRadio.setText(R.string.topic_of_friend);
        titleLeftBtn.setOnClickListener(this);
        leftRadio.setOnClickListener(this);
        leftRadio.setChecked(true);
        rightRadio.setOnClickListener(this);
        titleRightBtn.setVisibility(View.VISIBLE);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn.setBackgroundResource(R.drawable.ig_seach);
        titleRightBtn2.setBackgroundResource(R.drawable.ig_create);
        titleRightBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        viewpager = (ViewPager) findViewById(R.id.health_topic_viewpager);
        friendlayout = (FrameLayout) findViewById(R.id.health_topic_friend_layout);
        mChargeGroup = (RadioGroup) findViewById(R.id.health_topic_radiogroup_selector);
        mChargeGroup.setOnCheckedChangeListener(this);
    }


    //点击事件
    @Override
    public void onClick(View v) {
        Intent intent = null;
        FragmentTransaction ft = null;
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.left_radio:
                mChargeGroup.setVisibility(View.VISIBLE);
                viewpager.setVisibility(View.VISIBLE);
                currentFragment = (BaseListFragment) mlList.get(viewpager.getCurrentItem());
                currentFragment.isVisiable = true;//使当前的额是否可见属性为true,可以接受广播
                if (friendFragment != null) {
                    ft = getSupportFragmentManager().beginTransaction();
                    friendFragment.isVisiable = false;//使当前的额是否可见属性为false,不可以接受广播
                    ft.hide(friendFragment);
                    ft.commit();
                }
                break;
            case R.id.right_radio:
                mChargeGroup.setVisibility(View.GONE);
                viewpager.setVisibility(View.GONE);
                currentFragment = (BaseListFragment) mlList.get(viewpager.getCurrentItem());
                currentFragment.isVisiable = false;
                ft = getSupportFragmentManager().beginTransaction();
                if (friendFragment == null) {
                    friendFragment = HealthTopicListFragment.newInstance(100);
                    ft.add(R.id.health_topic_friend_layout, friendFragment);
                } else {
                    friendFragment.onStart();
                    ft.show(friendFragment);
                }
                friendFragment.isVisiable = true;
                ft.commit();
                break;

            case R.id.title_right://搜索
                intent = new Intent(HealthTopicMainUi.this, TopicSearchMainUi.class);
                startActivity(intent);
                break;
            case R.id.title_right2://创建话题
                intent = new Intent(HealthTopicMainUi.this, CreateTopicInfoUI.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 切换radioButton时的事件,是ViewPager也随之切换
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < mlList.size(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                if (currentPage == i)
                    return;
                currentPage = i;
                viewpager.setCurrentItem(currentPage);
            }
        }
    }

}
