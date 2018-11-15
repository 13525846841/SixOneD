package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.dossier.DoctorSelectPatientActivity;

import java.util.ArrayList;

/**
 * @author HEKL
 *         <p/>
 *         会诊医生会诊服务
 */
public class DAtyAssistantConsultService extends BaseActivity implements OnCheckedChangeListener,
        OnPageChangeListener, OnClickListener {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fgt_basicdoctor_service);
        initView();
    }

    private void initView() {//初始化
        initializeTitle();
        titleTextV.setText("病历夹");
        titleRightBtn2.setText("创建病历");
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setBackgroundResource(R.drawable.create_case);
        titleLeftBtn.setOnClickListener(this);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getSupportFragmentManager());
        mViewPager.setAdapter(mAdpater);
        mViewPager.setOnPageChangeListener(this);
        ArrayList<Fragment> mList = new ArrayList<Fragment>();
        /**
         * 医生端我的服务
         */
        // 待会诊
        Fragment allFragment = new DFgtConsultMyServiceList();
        Bundle S = new Bundle();
        S.putInt("typeList", 10);
        allFragment.setArguments(S);
        mList.add(allFragment);

        // 会诊中
        Fragment acceptFragment = new DFgtConsultMyServiceList();
        Bundle A = new Bundle();
        A.putInt("typeList", 11);
        acceptFragment.setArguments(A);
        mList.add(acceptFragment);
        // 已完成
        Fragment fillFragment = new DFgtConsultMyServiceList();
        Bundle B = new Bundle();
        B.putInt("typeList", 12);
        fillFragment.setArguments(B);
        mList.add(fillFragment);
        mAdpater.bindFragment(mList);
        mViewPager.setCurrentItem(0, false);
//        AnimationUtils.startGuiPager(this, getClass().getAccounts());
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                i = new Intent(this, DoctorSelectPatientActivity.class);
                startActivity(i);
                break;
        }
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

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // FriendHttpUtil.chatFromPerson(activity, entity)
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        RadioButton mButton = (RadioButton) mRadioGroup.getChildAt(arg0);
        mButton.setChecked(true);
    }

}
