package com.yksj.consultation.sonDoc.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
/**
 * 我的消息  医生端
 * @author jack_tang
 *
 */
public class DcotorMessage extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.my_message_doctor);
		initView();
	}

	private void initView() {
		initializeTitle();
		titleLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTextV.setText("我的消息");
		Fragment fragment = new MessageHistoryFragment();
		FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
		beginTransaction.replace(R.id.fragment, fragment).commit();
	}
}
