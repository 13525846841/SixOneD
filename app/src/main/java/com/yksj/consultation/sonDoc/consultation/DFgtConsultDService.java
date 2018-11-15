/**
 * 
 */
package com.yksj.consultation.sonDoc.consultation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;

/**
 * 医生端会诊服务Fragment
 * 
 * @author HEKL
 * 
 */
@SuppressLint("ValidFragment")
public class DFgtConsultDService extends BaseFragment implements
		OnCheckedChangeListener, OnPageChangeListener {

	private Context context;
	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	public DFgtConsultDService(Context context) {
		super();
		this.context = context;
	}

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.from(context).inflate(
				R.layout.fgt_basicdoctor_service, container, false);
		initView(view);
		return view;
	}

	private void initView(View v) {
		mRadioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
		mRadioGroup.setOnCheckedChangeListener(this);
		mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
		BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(
				getChildFragmentManager());
		mViewPager.setAdapter(mAdpater);
		mViewPager.setOnPageChangeListener(this);
		ArrayList<Fragment> mList = new ArrayList<Fragment>();

		/**
		 * 医生端我的服务
		 */
		// 全部
		Fragment allFragment = new DFgtConsultMyServiceList();
		Bundle S = new Bundle();
		S.putInt("typeList", 10);
		allFragment.setArguments(S);
		mList.add(allFragment);

		// 待接诊
		Fragment acceptFragment = new DFgtConsultMyServiceList();
		Bundle A = new Bundle();
		A.putInt("typeList", 11);
		acceptFragment.setArguments(A);
		mList.add(acceptFragment);
		// 填病历
		Fragment fillFragment = new DFgtConsultMyServiceList();
		Bundle B = new Bundle();
		B.putInt("typeList", 12);
		fillFragment.setArguments(B);
		mList.add(fillFragment);
		// 待同意
		Fragment waitingagreeFragment = new DFgtConsultMyServiceList();
		Bundle C = new Bundle();
		C.putInt("typeList", 13);
		waitingagreeFragment.setArguments(C);
		mList.add(waitingagreeFragment);
		mAdpater.bindFragment(mList);
		// 待付款
		Fragment payingFragment = new DFgtConsultMyServiceList();
		Bundle D = new Bundle();
		D.putInt("typeList", 14);
		payingFragment.setArguments(D);
		mList.add(payingFragment);
		mAdpater.bindFragment(mList);
		// 待服务
		Fragment serveringFragment = new DFgtConsultMyServiceList();
		Bundle E = new Bundle();
		E.putInt("typeList", 15);
		serveringFragment.setArguments(E);
		mList.add(serveringFragment);
		mAdpater.bindFragment(mList);
		mViewPager.setCurrentItem(0, false);
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
//		FriendHttpUtil.chatFromPerson(activity, entity)
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
