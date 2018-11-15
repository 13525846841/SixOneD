package com.yksj.consultation.agency;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.agency.constant.AgencyCategroy;
import com.yksj.consultation.agency.view.AgencyCategroyView;
import com.yksj.consultation.agency.view.AgencyHomeView;
import com.yksj.consultation.bean.AgencyBean;

import org.jetbrains.annotations.NotNull;

/**
 * 机构首页
 */
public class AgencyHomeActivity extends BaseActivity implements AgencyHomeView.IPresenter, AgencyCategroyView.IPresenter {

    private AgencyHomeView mView;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, AgencyHomeActivity.class);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyHomeView(this, this);
    }

    @Override
    public void onApplyJoin() {
        Intent intent = AgencyApplyActivity.getCallingIntent(this);
        startActivity(intent);
    }

    @Override
    public void toSelfAgency() {
        Intent intent = AgencySelfActivity.getCallingIntent(this);
        startActivity(intent);
    }

    @Override
    public void toAgencyInfo(@NotNull AgencyBean bean) {
        Intent intent = AgencyInfoActivity.getCallingIntent(this, bean.id, AgencyCategroy.RECOMMENT);
        startActivity(intent);
    }

    @Override
    public void onExperienceClick(@NotNull View v, @NotNull String areaCode) {
        Intent intent = AgencyCategroyActivity.getCallingIntent(this, areaCode, AgencyCategroy.EXPERIENCE);
        startActivity(intent);
    }

    @Override
    public void onExpandClick(@NotNull View v, @NotNull String areaCode) {
        Intent intent = AgencyCategroyActivity.getCallingIntent(this, areaCode, AgencyCategroy.EXPAND);
        startActivity(intent);
    }

    @Override
    public void onRehabilitationClick(@NotNull View v, @NotNull String areaCode) {
        Intent intent = AgencyCategroyActivity.getCallingIntent(this, areaCode, AgencyCategroy.REHABILITATION);
        startActivity(intent);
    }

    @Override
    public void onInterestClick(@NotNull View v, @NotNull String areaCode) {
        Intent intent = AgencyCategroyActivity.getCallingIntent(this, areaCode, AgencyCategroy.INTEREST);
        startActivity(intent);
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
