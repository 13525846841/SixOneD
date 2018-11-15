package com.yksj.consultation.doctor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.setting.SettingMainFragment;
import com.yksj.consultation.sonDoc.R;

/**
 * 
 * 设置,信息厅
 * @author jack_tang
 *
 */
public class ContentActivity extends BaseTitleActivity {
	
	@Override public int createLayoutRes() {
		return R.layout.fragment_content_layout;
	}

	@Override public void initialize(Bundle bundle) {
		super.initialize(bundle);
		setTitle("设置");

		Fragment fragment;
		fragment = new SettingMainFragment();
		FragmentManager fg = getSupportFragmentManager();
		FragmentTransaction ft = fg.beginTransaction();
		ft.replace(R.id.fragmentcontent,fragment);
		ft.commit();
	}
}
