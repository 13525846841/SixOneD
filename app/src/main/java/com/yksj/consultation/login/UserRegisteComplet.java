package com.yksj.consultation.login;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.blankj.utilcode.util.NetworkUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.socket.SmartControlClient;

public class UserRegisteComplet extends BaseActivity implements OnClickListener {

	WaitDialog mDialog;
	SmartControlClient mControlClient;
	
	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1://登录超时
//				if(mControlClient.getLoginState() != 2){
//					if(mDialog != null && mDialog.isShowing()){
//						mDialog.dismissAllowingStateLoss();
//						mDialog = null;
//					}
//					SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),"登录超时,请稍后重试!");
//				}
				break;
			case 2://登录错误
				if(mDialog != null && mDialog.isShowing()){
					mDialog.dismissAllowingStateLoss();
					mDialog = null;
				}
				SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),msg.obj.toString());
				break;
			case 3://登录成功
//				Intent intent = new Intent(UserRegisteComplet.this,MainTabActivity.class);
//				intent.putExtra("isFromLogin",true);
//				startActivity(intent);
//				finish();
				if(mDialog != null && mDialog.isShowing()){
					mDialog.dismissAllowingStateLoss();
					mDialog = null;
				}
				break;
			}
		};
	};
	
	final BroadcastReceiver mReceiver = new BroadcastReceiver(){
		public void onReceive(android.content.Context context, Intent intent) {
			int state = intent.getIntExtra("state",-1);
			//登录成功
			if(state == 2){
				
			//登录加载个人资料完成	
			}else if(state == 0){
				//是否显示协议
				mHandler.removeMessages(1);
				boolean isShow = intent.getBooleanExtra("isShowProtocol",false);
				Message message = mHandler.obtainMessage();
				message.what = 3;
				message.obj = isShow;
				mHandler.sendMessage(message);
			//登录错误	
			}else if(intent.hasExtra("errormsg")){
				mHandler.removeMessages(1);
				Message message = mHandler.obtainMessage();
				message.what = 2;
				message.obj = intent.getStringExtra("errormsg");
				mHandler.sendMessage(message);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_registe_complet);
		mControlClient = SmartControlClient.getControlClient();
		initUI();
	}
	
	private void initUI(){
		findViewById(R.id.login).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.login:
			onLogin();
			break;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
//		filter.addAction(SFSEvent.LOGIN);
		registerReceiver(mReceiver, filter);
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mHandler.removeMessages(1);
		unregisterReceiver(mReceiver);
	}
	
	private void onLogin(){
		if(NetworkUtils.isConnected()){
			mDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(),"登录中...");
			mDialog.setCancelable(false);
			Intent intent = getIntent();
			String duomeinum = intent.getStringExtra("duomeinum");
			String passwd = intent.getStringExtra("passwd");
			mControlClient.setUserPassword(duomeinum, passwd);
//			mControlClient.setLoginState(0);
			LoginBusiness.getInstance().login();
			mHandler.sendEmptyMessageDelayed(1,SmartControlClient.CONNECTION_TIMEOUT);
		}else{
			SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),"网络不可用");
		}
	}
}
