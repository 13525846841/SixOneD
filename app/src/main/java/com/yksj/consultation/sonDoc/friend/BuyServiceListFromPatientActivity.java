package com.yksj.consultation.sonDoc.friend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.caledar.CaledarViewFragment;
import com.yksj.consultation.caledar.CaledarViewFragment.OnItemClickCaladerListener;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog.OnClickSureBtnListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.CaledarObject;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.JsonParseUtils;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ViewFinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 患者购买页面 (门诊预约  预约咨询)
 * @author jack_tang
 *
 */
public class BuyServiceListFromPatientActivity extends BaseActivity implements OnItemClickCaladerListener, OnClickListener {

	private CaledarViewFragment mFragmentView;
	private String mData;
	private CustomerInfoEntity mCustomerInfoEntity;
	SimpleDateFormat mDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat mDateFormat2 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat mDateFormat3 = new SimpleDateFormat("HH:mm");
	Map<String, JSONArray> mDataMap = new LinkedHashMap<String, JSONArray>();
	// 闲忙状态
	Map<Date, String> mStateMap = new LinkedHashMap<Date, String>();
	private LinearLayout mGroupView;
	private int SERVICE_TYPE = 2;//2 预约咨询  3 门诊
	private WaitDialog showWaitDialog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.buy_service_list_from_patient_layout);
		initView();
		if (arg0 != null) {
			mData = arg0.getString("mData");
			mCustomerInfoEntity = arg0.getParcelable("mCustomerInfoEntity");
		} else {
//			mData = getIntent().getStringExtra("response");
			mCustomerInfoEntity = (CustomerInfoEntity) getIntent().getExtras().get("mCustomerInfoEntity");
		}
	}
	
	private void initView() {
		initializeTitle();
		mViewFinder = new ViewFinder(this);
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText(getIntent().getStringExtra("titleName"));
		SERVICE_TYPE = (getIntent().getIntExtra("type", 2));
		mFragmentView = (CaledarViewFragment)getSupportFragmentManager().findFragmentByTag("calendar");
		mFragmentView.setOnItemClickListener(this);
		mGroupView = (LinearLayout) findViewById(R.id.group);
//		AnimationUtils.startGuiPager(this, getClass().getAccounts());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		intData();
	}
	
	
	class AsyncHander extends JsonsfHttpResponseHandler {
		public AsyncHander() {
			super();
		}

		@Override
		public void onSuccess(int statusCode,com.alibaba.fastjson.JSONObject response) {
			super.onSuccess(statusCode, response);
			if (JsonParseUtils.filterErrorMessage(response) == null) {
				mData = response.toString();
				initParseView();
			}else {
				SingleBtnFragmentDialog.show(getSupportFragmentManager(),"六一健康",  JsonParseUtils.filterErrorMessage(response),"知道了",new OnClickSureBtnListener() {
					
					@Override
					public void onClickSureHander() {
						onBackPressed();
					}
				});
			}
			
		}

	}
	
	

	private synchronized void  intData() {
		showWaitDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
		ApiService.doHttpEngageTheDialogue(SmartFoxClient.getLoginUserId(),mCustomerInfoEntity.getId(),String.valueOf(SERVICE_TYPE) , new AsyncHander());
	}

	public synchronized void initParseView() {
		final JSONObject jsonObject = JSON.parseObject(mData);// TickMesg
		if (jsonObject.getIntValue("witchPage") == 1||jsonObject.getIntValue("witchPage") == 2 ||jsonObject.getIntValue("witchPage") == 3) {
			new AsyncTask<Void, Void, Map<Calendar,String>>() {
				@Override
				protected Map<Calendar,String> doInBackground(Void... params) {
					JSONArray array = jsonObject.getJSONArray("TickMesg");
					if (array.size() == 0) {//没有设置预约时段不显示服务内容 价格 等
//						findViewById(R.id.lin_bottom).setVisibility(View.GONE);
					}
					mStateMap.clear();
					mDataMap.clear();
					for (int i = 0; i < array.size(); i++) {
						JSONObject jsonObject2 = array.getJSONObject(i);
						String beginTime = jsonObject2.getString("SERVICE_TIME_BEGIN");
						String busy = jsonObject2.getString("ISBUZY");
						try {
							Date date = mDateFormat2.parse(mDateFormat2.format(mDateFormat1.parse(beginTime)));
							// 记录闲忙
							if (mStateMap.containsKey(date) && "0".equals(busy)) {
								mStateMap.put(date, busy);
							}else if(!mStateMap.containsKey(date)){
								mStateMap.put(date, busy);
							}
							//
							String endTime = jsonObject2.getString("SERVICE_TIME_END");
							beginTime = mDateFormat3.format(mDateFormat1.parse(beginTime));
							endTime = mDateFormat3.format(mDateFormat1.parse(endTime));
							JSONObject object = new JSONObject();
							object.put("time_space", beginTime + "-" + endTime);
							object.put("SERVICE_ITEM_ID",jsonObject2.getString("SERVICE_ITEM_ID"));
							object.put("SERVICE_PRICE",jsonObject2.getString("SERVICE_PRICE"));
							object.put("ISBUY",jsonObject2.getString("ISBUY"));
							object.put("ISBUZY",jsonObject2.getString("ISBUZY"));
							object.put("ISTALK",jsonObject2.getString("ISTALK"));
							object.put("ORDER_ID", jsonObject2.getString("ORDER_ID"));
							object.put("SERVICE_CONTENT", jsonObject2.getString("SERVICE_CONTENT"));
							object.put("SERVICE_PLACE", jsonObject2.getString("SERVICE_PLACE"));
							object.put("data", jsonObject2.toString());
							String str = mDateFormat2.format(date);
							if (mDataMap.containsKey(str)) {
								mDataMap.get(str).add(object);
							} else {
								JSONArray array2 = new JSONArray();
								array2.add(object);
								mDataMap.put(str, array2);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Map<Calendar,String> dates=new HashMap<Calendar, String>();
					for (Map.Entry<Date, String> entry : mStateMap.entrySet()) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(entry.getKey());
						if (entry.getValue().equals("1")) {
							dates.put(cal, CaledarObject.busy);
						} else {
							dates.put(cal, CaledarObject.noBusy);
						}
					}
					
					return dates;
				}
				
				protected void onPostExecute(Map<Calendar,String> result) {
					showWaitDialog.dismissAllowingStateLoss();
					mFragmentView.addApplyDate(result);
					updateBottomView(mPressDate);
				};
			}.execute();
			
		}
		
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mData", mData);
		outState.putSerializable("mCustomerInfoEntity", mCustomerInfoEntity);
	}
	
	private String mPressDate;
	private ViewFinder mViewFinder;
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CaledarObject descriptor= (CaledarObject) view.getTag();
		mPressDate = mDateFormat2.format(descriptor.getDate().getTime());
		updateBottomView(mPressDate);
	}
	
	
	public void updateBottomView(String date){
		if(HStringUtil.isEmpty(date))return;
		mViewFinder.setText(R.id.current_date, TimeUtil.getFormatDate2(date));
//		current_date
		mGroupView.removeAllViews();
		if(mDataMap.containsKey(date)){
			ViewFinder finder;
			JSONArray jsonArray = mDataMap.get(date);
			for (int i = 0; i < jsonArray.size(); i++) {
				final JSONObject jsonObject = jsonArray.getJSONObject(i);
				final View itemView =getLayoutInflater().inflate(R.layout.buy_service_list_from_patient_item_layout,null);
				finder =new ViewFinder(itemView);
				finder.setText(R.id.time, jsonObject.getString("time_space"));
				finder.setText(R.id.edit, jsonObject.getString("SERVICE_PRICE"));
				Button btnBuy = (Button) itemView.findViewById(R.id.delete);
				
				if(Integer.valueOf(jsonObject.getString("ISBUY")) !=0){//已经购买
					btnBuy.setBackgroundResource(R.drawable.doctor_clinic_go_buy);
					btnBuy.setText("看医生");//支付已完成,去留言
					btnBuy.setTag(1);
				}else if(!"".equals(jsonObject.getString("ORDER_ID"))){//未支付
					btnBuy.setBackgroundResource(R.drawable.doctor_clinic_go_buy);
					btnBuy.setText(R.string.go_pay);//订单以生成,去支付
					btnBuy.setTag(2);
				}else if("0".equals(jsonObject.getString("ISBUZY"))){//闲
					btnBuy.setBackgroundResource(R.drawable.doctor_clinic_go_buy);
					btnBuy.setText(R.string.go_pay);//订单以生成,去支付
					btnBuy.setTag(3);
				}else if("1".equals(jsonObject.getString("ISBUZY"))){//忙
					btnBuy.setBackgroundResource(R.drawable.doctor_clinic_can_not_buy);
					btnBuy.setOnClickListener(null);
					btnBuy.setText(R.string.go_pay);//订单以生成,去支付
					btnBuy.setTag(4);
				}else{
				}
				
				btnBuy.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						switch ((Integer) v.getTag()) {
						case 1:////支付已完成,去留言
							FriendHttpUtil.chatFromPerson(BuyServiceListFromPatientActivity.this, mCustomerInfoEntity);
							break;
						case 2://订单以生成,去支付
							actionBuy(jsonObject.getString("data"));
							break;
						case 3://
							if("0".equals(jsonObject.getString("SERVICE_PRICE"))){//0元
								Intent intent =new Intent(BuyServiceListFromPatientActivity.this,ServiceAddInfoActivity.class);
								intent.putExtra("response", jsonObject.getString("data").toString());
								intent.putExtra("mCustomerInfoEntity", mCustomerInfoEntity);
								startActivity(intent);
							}else{
								actionBuy(jsonObject.getString("data"));
							}
							break;
						case 4://忙 不能点击
							
							break;
						}
					}
				});
				
				if(SERVICE_TYPE == 3){
					finder.setText(R.id.address, "服务地点 \n"+ jsonObject.getString("SERVICE_PLACE")).setVisibility(View.VISIBLE);
				}else{
					finder.find(R.id.address).setVisibility(View.GONE);
				}
				if(Integer.valueOf(jsonObject.getString("ISBUY")) ==0){
					if("".equals(jsonObject.getString("ORDER_ID")))
						btnBuy.setText(R.string.go_pay);//去购买
					else
						btnBuy.setText(R.string.go_pay);//订单以生成,去支付
				}else{
					btnBuy.setText("看医生");//支付已完成,去留言
				}
				mGroupView.addView(itemView);
			}
		}
	
	if(mGroupView.getChildCount() == 0){
		findViewById(R.id.empty).setVisibility(View.VISIBLE);
	}else{
		findViewById(R.id.empty).setVisibility(View.GONE);
	}
	}
	

	protected void actionBuy(String data) {
		Intent intent = new Intent(this,ServicePayMainUi.class);
		intent.putExtra("isAddress", true);
		intent.putExtra("json", data);
		intent.putExtra("SERVICE_TYPE", SERVICE_TYPE);
		intent.putExtra("doctorInfoEntity", mCustomerInfoEntity);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;

		}
	}
}
