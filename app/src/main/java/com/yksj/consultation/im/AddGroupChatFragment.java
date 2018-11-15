package com.yksj.consultation.im;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.HEvent;
import com.yksj.consultation.sonDoc.order.AddListFragment;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ${chen} on 2016/11/26.
 * 选择群聊成员fragment
 */
public class AddGroupChatFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    private ViewPager mPager;
    private RadioGroup mGroup;
    private int searchType = 0;//0医生 1患者

    private EditText mEditText;

    public AddGroupChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(mActivity).inflate(R.layout.add_fragment, container, false);
        mGroup = (RadioGroup) view.findViewById(R.id.radio_group1);
        mGroup.setOnCheckedChangeListener(this);
        mPager = (ViewPager) view.findViewById(R.id.viewpager1);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getChildFragmentManager());
        mPager.setAdapter(mAdpater);
        mPager.setOnPageChangeListener(this);
        ArrayList<Fragment> mlList = new ArrayList<>();


        mEditText = (EditText) view.findViewById(R.id.include_search).findViewById(R.id.edit_search_top);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditText.getText().toString().length() == 0) {
                    EventBus.getDefault().post(new HEvent("AddListFragment", "", searchType));
                }
            }
        });
        mEditText.setHint("请输入...");
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mEditText.getText().toString().length() > 0) {
                        EventBus.getDefault().post(new HEvent("AddListFragment", mEditText.getText().toString(), searchType));
                    }

                }
                return false;
            }
        });


        //0-医生
        Fragment fragment1 = new AddListFragment();
        Bundle e = new Bundle();
        e.putString("type", "0");
        fragment1.setArguments(e);
        mlList.add(fragment1);

        //1-患者
        Fragment fragment2 = new AddListFragment();
        Bundle b = new Bundle();
        b.putString("type", "1");
        fragment2.setArguments(b);
        mlList.add(fragment2);


        mAdpater.bindFragment(mlList);
        mPager.setCurrentItem(0, false);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) {
                mPager.setCurrentItem(i, true);
                searchType = i;
                Log.e("onPageSelected", searchType + "");
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton mButton = (RadioButton) mGroup.getChildAt(position);
        mButton.setChecked(true);
        searchType = position;
        Log.e("onPageSelected", searchType + "");
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
