package com.yksj.consultation.sonDoc.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.unionpay.UPPayAssistEx;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.comm.PayActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.WalletPayFragmentDialog;
import com.yksj.consultation.comm.WalletPayFragmentDialog.OnClickSureBtnListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.setting.SettingPhoneBound;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.entity.TickMesg;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;
import com.yksj.healthtalk.wallet.PwdSettingActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 去购买跳转的界面
 * @author lmk
 *
 */
public class ServicePayMainUi extends BaseActivity implements OnClickListener,
OnClickSureBtnListener,OnDilaogClickListener{
	private String url = null;
	private EditText editName,editPhoneNum,editBeizhu;  //姓名,手机号,备注
	private TextView tvWalletBalance2,tvExtraMoney;//钱包余额,另外一个是钱包支付那个地方显示,还需支付
	private TextView zhifubaoPay,yinlianPay;//支付宝,银联 钱包支付

	private CustomerInfoEntity doctorInfoEntity;
	private TickMesg mesg;//传过来的TickMesg门票实体信息
	private static final int PLUGIN_NOT_INSTALLED = -1;
    private static final int PLUGIN_NEED_UPGRADE = 2;

    /*****************************************************************
     * mMode参数解释：
     *      "00" - 启动银联正式环境
     *      "01" - 连接银联测试环境
     *****************************************************************/
    private String mMode = "00";
//    private static final String TN_URL_01 = "http://222.66.233.198:8080/sim/gettn";
	private boolean isBindPhone,isSetPsw;//是否绑定手机,是否设置支付密码
	private String payId;//支付订单号
	private int balance,privice;//余额,价格
	private String SERVICE_PRICE,SERVICE_ITEM_ID,SERVICE_TYPE_SUB_ID,SERVICE_TYPE_ID;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.service_pay_layout);
		initView();
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		ApiService.doHttpGetQianBao(SmartFoxClient.getLoginUserId(), new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				try {
						JSONObject object =new JSONObject(content);
						tvWalletBalance2.setText("余额:"+object.optString("Balance","0")+"元");
						isBindPhone=("1".equals(object.optString("BindPhone")));//是否设置密码
						isSetPsw=("1".equals(object.optString("isSetPsw")));//是否绑定手机
				} catch (Exception e) {
				}
			}
		});
	}
	
	//初始化数据
	private void initData() {
		Intent intent=getIntent();
		doctorInfoEntity=(CustomerInfoEntity) intent.getExtras().get("doctorInfoEntity");
		if(intent.hasExtra("mesg")){
			mesg=(TickMesg) intent.getSerializableExtra("mesg");
			privice=Integer.parseInt(mesg.getSERVICE_PRICE());
			SERVICE_PRICE = mesg.getSERVICE_PRICE();
			SERVICE_ITEM_ID = mesg.getSERVICE_ITEM_ID();
			SERVICE_TYPE_SUB_ID = mesg.getSERVICE_TYPE_SUB_ID();
			SERVICE_TYPE_ID = mesg.getSERVICE_TYPE_ID();
			url=getParams();
			try {
				JSONObject object = new JSONObject(intent.getStringExtra("json"));
				balance=Integer.parseInt(object.optString("Balance"));//余额
				isBindPhone=("1".equals(object.optString("BindPhone")));//是否设置密码
				isSetPsw=("1".equals(object.optString("isSetPsw")));//是否绑定手机
				payId=object.optString("PAY_ID");//订单号
				tvWalletBalance2.setText(balance+"元");
				int need=(privice-balance>0)?privice-balance:balance-privice;
				tvExtraMoney.setText(need+"元");
			} catch (JSONException e) {
			}
		}else if(intent.hasExtra("json")){
			try {
				JSONObject json = new JSONObject(intent.getStringExtra("json"));
					editName.setText(json.optString("PATIENT_NAME"));
					editPhoneNum.setText(json.optString("PATIENT_PHONE"));
					editBeizhu.setText(json.optString("ADVICE_CONTENT"));
					SERVICE_PRICE = json.optString("SERVICE_PRICE") ;
					SERVICE_ITEM_ID = json.optString("SERVICE_ITEM_ID");
					SERVICE_TYPE_SUB_ID = json.optString("SERVICE_TYPE_SUB_ID");
					SERVICE_TYPE_ID =  json.optString("SERVICE_TYPE_ID");	
					
			} catch (Exception e) {
			}
		}
		
		
		if("3".equals(SERVICE_TYPE_ID)){
			findViewById(R.id.service_pay_basic_info_layout).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.service_pay_basic_info_layout).setVisibility(View.GONE);
		}
		
		
	/**留言	
	 * Intent intent=new Intent(LeaveMesgConsultActivity.this,ServicePayMainUi.class);
		intent.putExtra("doctorInfoEntity", doctorInfoEntity);
		intent.putExtra("mesg",mesg);
		intent.putExtra("json", object.toString());
		startActivity(intent);
		//预约  门诊
		Intent intent = new Intent(this,ServicePayMainUi.class);
		intent.putExtra("isAddress", true);
		intent.putExtra("json", data);
		intent.putExtra("doctorInfoEntity", mCustomerInfoEntity);
		startActivity(intent);
		//继续支付
			Intent intent = new Intent(this,ServicePayMainUi.class);
			intent.putExtra("json", mData);
			intent.putExtra("doctorInfoEntity", mCustomerInfoEntity);
			startActivity(intent);
		*
		*/
	}

	//初始化视图
	private void initView() {
		initializeTitle();
		titleTextV.setText(R.string.pay);//支付
		titleLeftBtn.setOnClickListener(this);
		editName=(EditText) findViewById(R.id.service_pay_basic_info_name2);
		editPhoneNum=(EditText) findViewById(R.id.service_pay_basic_info_phone2);
		editBeizhu=(EditText) findViewById(R.id.service_pay_basic_info_remark2);//备注
		zhifubaoPay=(TextView) findViewById(R.id.service_pay_method_zhifubao);
		yinlianPay=(TextView) findViewById(R.id.service_pay_method_yinlian);
		zhifubaoPay.setOnClickListener(this);
		yinlianPay.setOnClickListener(this);
		tvWalletBalance2=(TextView) findViewById(R.id.service_pay_method_wallet_money);
		tvWalletBalance2.setOnClickListener(this);
		tvExtraMoney=(TextView) findViewById(R.id.service_pay_still_need2);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.service_pay_method_zhifubao://支付宝
			if("3".equals(SERVICE_TYPE_ID) && (HStringUtil.isEmpty(editName.getText().toString()))){
					ToastUtil.showShort("请输入姓名");
					return ;
				}
			
			if ("3".equals(SERVICE_TYPE_ID) && !ValidatorUtil.checkMobile(editPhoneNum.getText().toString())){
				ToastUtil.showShort("请输入正确的手机号码!");
				return ;
			}
			String params = getPayService();
			if(HStringUtil.isEmpty(params))return;
			ApiService.doHttpGetAliPay(params, new AyncHander("Alipay"));
			break;
		case R.id.service_pay_method_yinlian://银联
			if("3".equals(SERVICE_TYPE_ID) && (HStringUtil.isEmpty(editName.getText().toString()))){
				ToastUtil.showShort("请输入姓名");
				return ;
			}
		
			if ("3".equals(SERVICE_TYPE_ID) && !ValidatorUtil.checkMobile(editPhoneNum.getText().toString())){
				ToastUtil.showShort("请输入正确的手机号码!");
				return ;
			}
			String para= getPayService();
			if(HStringUtil.isEmpty(para))return;
			ApiService.doHttpGetUnionPay(para, new AyncHander("Unionpay"));
			break;
		case R.id.service_pay_method_wallet_money://钱包支付
			if("3".equals(SERVICE_TYPE_ID) && (HStringUtil.isEmpty(editName.getText().toString()))){
				ToastUtil.showShort("请输入姓名");
				return ;
			}
		
		if ("3".equals(SERVICE_TYPE_ID) && !ValidatorUtil.checkMobile(editPhoneNum.getText().toString())){
			ToastUtil.showShort("请输入正确的手机号码!");
			return ;
		}
		
			getPayService();
			if (!isBindPhone) {
				DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  "使用钱包支付，需绑定手机并设置支付密码，您目前未绑定手机。", "稍后再说", "现在绑定", this);
			}else if(!isSetPsw){
				DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  "使用钱包支付，需绑定手机并设置支付密码，您目前未设置支付密码。", "稍后再说", "现在设置", this);
			}else {
				WalletPayFragmentDialog.show(getSupportFragmentManager(), "请输入支付密码","" );
			}
			break;
		}
	}
	
	
	//支付操作类
	class AyncHander extends AsyncHttpResponseHandler{
		private String type;
		public AyncHander(String string) {
			super(ServicePayMainUi.this);
			this.type = string;
		}
		@Override
		public void onSuccess(int statusCode, String content) {
			try {
				JSONObject response = new JSONObject(content);
				if (response.has("error_message")) {
					SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), response.getString("error_message"));
					return;
				}else  if (type.equals("Wallet")) {
					if (!response.has("error_message")) {
						FriendHttpUtil.chatFromPerson(ServicePayMainUi.this, doctorInfoEntity);
					}
				}else if(response.has("tn")){
//					{"PAY_ID":"1140227007141885","tn":"201402271420460080192"}
					  /************************************************* 
		             * 
		             *  步骤2：通过银联工具类启动支付插件 
		             *  
		             ************************************************/
		            // mMode参数解释：
		            // 0 - 启动银联正式环境
		            // 1 - 连接银联测试环境
		            int ret = UPPayAssistEx.startPay(ServicePayMainUi.this, null, null, response.getString("tn"), mMode);
		            if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
		                // 需要重新安装控件
		                AlertDialog.Builder builder = new AlertDialog.Builder(ServicePayMainUi.this);
		                builder.setTitle("六一健康");
		                builder.setMessage("完成购买需要安装银联支付控件，是否安装？");
		                builder.setNegativeButton("确定",
		                        new DialogInterface.OnClickListener() {
		                            @Override
		                            public void onClick(DialogInterface dialog,
		                                    int which) {
		                                dialog.dismiss();
		                                UPPayAssistEx.installUPPayPlugin(ServicePayMainUi.this);
		                            }
		                        });

		                builder.setPositiveButton("取消",
		                        new DialogInterface.OnClickListener() {

		                            @Override
		                            public void onClick(DialogInterface dialog,
		                                    int which) {
		                                dialog.dismiss();
		                            }
		                        });
		                builder.create().show();
		            }else {
						finish();
					}
				}else if(response.has("LING")){//价格为0 跳转到 聊天界面
					FriendHttpUtil.chatFromPerson(ServicePayMainUi.this, doctorInfoEntity);
				}
			} catch (Exception e) {
				if (content.contains("支付宝")) {
					Intent intent = new Intent(getApplicationContext(),PayActivity.class);
					intent.putExtra("summary", content);
					intent.putExtra("mCustomerInfoEntity", doctorInfoEntity);
					startActivity(intent);
				}
			}
			super.onSuccess(statusCode, content);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			isSetPsw = true;
		}else if(requestCode == 2 && resultCode == RESULT_OK){
			if (data.hasExtra("phone_num")) {
				isBindPhone = true;
			}else {
				isBindPhone = false;
			}
		}else {
			/************************************************* 
	         * 
	         *  步骤3：处理银联手机支付控件返回的支付结果 
	         *  
	         ************************************************/
	        if (data == null) {
	            return;
	        }
	        String msg = "";
	        /*
	         * 支付控件返回字符串:success、fail、cancel
	         * 分别代表支付成功，支付失败，支付取消
	         */
	        String str = data.getExtras().getString("pay_result");
	        if (str.equalsIgnoreCase("success")) {
	            msg = "支付成功！";
	        } else if (str.equalsIgnoreCase("fail")) {
	            msg = "支付失败！";
	        } else if (str.equalsIgnoreCase("cancel")) {
	            msg = "用户取消了支付";
	        }

	        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  msg);
		}
		super.onActivityResult(requestCode, resultCode, data);
    }
	
	/*
	 * 如果是门诊预约 或者 预约咨询 就装数据
	 */
	public String getPayService(){
		if(url != null) return url;
		try {
			org.json.JSONObject json = new org.json.JSONObject(getIntent().getStringExtra("json"));
			RequestParams params = new RequestParams();
			params.put("DOCTORID", doctorInfoEntity.getId());	
			params.put("Type", "MedicallyRegistered");
			params.put("SERVICE_ITEM_ID", SERVICE_ITEM_ID);
			params.put("SERVICE_TYPE_ID", SERVICE_TYPE_ID);
			params.put("VALID_MARK", AppContext.APP_VALID_MARK);
			params.put("ORDER_ID", json.getString("ORDER_ID"));
			if(!json.has("SERVICE_TIME_BEGIN"))
				params.put("SELECTDATE", json.getString("SERVICE_START").substring(0,8));
			else
				params.put("SELECTDATE", json.getString("SERVICE_TIME_BEGIN").substring(0,8));
			
			if ("2".equals(SERVICE_TYPE_ID) ) {
				params.put("ADVICE_CONTENT", editBeizhu.getText().toString());//咨询内容
			}else if("3".equals(SERVICE_TYPE_ID) ){
				params.put("ADVICE_CONTENT", editBeizhu.getText().toString());//咨询内容
				params.put("PATIENT_NAME", editName.getText().toString());//名字
				params.put("PATIENT_PHONE", editPhoneNum.getText().toString());//手机
				params.put("SERVICE_PLACE", json.optString("SERVICE_PLACE"));//地点
			}
			params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			return url = params.toString();
		} catch (Exception e) {
			return null ;
		}
	}
	
	/**
	 * 获取到查询钱包余额的字符串参数
	 * @return  字符串
	 */
	public String getParams(){
		RequestParams params = new RequestParams();
		params.put("Type", "MedicallyRegistered");
		params.put("DOCTORID", doctorInfoEntity.getId());
//		params.put("SELECTDATE", null);
		params.put("CUSTOMER_ID",SmartFoxClient.getLoginUserId());
		params.put("SERVICE_ITEM_ID", SERVICE_ITEM_ID);
		params.put("SERVICE_TYPE_SUB_ID", SERVICE_TYPE_SUB_ID);
		params.put("SERVICE_TYPE_ID", SERVICE_TYPE_ID);
		return params.toString();
	}

	/**
	 * 钱包支付调用
	 */
	@Override
	public void onClickSureHander(String money) {
		if (TextUtils.isEmpty(money)) {
			ToastUtil.showShort(getApplicationContext(), "密码不能为空");
		}else {//ADVICE_CONTENT=wwfgg&DOCTORID=2607&CUSTOMER_ID=116305&SERVICE_ITEM_ID=24407&SERVICE_TYPE_ID=3&SERVICE_PLACE=嘻嘻嘻休息一&Type=MedicallyRegistered&ORDER_ID=&PATIENT_PHONE=15525252525&SELECTDATE=20150313&PATIENT_NAME=qqq
			String str[] = url.split("&");
			for (int i = 0; i < str.length; i++) {
				if (str[i].contains("Type")) {
					str[i] = "Type=WalletPayment";
				}
			}
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < str.length; i++) {
				builder.append(str[i]);
				builder.append("&");
			}
			url = builder.toString().substring(0, builder.toString().length()-1);
			ApiService.doHttpWalletPay(url,MD5Utils.getMD5(money), new AyncHander("Wallet"));
		}
	}
	

	@Override
	public void onDismiss(DialogFragment fragment) {
	}

	@Override
	public void onClick(DialogFragment fragment, View v) {
		if (!isBindPhone) {
			Intent intent = new  Intent(this, SettingPhoneBound.class);
			startActivityForResult(intent, 2);
		}else if(!isSetPsw){
			Intent intent = new Intent(getApplicationContext(), PwdSettingActivity.class);
			intent.putExtra("isPayPwd", isSetPsw);
			intent.putExtra("isBDPhoneNum", SmartFoxClient.getLoginUserInfo().getPoneNumber());
			startActivityForResult(intent, 1);
		}
	}
	
}
