package com.yksj.consultation.sonDoc.doctorstation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 设置服务地点
 * @author jack_tang
 *
 */
public class DoctorServiceSetAddressActivity extends BaseActivity implements OnClickListener {

	private EditText mEdit;
	
	private LinearLayout mLin;

	public int flag;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctor_service_set_address_layout);
		initializeTitle();
		 mLin = (LinearLayout) findViewById(R.id.group);
		 Button bu = (Button) findViewById(R.id.add_sure);
		 bu.setOnClickListener(this);
		 mEdit = (EditText) findViewById(R.id.add_edit);
		 titleTextV.setText("门诊预约地点");
		 titleLeftBtn.setOnClickListener(this);
		 first();
	}

	private void first() {
		flag = 1;
		 RequestParams params = new RequestParams();
		 params.put("Type", "queryServicePlace");
		 params.put("CUSTOMER_ID", LoginBusiness.getInstance().getLoginEntity().getId());
		 initData(params);
	}

	private void initData(RequestParams params) {
		//http://220.194.46.204/DuoMeiHealth/ServiceSetServlet?Type=queryServicePlace&CUSTOMER_ID=3778


		ApiService.doHttpSERVICESETSERVLET420(params, new AsyncHttpResponseHandler(this){
			
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				switch (flag) {
				case 1:
					ParseJson(content);
					break;
				case 2://添加
					mEdit.setText("");
					ToastUtil.showShort("新地址添加成功!");
					first();
					break;
				case 3://删除
					JSONObject jsontwo;
					try {
						jsontwo = new JSONObject(content);
						ToastUtil.showShort(jsontwo.optString("message"));
						first();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					break;
				}
				
			}
			
		});
		
	}

	protected void ParseJson(String content) {
		try {
			mLin.removeAllViews();
			JSONObject object=new JSONObject(content);
			JSONArray jsona = object.getJSONArray("result");
			for(int j=0;j<jsona.length();j++){
				JSONObject jsono = jsona.getJSONObject(j);
				final String address = jsono.optString("PLACE");
				final String id = jsono.optString("PLACE_ID");
				LinearLayout lin = (LinearLayout) View.inflate(this, R.layout.service_set_address_item_layout, null);
				TextView tv = (TextView) lin.findViewById(R.id.add_item_text);
				tv.setText(address);
				
				Button item_delete = (Button) lin.findViewById(R.id.add_item_delete);
				item_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						flag =3;
						RequestParams params = new RequestParams();
						 params.put("Type", "deleteServicePlace");
						 params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
						 params.put("PLACE_ID",id);
						 initData(params);
					}
				});
				
				lin.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = getIntent();
						intent.putExtra("address",address);
						intent.putExtra("id",id);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				mLin.addView(lin);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		SystemUtils.hideSoftBord(getApplicationContext(), mEdit);
		switch (v.getId()) {
		case R.id.add_sure:
			flag = 2;
			addData();
			break;

		case R.id.title_back:
			onBackPressed();
		}
	}

	private void addData() {
		if(mEdit.getText().toString()!=null&& !"".equals(mEdit.getText().toString())){
			 RequestParams params = new RequestParams();
			 params.put("Type", "addServicePlace");
			 params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			 params.put("PLACE",mEdit.getText().toString());
			 initData(params);
			
		}else{
			ToastUtil.showShort("新地址不能为空");
		}
	}

	
}
