package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import java.math.BigDecimal;

/**
 * 提现界面
 */

public class GetMoney extends BaseActivity implements View.OnClickListener{

    private Button next;
    private EditText et_number;
    private String money;
    private TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_money);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("余额提现");
        titleLeftBtn.setOnClickListener(this);
        money = getIntent().getStringExtra("money");
        next = (Button) findViewById(R.id.getmon_next);
        mTv = (TextView) findViewById(R.id.tv);
        if (HStringUtil.isEmpty(money)){
            mTv.setText("0");
        }else{
            mTv.setText(money);
        }
        et_number = (EditText) findViewById(R.id.et_number);
        next.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.getmon_next:
                String number = et_number.getText().toString();

                if (HStringUtil.isEmpty(number)){
                    ToastUtil.showShort("请输入提现金额");
                }else if (compare( Double.parseDouble(money),Double.parseDouble(number))){
                    ToastUtil.showShort("提现金额不能大于余额");
                }else if (Integer.parseInt(number)<5){
                    ToastUtil.showShort("提现金额不能小于5元");
                } else{
                    Intent intent = new Intent(this,GmNexeActivity.class);
                    intent.putExtra("NUMBER",number);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public static boolean compare(double v1, double v2){

        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        if (b1.compareTo(b2)<0){
            return true;
        }
        return false;
    }
}
