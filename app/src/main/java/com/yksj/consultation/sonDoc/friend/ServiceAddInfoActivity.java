package com.yksj.consultation.sonDoc.friend;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseActivity;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.JsonParseUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;
/**
 * 服务为免费的  填写需要的资料信息
 * @author jack_tang
 *
 */
public class ServiceAddInfoActivity extends BaseActivity implements OnClickListener {

	private CustomerInfoEntity mCustomerInfoEntity;
	private EditText mName;
	private EditText mPhone;
	private EditText mRemark;
	private org.json.JSONObject json;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.service_addinfo_activity_layout);
		initView();
	}

	private void initView() {
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		try {
			json = new org.json.JSONObject(getIntent().getStringExtra("response"));
			findViewById(R.id.chat_action).setOnClickListener(this);
			mCustomerInfoEntity = (CustomerInfoEntity) getIntent().getExtras().get("mCustomerInfoEntity");
			mName = (EditText) findViewById(R.id.service_pay_basic_info_name2);
			mPhone = (EditText) findViewById(R.id.service_pay_basic_info_phone2);
			mRemark = (EditText) findViewById(R.id.service_pay_basic_info_remark2);
			
			
			if("2".equals(json.getString("SERVICE_TYPE_ID"))){//只显示备注
				findViewById(R.id.phone_view).setVisibility(View.GONE);
				findViewById(R.id.name_view).setVisibility(View.GONE);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.chat_action:
			actionBuy();
			break;
		}
	}
	
	protected void actionBuy() {
		
		SystemUtils.hideSoftBord(getApplicationContext(), mName);
		final RequestParams params = new RequestParams();
		try {
			
			if ("3".equals(json.getString("SERVICE_TYPE_ID")) && !ValidatorUtil.checkMobile(mPhone.getText().toString())){
				ToastUtil.showShort("请输入正确的手机号码!");
				return ;
			}
			params.put("DOCTORID", json.getString("CUSTOMER_ID"));	
			params.put("Type", "MedicallyRegistered");
			params.put("SERVICE_ITEM_ID", json.getString("SERVICE_ITEM_ID"));
			params.put("SERVICE_TYPE_ID", json.getString("SERVICE_TYPE_ID"));
			params.put("SELECTDATE", json.getString("SERVICE_TIME_BEGIN").substring(0,8));
			if ("2".equals(json.getString("SERVICE_TYPE_ID")) ) {
				params.put("ADVICE_CONTENT", mRemark.getText().toString());//咨询内容
			}else if("3".equals(json.getString("SERVICE_TYPE_ID")) ){
				params.put("ADVICE_CONTENT", mRemark.getText().toString());//咨询内容
				params.put("PATIENT_NAME", mName.getText().toString());//名字
				params.put("PATIENT_PHONE", mPhone.getText().toString());//手机
				params.put("SERVICE_PLACE", json.optString("SERVICE_PLACE"));//地点
			}
			params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
		} catch (Exception e) {
			return ;
		}
		ApiService.doHttpWalletBalanceServlet(null,params.toString(), new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					Object object = JSON.parse(content);
					if (object instanceof JSONObject) {
						JSONObject object2 = (JSONObject) object;
						String str = JsonParseUtils.filterErrorMessage(content);
						if ( str != null) {//错误信息
							SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), str);
						}else if(object2.containsKey("LING")){//价格为0 跳转到 聊天界面
							FriendHttpUtil.chatFromPerson(ServiceAddInfoActivity.this, mCustomerInfoEntity);
						}
					}
				} catch (Exception e) {
				}
				super.onSuccess(statusCode, content);
			
			}
		});
	}
	
}
