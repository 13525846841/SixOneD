package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * 点击我的订单进入的界面
 * @author Administrator
 */
public class MyFaceOrder extends BaseActivity implements OnClickListener {

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, MyFaceOrder.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.myorders_activty);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("门诊预约");
        PDoctorServiceFragment psFragment = new PDoctorServiceFragment(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.ll_fragment, psFragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }
}
