package com.yksj.consultation.sonDoc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.yksj.consultation.adapter.PowerDoctorAdapter;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * 授权医生
 */
public class PowerDoctorActivity extends BaseActivity {

    private ListView mListView;
    private PowerDoctorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_doctor);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("授权医生");
        titleLeftBtn.setOnClickListener(this);

        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("添加");

        mListView = (ListView) findViewById(R.id.list);
        adapter = new PowerDoctorAdapter(this);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://添加
                intent = new Intent(this,AddPowerDoctor.class);
                startActivity(intent);
                break;
        }
    }

}
