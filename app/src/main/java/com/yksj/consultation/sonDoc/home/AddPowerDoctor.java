package com.yksj.consultation.sonDoc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.yksj.consultation.adapter.AddPowerDoctorAdapter;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * 添加授权医生
 */
public class AddPowerDoctor extends BaseActivity {

    private ListView mListView;
    private AddPowerDoctorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_power_doctor);
        initView();
    }
    private void initView() {
        initializeTitle();
        titleTextV.setText("添加");
        titleLeftBtn.setOnClickListener(this);

        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("完成");

        mListView = (ListView) findViewById(R.id.list);
        adapter = new AddPowerDoctorAdapter(this);
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
                ToastUtil.showShort("完成");
                break;

        }
    }
}
