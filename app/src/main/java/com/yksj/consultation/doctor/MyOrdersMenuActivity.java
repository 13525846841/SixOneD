package com.yksj.consultation.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.consultationorders.MyConsultationActivity;

import butterknife.OnClick;

/**
 * 医生端我的订单
 * Created by HEKL on 16/11/11.
 */
public class MyOrdersMenuActivity extends BaseTitleActivity {

    @Override
    public int createLayoutRes() {
        return R.layout.aty_myordes;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("我的订单");
    }

    @OnClick({R.id.experience_view, R.id.picandcul_view, R.id.phone_view, R.id.consul_view, R.id.video_view, R.id.order3_view, R.id.order4_view})
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.experience_view://体验咨询
                intent = MyOrderActivity.getCallingIntent(this, "体验咨询", ServiceType.TY);
                startActivity(intent);
                break;
            case R.id.picandcul_view://图文咨询
                intent = MyOrderActivity.getCallingIntent(this, "图文咨询", ServiceType.TW);
                startActivity(intent);
                break;
            case R.id.phone_view://电话咨询
                intent = MyOrderActivity.getCallingIntent(this, "电话咨询", ServiceType.DH);
                startActivity(intent);
                break;
            case R.id.consul_view://包月咨询
                intent = MyOrderActivity.getCallingIntent(this, "包月咨询", ServiceType.BY);
                startActivity(intent);
                break;
            case R.id.video_view://视频咨询
                intent = MyOrderActivity.getCallingIntent(this, "视频咨询", ServiceType.SP);
                startActivity(intent);
                break;
            case R.id.order3_view://会诊订单
                intent = MyConsultationActivity.getCallingIntent(this);
                startActivity(intent);
                break;
            case R.id.order4_view://门诊订单
                intent = MyOutpatientActivity.getCallingIntent(this);
                startActivity(intent);
                break;
        }
    }
}
