package com.yksj.consultation.sonDoc.salon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 特殊成员列表
 * @author zhao
 * 
 */
public class SalonSpecialPeoplsListActivity extends BaseActivity
		implements OnClickListener {
	
	PullToRefreshListView mListView;
	DataListAdapter mAdapter;
	String Special_Group_ID;//按门票
	String Service_Group_ID;//按服务特殊收费人群列表
	String SPECIAL_PRICE;
	String SPECIAL_GROUP;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Override
	protected void onCreate(Bundle saveBundle) {
		super.onCreate(saveBundle);
		setContentView(R.layout.salon_special_peoples_list_layout);
		if(saveBundle != null){
			Special_Group_ID = saveBundle.getString("Special_Group_ID");
			Service_Group_ID = saveBundle.getString("Service_Group_ID");
			SPECIAL_PRICE = saveBundle.getString("SPECIAL_PRICE");
		}else{
			Special_Group_ID = getIntent().getStringExtra("Special_Group_ID");
			Service_Group_ID = getIntent().getStringExtra("Service_Group_ID");
			SPECIAL_PRICE = getIntent().getStringExtra("SPECIAL_PRICE");
			SPECIAL_GROUP = getIntent().getStringExtra("SPECIAL_GROUP");
		}
		initUI();
		
		initData();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("Special_Group_ID", Special_Group_ID);
		outState.putString("Service_Group_ID", Service_Group_ID);
		outState.putString("SPECIAL_PRICE",SPECIAL_PRICE);
		
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		initData();
		
	}

	@SuppressWarnings("unchecked")
	private void initUI() {
		initializeTitle();
		titleTextV.setText(R.string.member);
		titleLeftBtn.setOnClickListener(this);
		setRight("添加", this);
		mListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
		mAdapter = new DataListAdapter(getLayoutInflater(), this);
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mListView.onRefreshComplete();
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				mListView.onRefreshComplete();
			}
		});
		mListView.getRefreshableView().addFooterView(getLayoutInflater().inflate(R.layout.line_layout, null));
		mListView.getRefreshableView().setFooterDividersEnabled(false);
		View view = getLayoutInflater().inflate(R.layout.commn_empty_layout,null);
		((TextView)view.findViewById(R.id.empty_txt)).setText("该特殊人群暂无成员，点击右上角可开始添加。");
		mListView.setEmptyView(view);
	}
	
	private void initData(){
		if(Special_Group_ID != null){//按群查找特殊收费成员
//			customerId 客户id specialGroupId 话题特殊收费人群id type=findSalonSpecialPriceGroupMember
			RequestParams params = new RequestParams();
			params.put("customerId", SmartFoxClient.getLoginUserId());
			params.put("specialGroupId",Special_Group_ID);
			params.put("type","findSalonSpecialPriceGroupMember");
			params.put("VALID_MARK","40");
			ApiService.doHttpSalonSpecialPriceGroupSet(params,new JsonsfHttpResponseHandler(this){
				@Override
				public void onSuccess(int statusCode, JSONArray response) {
					super.onSuccess(statusCode, response);
					mAdapter.onDataChange(response);
				}
			});
		}else{
			/*
			Type=findSpecialPriceGroupMember
			CUSTOMER_ID 客户id
			SPECIAL_GROUP_ID 服务特殊收费人群ID
			Type=findSpecialPriceGroupMember
			CUSTOMER_ID 客户id
			SPECIAL_GROUP_ID 服务特殊收费人群ID
			*/
			RequestParams params = new RequestParams();
			params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			params.put("SPECIAL_GROUP_ID",Service_Group_ID);
			params.put("Type","findSpecialPriceGroupMember");
			ApiService.doHttpServiceSetServlet320(params,new JsonsfHttpResponseHandler(this){
				@Override
				public void onSuccess(int statusCode, JSONArray response) {
					super.onSuccess(statusCode, response);
					mAdapter.onDataChange(response);
				}
			});
		}
	}
	
	/**
	 * 
	 * 按服务类型删除群成员
	 * @param object
	 * 
	 */
	private void onDeleteGroupMenberByService(final JSONObject object){
		/*Type=deleteSpecialPriceGroupMember
		SERVICE_ITEM_ID 医生服务项目ID
		CUSTOMER_ID 客户id
		*/
		RequestParams params = new RequestParams();
		params.put("Type", "deleteSpecialPriceGroupMember");
		params.put("SERVICE_ITEM_ID",getIntent().getStringExtra("SERVICE_ITEM_ID"));
		params.put("CUSTOMER_ID", object.getString("customerId"));
		ApiService.doHttpServicesetservletrj320(params,
				new JsonsfHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				if(response.containsKey("error_message")){
					ToastUtil.showShort(response.getString("error_message"));
				}else{
					mAdapter.mArray.remove(object);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	/**
	 * 按话题删除群成员
	 * @param object
	 */
	private void onDeleteGroupMenberByGroup(final JSONObject object){
		RequestParams params = new RequestParams();
		params.put("type", "deleteSalonSpecialPriceGroupMember");
		params.put("specialGroupId",Special_Group_ID);
		params.put("customerId", object.getString("customerId"));
		ApiService.doHttpSalonSpecialPriceGroupSet(params,
				new JsonsfHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				if(response.containsKey("error_message")){
					ToastUtil.showShort(response.getString("error_message"));
				}else{
					mAdapter.mArray.remove(object);
					mAdapter.notifyDataSetChanged();
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
		case R.id.title_right2://添加成员
//			Intent intent = getIntent();
////			intent.setClass(this, SalonSpecialPeoplsMenberAddActivity.class);
//			intent.setClass(this, TopicSpecialPriceGroupAddMainUi.class);
//			startActivity(intent);
			break;
		}
	}

	private class DataListAdapter extends BaseAdapter {
		JSONArray mArray = new JSONArray();
		final LayoutInflater mInflater;
		private final ImageLoader mImageLoader;

		private void onDataChange(JSONArray array){
			this.mArray = array;
			notifyDataSetChanged();
		}
		
		public void onDataChangeByAddEnd(JSONArray array){
			mArray.addAll(array);
			notifyDataSetChanged();
		}
		
		public DataListAdapter(LayoutInflater inflater, Context context) {
			this.mInflater = inflater;
			this.mImageLoader = ImageLoader.getInstance();
		}

		@Override
		public int getCount() {
			return mArray.size();
		}

		@Override
		public JSONObject getItem(int position) {
			return mArray.getJSONObject(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			final JSONObject jsonObject = getItem(position);
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.special_peopl_item,null);
				viewHolder.headerImageV = (ImageView)convertView.findViewById(R.id.head_image);
				viewHolder.nameTextV = (TextView)convertView.findViewById(R.id.name);
				viewHolder.sexImageV = (ImageView)convertView.findViewById(R.id.head_sex);
				viewHolder.levelImageV = (ImageView)convertView.findViewById(R.id.levl);
				viewHolder.noteTextV = (TextView)convertView.findViewById(R.id.note);
				viewHolder.timeTextV = (TextView)convertView.findViewById(R.id.add_time);
				viewHolder.deletButton = convertView.findViewById(R.id.delet_btn);
				viewHolder.sexImageV.setVisibility(View.GONE);
				viewHolder.levelImageV.setVisibility(View.GONE);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			//LevelListDrawable listDrawable = (LevelListDrawable)viewHolder.sexImageV.getDrawable();
			//listDrawable.setLevel(jsonObject.getIntValue("customerSex"));
			viewHolder.nameTextV.setText(jsonObject.getString("customerNickname"));
//			viewHolder.noteTextV.setText(jsonObject.getString("personalNarrate"));
//			viewHolder.noteTextV.setText(SPECIAL_PRICE + "元");
			try{
				String time = jsonObject.getString("addTime");
				Date date = format.parse(time);
				viewHolder.timeTextV.setText("添加时间 :"+format1.format(date));
			}catch(Exception e){
			}
			//int roldid = jsonObject.getIntValue("roleId");
//			viewHolder.levelImageV.setVisibility((roldid == 777 || roldid == 888)? View.VISIBLE:View.GONE);
//			mHeaderImageOptions=DefaultConfigurationFactory.createSexHeaderDisplayImageOptions(String.valueOf(jsonObject.getIntValue("customerSex")), SalonSpecialPeoplsListActivity.this);
			mImageLoader.displayImage(jsonObject.getString("customerSex"),jsonObject.getString("clientIconBackground"),viewHolder.headerImageV);
			viewHolder.deletButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String name = jsonObject.getString("customerNickname");
					name = "您确认将"+name+"从"+SPECIAL_GROUP+"中删除吗?";
					DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),name,"放弃", "确认",new OnDilaogClickListener() {
						@Override
						public void onDismiss(DialogFragment fragment) {
						}
						@Override
						public void onClick(DialogFragment fragment, View v) {
							if(Special_Group_ID != null){//按话题
								onDeleteGroupMenberByGroup(jsonObject);
							}else{//按服务类型
								onDeleteGroupMenberByService(jsonObject);
							}
						}
					});
				}
			});
			return convertView;
		}
	}
	
	private static class ViewHolder{
		ImageView headerImageV;
		TextView nameTextV;
		ImageView sexImageV;
		ImageView levelImageV;
		TextView noteTextV;
		TextView timeTextV;
		View  deletButton;
	}

}
