package com.yksj.consultation.sonDoc.consultation.outpatient;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * @author HEKL
 *         取消预约原因
 */
public class AtyCancelReasons extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_consult_canceled);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("取消原因");
        TextView mCancelPerson = (TextView) findViewById(R.id.tv_cancelMen);
        TextView mCancelTime = (TextView) findViewById(R.id.tv_cancelTime);
        TextView mCancelReason = (TextView) findViewById(R.id.tv_cancelResons);
        String man = getIntent().getStringExtra("MAN");
        String time = getIntent().getStringExtra("TIME");
        String reansons = getIntent().getStringExtra("REASONS");
        mCancelPerson.setText(man);
        mCancelTime.setText(TimeUtil.format(time));
        mCancelReason.setText(reansons);
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