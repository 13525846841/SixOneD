package com.yksj.consultation.agency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yksj.consultation.agency.constant.AgencyCategroy;
import com.yksj.consultation.agency.constant.AgencyCategroy.Categroy;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.view.AgencyCategroyView;
import com.yksj.consultation.basic.BaseTabActivity;

import org.jetbrains.annotations.NotNull;

/**
 * 找机构首页分类
 */
public class AgencyCategroyActivity extends BaseTabActivity implements AgencyCategroyView.IPresenter {

    private String mCategroy;
    private String mAreaCode;
    private AgencyCategroyView mView;

    public static Intent getCallingIntent(Context context, String areaCode, @Categroy String categroy) {
        Intent intent = new Intent(context, AgencyCategroyActivity.class);
        intent.putExtra(AgencyConst.CATEGROY_EXTRA, categroy);
        intent.putExtra(AgencyConst.AREA_CODE_EXTRA, areaCode);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyCategroyView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mCategroy = getIntent().getStringExtra(AgencyConst.CATEGROY_EXTRA);
        mAreaCode = getIntent().getStringExtra(AgencyConst.AREA_CODE_EXTRA);
        setTitleByCategroy(mCategroy);
        mView.initialize(mCategroy, mAreaCode);
        mTabLayout.setViewPager(mView.getPager());
    }

    public void setTitleByCategroy(@NotNull String categroy) {
        if (AgencyCategroy.EXPERIENCE.equals(categroy)) {
            setTitle("体验中心");
        } else if (AgencyCategroy.EXPAND.equals(categroy)) {
            setTitle("拓展中心");
        } else if (AgencyCategroy.REHABILITATION.equals(categroy)) {
            setTitle("康复中心");
        } else if (AgencyCategroy.INTEREST.equals(categroy)) {
            setTitle("兴趣中心");
        }
    }
}
