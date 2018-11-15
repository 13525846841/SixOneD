package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.dossier.AtyDossierSearch;
import com.yksj.consultation.sonDoc.dossier.FgtHistoryDossier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEKL on 2015/7/8.
 * Used for 病历讨论
 */
public class DAtyCasehistoryDiscussion extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ViewPager mViewPager;
    private FgtHistoryDossier mFargment1, mFargment2, mFargment3;
    private Bundle bun1, bun2, bun3;
    private PConsultServiceAdapter mAdapter;
    private List<Fragment> mFargments;
    private RadioGroup mRadiogroup;
    private Intent intent;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_casehistorydiscussion);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("我的病历");
        titleRightBtn2.setVisibility(View.VISIBLE);
//        titleRightBtn.setVisibility(View.VISIBLE);
//        titleRightBtn.setBackgroundResource(R.drawable.ig_seach);
//        titleRightBtn.setOnClickListener(this);
//        titleRightBtn2.setBackgroundResource(R.drawable.ig_create);
        titleRightBtn2.setBackgroundResource(R.drawable.ig_seach);
        titleRightBtn2.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.dossier_vp);
        mViewPager.setOffscreenPageLimit(0);
        mRadiogroup = (RadioGroup) findViewById(R.id.dossier_rg);
        initFrag();
    }

    private void initFrag() {
        mFargments = new ArrayList<Fragment>();
        mFargments.clear();
        mFargment1 = new FgtHistoryDossier();
        bun1 = new Bundle();
        bun1.putInt("DOSSIER", 112);
        mFargment1.setArguments(bun1);
        mFargments.add(mFargment1);

        mFargment2 = new FgtHistoryDossier();
        bun2 = new Bundle();
        bun2.putInt("DOSSIER", 113);
        mFargment2.setArguments(bun2);
        mFargments.add(mFargment2);

        mFargment3 = new FgtHistoryDossier();
        bun3 = new Bundle();
        bun3.putInt("DOSSIER", 114);
        mFargment3.setArguments(bun3);
        mFargments.add(mFargment3);
        mAdapter = new PConsultServiceAdapter(getSupportFragmentManager(), mFargments);
        mViewPager.setAdapter(mAdapter);
        initFunction();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right://搜索
//                intent = new Intent(this, AtyDossierSearch.class);
//                startActivity(intent);
                break;
            case R.id.title_right2://上传
//                ToastUtil.showToastPanl("敬请期待");
//                intent = new Intent(this, DoctorCreateCaseActivity.class);
//                startActivity(intent);
                intent = new Intent(this, AtyDossierSearch.class);//搜索
                startActivity(intent);
                break;
        }
    }

    private void initFunction() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mRadiogroup.check(mRadiogroup.getChildAt(arg0).getId());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mRadiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        for (int j = 0; j < mRadiogroup.getChildCount(); j++) {
            if (mRadiogroup.getChildAt(j).getId() == i) {
                if (mViewPager.getCurrentItem() != j) {
                    mViewPager.setCurrentItem(j, false);
                }
                break;
            }
        }
    }
}
