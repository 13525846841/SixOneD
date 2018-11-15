package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.bean.CommentSubTab;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.order.SubCommentFeagment;

import java.util.ArrayList;

/**
 * 我的评价界面
 */
public class MyCommentActivity extends BaseTitleActivity implements RadioGroup.OnCheckedChangeListener {
    private ViewPager mPager;
    private RadioGroup mGroup;
    private BaseTabPagerAdpater mAdpater;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, MyCommentActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_my_assess;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("我的评价");
        mGroup = (RadioGroup) findViewById(R.id.radio_group1);
        mGroup.setOnCheckedChangeListener(this);
        mPager = (ViewPager) findViewById(R.id.viewpager1);
        mAdpater = new BaseTabPagerAdpater(getSupportFragmentManager());
        mPager.setAdapter(mAdpater);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                RadioButton mButton = (RadioButton) mGroup.getChildAt(position);
                mButton.setChecked(true);
            }
        });
        ArrayList<Fragment> fragments = new ArrayList<>();
        CommentSubTab tab = new CommentSubTab();
        tab.tag = SubCommentFeagment.ALREADY;
        fragments.add(SubCommentFeagment.newInstance(tab));

        tab = new CommentSubTab();
        tab.tag = SubCommentFeagment.NOT;
        fragments.add(SubCommentFeagment.newInstance(tab));
        mAdpater.bindFragment(fragments);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked())
                mPager.setCurrentItem(i, false);
        }
    }
}
