package com.yksj.consultation.union;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.union.view.UnionIncidentTreeView;

/**
 * 医生联盟大事件
 */
public class UnionIncidentActivity extends BaseTitleActivity {

    private static final String ID_EXTRA = "id_extra";
    private String mUnionId;

    UnionIncidentTreeView mIncidentTreeLay;

    public static Intent getCallingIntent(Context context, String unionId){
        Intent intent = new Intent(context, UnionIncidentActivity.class);
        intent.putExtra(ID_EXTRA, unionId);
        return intent;
    }

    @Override
    public View createLayout() {
        return mIncidentTreeLay = new UnionIncidentTreeView(this);
    }

//    @Override
//    public int createLayoutRes() {
//        return R.layout.activity_union_incident;
//    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("大事件");
        mUnionId = getIntent().getStringExtra(ID_EXTRA);
        mIncidentTreeLay.requestIncident(mUnionId, false);
    }
}
