package com.yksj.consultation.sonDoc.doctorstation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.healthtalk.entity.DoctorServiceGroupEntity;
import com.yksj.healthtalk.entity.DoctorSpecialServiceAdapter;
import com.yksj.healthtalk.entity.DoctorSpecialServiceAdapter.OnUpdateClickListener;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.salon.SalonSpecialPeoplsListActivity;
/**
 * 特殊收费人群
 * @author Administrator
 *
 */
public class DoctorSpecialServiceListActivity extends BaseActivity implements OnClickListener, OnItemClickListener,OnUpdateClickListener{

	private String mServiceItemId;
	List<DoctorServiceGroupEntity> entitys = new ArrayList<DoctorServiceGroupEntity>();
	private DoctorSpecialServiceAdapter adapter;
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctor_special_service);
		initializeTitle();
		initView();
	}

	private void initView() {
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText("特殊收费人群");
		setRight("创建", this);
		mListView = (ListView) findViewById(R.id.listview);
		adapter = new DoctorSpecialServiceAdapter(entitys,this,this);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		mServiceItemId = getIntent().getStringExtra("SERVICE_ITEM_ID");
		initData(mServiceItemId);
//		View view = getLayoutInflater().inflate(R.layout.commn_empty_layout,null);
//		((TextView)view.findViewById(R.id.empty_txt)).setText("还未创建收费人群");
//		listView.setEmptyView(view);
	}

	
	
	private void initData(String id) {
		/**
		 * Type=querySpecialPriceGroup
			SERVICE_ITEM_ID 医生服务项目ID
		 */
		ApiService.doHttpDoctorGroupList(id,new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				List<DoctorServiceGroupEntity> list = DoctorServiceGroupEntity.parseToList(content);
				if(list==null)return;
				adapter.boundData(list);
				if(adapter.getCount()==0){
					findViewById(R.id.load_faile_layout).setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
					
				}else{
					findViewById(R.id.load_faile_layout).setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.title_right2://创建特殊收费人群
			Intent intent=new Intent(this,DoctorCreateTalkGroup.class);
			intent.putExtra("SERVICE_ITEM_ID", mServiceItemId);
			startActivityForResult(intent, 1000);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=Activity.RESULT_OK)return;
		switch (requestCode) {
		case 1000:
			initData(mServiceItemId);
			break;
		}
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this,DoctorCreateTalkGroup.class);
		intent.putExtra("entity", entitys.get(position));
		startActivityForResult(intent, 1000);
	}
	
	
/**
 *  Type=addSpecialPriceGroupMember
	CUSTOMER_ID 客户id
	SPECIAL_GROUP_ID 服务特殊收费人群ID
	SERVICE_ITEM_ID 医生服务项目ID
	PARAME 添加的客户id，用放在数组里用json打包发过来
	CUSTOMERGROUPID 组id 
 */
	//特殊收费人群群成员
	@Override
	public void onUpdateClick(DoctorServiceGroupEntity mEntity) {
		Intent intent = new Intent(this,SalonSpecialPeoplsListActivity.class);
		intent.putExtra("groupid", mEntity.getSPECIAL_GROUP_ID());
		intent.putExtra("SPECIAL_GROUP", mEntity.getSPECIAL_GROUP());
		intent.putExtra("Service_Group_ID", mEntity.getSPECIAL_GROUP_ID());
		intent.putExtra("SERVICE_ITEM_ID", mEntity.getSERVICE_ITEM_ID());
		intent.putExtra("CUSTOMERGROUPID ", mEntity.getSPECIAL_GROUP_ID());
		startActivityForResult(intent, 1000);
	}
}
