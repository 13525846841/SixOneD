package com.yksj.consultation.sonDoc.consultation.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.constant.Constant;

/**
 * 成员审核列表
 */
public class InviteMemActivity  extends BaseTitleActivity {

    private String mStationId;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_invite_mem;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("申请列表");

        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        InviteMemFragment psFragment = InviteMemFragment.newInstance(mStationId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.ll_fragment, psFragment);
        ft.commit();
    }
}
