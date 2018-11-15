package com.yksj.consultation.sonDoc.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * 扫描结果
 * 
 * @author Administrator
 * 
 */
public class QrCodeResultActivity extends BaseActivity implements
		OnClickListener {

	private TextView result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_result);
		initWidget();
		initDate();
	}

	private void initDate() {
		// TODO Auto-generated method stub
		titleTextV.setText("扫描结果");
		Intent in = getIntent();
		if (in.hasExtra("result")) {
			result.setText(in.getStringExtra("result"));
		}
	}

	private void initWidget() {
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		result = (TextView) findViewById(R.id.result);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}

}
