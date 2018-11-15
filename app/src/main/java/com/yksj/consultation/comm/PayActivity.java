package com.yksj.consultation.comm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.JsonParseUtils;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * 支付宝界面
 * 
 * @author Administrator
 * 
 */
public class PayActivity extends BaseActivity implements
		OnClickListener {

	private WebView wv;
	private Boolean isFirstLoad = true;
	private CustomerInfoEntity mCustomerInfoEntity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.salon_introduction);
		initWidget();
		initData();
	}

	private void initData() {
		if (getIntent().hasExtra("mCustomerInfoEntity")) {
			mCustomerInfoEntity = (CustomerInfoEntity) getIntent().getExtras().get("mCustomerInfoEntity");
		}
		// 设置WebView属性，能够执行JavaScript脚本
//		wv.getSettings().setJavaScriptEnabled(true);
//		wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		wv.getSettings().setDomStorageEnabled(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setSupportZoom(false);
		wv.getSettings().setBuiltInZoomControls(false);
		wv.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wv.getSettings().setDefaultFontSize(18);
//		wv.loadData( getIntent().getStringExtra("summary"),"text/html", "utf-8");
		try {
			wv.loadDataWithBaseURL(null, getIntent().getStringExtra("summary").replace("am-loading-text","aa").replace("J-loading am-loading", "bb").replace("加载中...",""),"text/html", "utf-8", null);
		} catch (Exception e) {
			wv.loadDataWithBaseURL(null, getIntent().getStringExtra("summary"),"text/html", "utf-8", null);
		}
		
		wv.setWebViewClient(new MyWebViewClient());
		
	}


	private void initWidget() {
		initializeTitle();
		wv = (WebView) findViewById(R.id.wv);
		titleTextV.setText("订单支付");
		titleLeftBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		}
	}

	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent());
		finish();
	}
	
	private class MyWebViewClient extends WebViewClient {
		private WaitDialog dialog;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("healthchat2://com.dummyvision")) {
				String id = url.substring(url.lastIndexOf("=") + 1);
				if (url.contains("guahao")) {
					if (mCustomerInfoEntity == null
							&& !mCustomerInfoEntity.getId().equals(id)) {
						ToastUtil.showShort(getApplicationContext(),
								"mCustomerInfoEntity is null");
					} else {
						ApiService.doHttpInitChat(SmartFoxClient.getLoginUserId(),mCustomerInfoEntity.getId(),new JsonsfHttpResponseHandler(){
							public void onSuccess(int statusCode, com.alibaba.fastjson.JSONObject response) {
								super.onSuccess(statusCode, response);
									String content;
									if ((content = JsonParseUtils.filterErrorMessage(response) )!= null) {
										SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),content);
									}else {
										String sendCode = response.getString("send_code");
										Intent intent = new Intent();
										intent.setClass(getApplicationContext(), ChatActivity.class);
										if (!TextUtils.isEmpty(sendCode)) {
											intent.putExtra("NOTE", sendCode);
										}
										intent.putExtra("pay_type", "pay_type_dialog");
										intent.putExtra(Constant.Chat.KEY_PARAME, mCustomerInfoEntity);
										startActivity(intent);
										finish();
										}
								}
						});
					}

				} else if (url.contains("buyTicket")) {
//					if (mGroupInfoEntity == null
//							&& !mGroupInfoEntity.getId().equals(id)) {
//						ToastUtil.showShort(getApplicationContext(),
//								"mGroupInfoEntity is null");
//					} else {
//						intent.setClass(getApplicationContext(),
//								ChatActivity.class);
//						if (!TextUtils.isEmpty(message)) {
//							intent.putExtra("NOTE", message);
//						}
//						intent.putExtra(ChatActivity.KEY_PARAME,
//								mGroupInfoEntity);
//						startActivity(intent);
//					}
				}
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (dialog != null) {
				dialog.dismissAllowingStateLoss();
				dialog = null;
				isFirstLoad = false;
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (isFirstLoad) {
				dialog = WaitDialog.showLodingDialog(
						getSupportFragmentManager(), getResources());
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	private class MyWebChromeClient extends WebChromeClient {

	}
}
