package com.yksj.consultation.sonDoc.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yksj.consultation.adapter.HospitalAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.HospitalBean;
import com.yksj.healthtalk.net.http.ApiService;

public class HospitalListActivity extends BaseListActivity {
    private static final String AREA_CODE_EXREA = "area_code_exrea";
    private static final String HOSPITAL_DATA = "hospital_data";
    private String mAreaCode;// 地区编码

    public static Intent getCallingIntent(Context context, String areaCode) {
        Intent intent = new Intent(context, HospitalListActivity.class);
        intent.putExtra(AREA_CODE_EXREA, areaCode);
        return intent;
    }

    @Override public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("选择医院");
        mAreaCode = getIntent().getStringExtra(AREA_CODE_EXREA);
    }

    @Override public boolean getLoadMoreEnable() {
        return false;
    }

    @Override public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
        HospitalBean hospitalBean = (HospitalBean) adapter.getItem(i);
        Intent intent = new Intent();
        intent.putExtra(HOSPITAL_DATA, hospitalBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override protected BaseQuickAdapter createAdapter() {
        return new HospitalAdapter();
    }

    @Override protected void requestData(boolean isMore, int pageIndex) {
        ApiService.requestHospital(mAreaCode, createSimpleCallback(HospitalBean.class));
    }

    public static HospitalBean obtainData(Intent data){
        HospitalBean hospitalBean = data.getParcelableExtra(HOSPITAL_DATA);
        return hospitalBean;
    }
}
