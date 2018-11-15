package com.yksj.consultation.basic;

import com.library.base.base.BaseTitleActivity;;
import com.library.base.widget.SlidingTabLayout;
import com.yksj.consultation.sonDoc.R;

import butterknife.BindView;

public class BaseTabActivity extends BaseTitleActivity {

    @BindView(R.id.tab_layout)
    public SlidingTabLayout mTabLayout;

    @Override
    public int createTitleLayoutRes() {
        return R.layout.activity_base_tab;
    }

    public SlidingTabLayout getTabLayout(){
        return mTabLayout;
    }
}
