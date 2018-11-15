package com.yksj.consultation.doctor;

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

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.order.MyOutpatientSubFragment;

import java.util.ArrayList;

/**
 * 医生服务
 *
 * @author zheng
 */
@SuppressLint("ValidFragment")
public class PDoctorServiceFragment extends BaseFragment implements OnPageChangeListener, android.widget.RadioGroup.OnCheckedChangeListener {

    private Context context;
    private ViewPager mPager;
    private RadioGroup mGroup;

    public PDoctorServiceFragment(Context context) {
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(context).inflate(R.layout.doctor_service_fragment, container, false);
        mGroup = (RadioGroup) view.findViewById(R.id.radio_group1);
        mGroup.setOnCheckedChangeListener(this);
        mPager = (ViewPager) view.findViewById(R.id.viewpager1);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getChildFragmentManager());
        mPager.setAdapter(mAdpater);
        mPager.setOnPageChangeListener(this);
        ArrayList<Fragment> mlList = new ArrayList<Fragment>();

        //0-服务中
        Fragment seviceing = new MyOutpatientSubFragment();
        Bundle e = new Bundle();
        e.putString("type", "0");
        seviceing.setArguments(e);
        mlList.add(seviceing);

        //1-待服务
        Fragment serviceFragment = new MyOutpatientSubFragment();
        Bundle b = new Bundle();
        b.putString("type", "1");
        serviceFragment.setArguments(b);
        mlList.add(serviceFragment);


        mAdpater.bindFragment(mlList);
        mPager.setCurrentItem(0, false);
        return view;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        RadioButton mButton = (RadioButton) mGroup.getChildAt(arg0);
        mButton.setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) mPager.setCurrentItem(i, false);
        }
    }
}
