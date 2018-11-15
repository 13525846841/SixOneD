package com.yksj.consultation.agency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.agency.constant.AgencyCategroy.Categroy;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.view.AgencyInfoView;

import org.jetbrains.annotations.NotNull;

/**
 * 机构首页
 */
public class AgencyInfoActivity extends BaseTitleActivity implements AgencyInfoView.IPresenter {

    private AgencyInfoView mView;
    // 机构ID
    private String mInfoId;
    private String mCategroy;

    public static Intent getCallingIntent(Context context, String id, @Categroy String categroy){
        Intent intent = new Intent(context, AgencyInfoActivity.class);
        intent.putExtra(AgencyConst.ID_EXTRA, id);
        intent.putExtra(AgencyConst.CATEGROY_EXTRA, categroy);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyInfoView(this, this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("机构详情");
        mInfoId = getIntent().getStringExtra(AgencyConst.ID_EXTRA);
        mCategroy = getIntent().getStringExtra(AgencyConst.CATEGROY_EXTRA);
    }

    @Override
    public void addActive() {
        Intent intent = AgencyAddActiveActivity.getCallingIntent(this, mInfoId);
        startActivity(intent);
    }

    /**
     * 设置封面
     * @param imgPath
     */
    public void setAvatar(String imgPath){
        mView.setAvatar(imgPath);
    }

    @NotNull
    @Override
    public String getAgencyId() {
        return getIntent().getStringExtra(AgencyConst.ID_EXTRA);
    }

    @NotNull
    @Override
    public String getCategroy() {
        return getIntent().getStringExtra(AgencyConst.CATEGROY_EXTRA);
    }
}
