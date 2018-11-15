package com.yksj.consultation.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yksj.consultation.sonDoc.friend.FindFriendListFragment;

import java.util.ArrayList;

/**
 * 找病友列表ViewPager的PagerAdapter
 * 
 * @author lmk
 * 
 */
public class BaseTabPagerAdpater extends FragmentStatePagerAdapter {
	FragmentManager fm;
	ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	public FindFriendListFragment activeFragment;
	public BaseTabPagerAdpater(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}
	
	public void bindFragment(ArrayList<Fragment> fragments){
		this.fragments.clear();
		this.fragments.addAll(fragments);
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}


	@Override
	public int getCount() {
		return fragments.size();
	}


}
