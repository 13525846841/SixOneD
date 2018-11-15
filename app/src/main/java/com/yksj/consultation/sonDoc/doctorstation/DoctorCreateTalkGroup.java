package com.yksj.consultation.sonDoc.doctorstation;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.DoctorServiceGroupEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 创建特殊人群收费
 * @author Administrator
 *
 */
public class DoctorCreateTalkGroup extends BaseActivity implements OnClickListener {

	private boolean isUpdate=false;
	private EditText mGroupName,mGroupPrice;
	private String mServiceItemId;
	private DoctorServiceGroupEntity mEntity;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctor_create_group);
		initializeTitle();
		mServiceItemId = getIntent().getStringExtra("SERVICE_ITEM_ID");
		initView();
	}
	
	private void initView(){
		titleLeftBtn.setOnClickListener(this);
		titleRightBtn2.setText("确定");
		titleRightBtn2.setVisibility(View.VISIBLE);
		titleRightBtn2.setOnClickListener(this);
		titleTextV.setText("特殊人群收费");
		
		mGroupName = (EditText) findViewById(R.id.group_name);
		mGroupPrice = (EditText) findViewById(R.id.group_price);
		Button delete=(Button)findViewById(R.id.delete_item);
		if(getIntent().hasExtra("entity")){
			titleTextV.setText("修改特殊人群收费");
			isUpdate=true;
			mEntity = (DoctorServiceGroupEntity) getIntent().getSerializableExtra("entity");
			mGroupName.setText(mEntity.getSPECIAL_GROUP());
			mGroupPrice.setText(mEntity.getSPECIAL_PRICE());
			delete.setOnClickListener(this);
			delete.setVisibility(View.VISIBLE);
		}else{
			delete.setVisibility(View.GONE);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.title_right2:
			SystemUtils.hideSoftBord(getApplicationContext(), mGroupName);
			String name=mGroupName.getText().toString();
			String price=mGroupPrice.getText().toString();
			if(name==null||"".equals(name)){
				ToastUtil.showBasicShortToast(getApplicationContext(), "请输入特殊收费人群名称");
				return ;
			}
			
			if(name.length()>10){
				ToastUtil.showBasicShortToast(getApplicationContext(), "特殊收费人群名称最多支持十个字哦!");
				return ;
			}
			if(price==null||"".equals(price)){
				ToastUtil.showBasicShortToast(getApplicationContext(), "请输入服务价格");
				return;
			}
			int pri=Integer.valueOf(price);
			if(pri>19999){
				ToastUtil.showBasicShortToast(getApplicationContext(), "服务最大金额不能超过19999元/人");
				return ;
			}
			if(isUpdate){
				//修改特殊收费人群
				updateData(mEntity.getSERVICE_ITEM_ID(), mEntity.getSPECIAL_GROUP_ID(), price, name);
			}else{
				//创建特殊收费人群
				subMint(name,pri);
			}
			break;
		case R.id.delete_item:
			DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  "删除人群后该人群内的成员将不再享受特殊收费,你确认要删除人群<<"+mGroupName.getText().toString()+">>吗?", "放弃", "确定", new OnDilaogClickListener() {
				@Override
				public void onDismiss(DialogFragment fragment) {
				}
				@Override
				public void onClick(DialogFragment fragment, View v) {
					deleteItem(mEntity.getSPECIAL_GROUP_ID());
				}
			});
			break;
		}
		
	}
	
	/**
	 * 删除特殊收费人群
	 * @param id
	 */
	private void deleteItem(String id){
		ApiService.doHttpDeleteServiceGroup(id,new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject object=new JSONObject(content);
					if(object.has("error_message")){
						ToastUtil.showBasicShortToast(getApplicationContext(), object.getString("error_message")+"");
					}else{
						setResult(RESULT_OK, getIntent());
						finish();
						ToastUtil.showBasicShortToast(getApplicationContext(), "删除成功");	
					}
				} catch (JSONException e) {
				}
			}
		});
	}
	
	/**
	 * Type=insertSpecialPriceGroup
		SPECIAL_GROUP 服务特殊收费人群名称
		SPECIAL_PRICE 特殊服务价格
	 */
	private void subMint(String name,int price){
		ApiService.doHttpCreateDoctorServiceGroup(name,price,mServiceItemId,new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				try {
					JSONObject object=new JSONObject(content);
					if(object.has("error_message")){
						ToastUtil.showBasicShortToast(getApplicationContext(), object.getString("error_message")+"");
					}else{
						setResult(RESULT_OK, getIntent());
						finish();
						overridePendingTransition(R.anim.anim_activity_close_enter,R.anim.anim_activity_close_exit);
						ToastUtil.showBasicShortToast(getApplicationContext(), "创建成功");
					}
				} catch (JSONException e) {
				}
				
			}
		});
	}
	
	//修改
	/**
	 * Type=updateSpecialPriceGroup
	SERVICE_ITEM_ID 医生服务项目ID
	SPECIAL_GROUP_ID 服务特殊收费人群ID
	SPECIAL_GROUP 服务特殊收费人群名称
	SPECIAL_PRICE 服务价格
	 */
	private void updateData(String ITEM_ID,String GROUP_ID,String SPECIAL_PRICE,String SPECIAL_GROUP){
		ApiService.doHttpUpdateDoctorServiceGroup(ITEM_ID,GROUP_ID,SPECIAL_PRICE,SPECIAL_GROUP,new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				try {
					JSONObject object=new JSONObject(content);
					if(object.has("error_message")){
						ToastUtil.showBasicShortToast(getApplicationContext(), object.getString("error_message")+"");
					}else{
						setResult(RESULT_OK, getIntent());
						finish();
						ToastUtil.showBasicShortToast(getApplicationContext(), "修改成功");
					}
				} catch (JSONException e) {
				}
			}
		});
	}
}
