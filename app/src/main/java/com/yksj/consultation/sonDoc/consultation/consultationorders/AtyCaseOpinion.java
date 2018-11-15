package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
/**
 * Created by HEKL on 15/10/13.
 * Used for 会诊意见_
 */
public class AtyCaseOpinion extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_case_opinion);
        initView();
    }

    private void initView() {
        initializeTitle();//初始化title
        String str=null;
        if (getIntent().hasExtra("Opinion")) {
            str = getIntent().getStringExtra("Opinion");
        }
        //会诊意见
        TextView textOpinion = (TextView) findViewById(R.id.tv_opinion);
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("会诊意见");
        textOpinion.setText(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }
}
