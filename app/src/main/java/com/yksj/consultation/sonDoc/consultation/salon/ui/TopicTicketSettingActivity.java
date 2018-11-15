package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 假如医生选择开通门票则跳转至此页
 * 进行设置门票,也可以跳转到特殊收费人群设置
 * @author lmk
 *
 */
public class TopicTicketSettingActivity extends BaseTitleActivity implements OnClickListener{
	private LinearLayout specialLayout;
	private String  topicId;
	private EditText editDayPrice,editMonthPrice;
	private JSONObject object;

	@Override
	public int createLayoutRes() {
		return R.layout.topic_ticket_setting_ui;
	}

	@Override
	public void initialize(Bundle savedInstanceState) {
		super.initialize(savedInstanceState);
		if(savedInstanceState != null){
			topicId = savedInstanceState.getString("topicId");
		}else{
			topicId = getIntent().getStringExtra("topicId");
		}
		initView();
		initData();
	}

	//初始化数据
	private void initData() {
		ApiService.doHttpQuerySalonPrice(topicId, "findSalonTicketMsg", new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					object=new JSONObject(content);
					JSONArray array = object.getJSONArray("ticket");
					if(array != null && array.length() != 0){
						editDayPrice.setText(array.getJSONObject(0).getString("charge"));
						editMonthPrice.setText(array.getJSONObject(1).getString("charge"));
					}
					editDayPrice.setHint(object.getString("dayMinCharge")+"-"+object.getString("dayMaxCharge"));
					editMonthPrice.setHint(object.getString("monMinCharge")+"-"+object.getString("monMaxCharge"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, content);
			}
			
		});
		}
		

	//初始化控件
	private void initView() {
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText(R.string.topic_ticket_seeting);
		titleRightBtn2.setVisibility(View.VISIBLE);
		titleRightBtn2.setText(R.string.save);
		titleRightBtn2.setOnClickListener(this);
		editDayPrice=(EditText) findViewById(R.id.topic_ticket_day_price);
		editMonthPrice=(EditText) findViewById(R.id.topic_ticket_monrh_price);
		specialLayout=(LinearLayout) findViewById(R.id.topic_ticket_special_layout);
		specialLayout.setOnClickListener(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("topicId", topicId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.topic_ticket_special_layout://特殊收费人群
			Intent intent = new Intent(this,SalonSpecialPeoplesGroupListActivity.class);
			intent.putExtra("groupid",topicId);
			startActivity(intent);
			break;
		case R.id.title_right2://保存
			onSave();
			break;

		}
	}

	//提交修改或者创建的数据
	private void onSave() {
		if(object == null ) return;
		String str = editDayPrice.getText().toString();
		NumberRange range = new NumberRange(object.optInt("dayMinCharge"), object.optInt("dayMaxCharge"));
		if(!NumberUtils.isNumber(str) || !range.containsFloat(NumberUtils.toFloat(str))){
			ToastUtil.showLong(this,"日票设置错误"+object.optString("dayMinCharge")+"-"+object.optString("dayMaxCharge"));
			return;
		}
		String str1 = editMonthPrice.getText().toString();
		range = new NumberRange(object.optInt("monMinCharge"), object.optInt("monMaxCharge"));
		if(!NumberUtils.isNumber(str1) || !range.containsFloat(NumberUtils.toFloat(str1))){
			ToastUtil.showLong(this,"月票设置错误"+object.optString("monMinCharge")+"-"+object.optString("monMaxCharge"));
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("ticketType","1");
			obj.put("charge", str);
		
			JSONObject object1 = new JSONObject();
			object1.put("ticketType","2");
			object1.put("charge", str1);
			jsonArray.put(obj);
			jsonArray.put(object1);
			object.put("ticket",jsonArray);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.put("type","salonTicketSet");
		params.put("parameter", object.toString());
		ApiService.doHttpSalonSpecialPriceGroupSet(params,new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, String content) {
				if(content.contains("error_message")){
					ToastUtil.showShort(getString(R.string.ticket_setting_error));
				}else{
					Intent data=new Intent();
					setResult(RESULT_OK, data);
					onBackPressed();
				}
				super.onSuccess(statusCode, content);
			}
			
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent data=new Intent();
		setResult(RESULT_OK, data);
		finish();
		super.onBackPressed();
	}
}
