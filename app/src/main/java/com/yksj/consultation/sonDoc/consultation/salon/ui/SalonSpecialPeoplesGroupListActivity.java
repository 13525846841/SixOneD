package com.yksj.consultation.sonDoc.consultation.salon.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.salon.SalonSpecialPeoplsListActivity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;

import org.handmark.pulltorefresh.library.PullToRefreshListView;


/**
 * 
 * 特殊收费人群组列表
 * @author zhao
 * 
 */
public class SalonSpecialPeoplesGroupListActivity extends BaseTitleActivity implements OnClickListener,OnItemClickListener {
	DataListAdapter mAdapter;
	PullToRefreshListView mListView;
	String mGroupId;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.salon_special_groups_layout);
		
		if(arg0 != null){
			mGroupId = arg0.getString("mGroupId");
		}else{
			mGroupId = getIntent().getStringExtra("groupid");
		}
		initUI();
		
		initData();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mGroupId", mGroupId);
		
	}
	
	private void initUI(){
		titleTextV.setText("特殊收费人群");
		titleLeftBtn.setOnClickListener(this);
		setRight("创建",this);
		mListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_listview);
		mListView.getRefreshableView().setFooterDividersEnabled(false);
		mListView.setAdapter(mAdapter = new DataListAdapter());
		mListView.setOnItemClickListener(this);
		View view = getLayoutInflater().inflate(R.layout.commn_empty_layout,null);
		((TextView)view.findViewById(R.id.empty_txt)).setText("还未创建收费人群");
		mListView.setEmptyView(view);
	}
	
	private void initData(){
		ApiService.doHttpQuerySalonSpecialGroup(mGroupId,new JsonsfHttpResponseHandler(this){
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				mAdapter.onDataChange(response);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.title_back:
			onBackPressed();  
			break;
		case R.id.title_right2://创建
			Intent intent = new Intent(this,SalonSpecialPeoplesGroupCreateActivity.class);
			intent.putExtra("groupid",mGroupId);
			startActivityForResult(intent,100);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode == RESULT_OK){
			String str = intent.getStringExtra("date");
			try {
				JSONArray array = JSON.parseArray(str);
				mAdapter.onDataChange(array);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this,SalonSpecialPeoplesGroupCreateActivity.class);
		intent.putExtra("groupid",mGroupId);
		intent.putExtra("SPECIAL_GROUP_ID",mAdapter.getItem(--position).toJSONString());
		startActivityForResult(intent,100);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}
	
	private class DataListAdapter extends BaseAdapter{
		JSONArray mArray = new JSONArray();
		public void onDataChange(JSONArray array){
			this.mArray = array;
			notifyDataSetChanged();
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
			final JSONObject jsonObject = getItem(position);
			TextView mTextView = null;
			TextView priceTextV = null;
			if(convertView == null){
				int type = getItemViewType(position);
				convertView = getLayoutInflater().inflate(R.layout.commn_txt_btn_item,null);
				mTextView = (TextView)convertView.findViewById(R.id.item_title);
				Button button = (Button)convertView.findViewById(R.id.item_menber);
				priceTextV = (TextView)convertView.findViewById(R.id.price_txt);
				convertView.setTag(R.id.item_menber,button);
				convertView.setTag(R.id.price_txt,priceTextV);
				convertView.setTag(mTextView);
				if(type == 1){
					convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_background_up));
				}else if(type == 2){
					convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_background_down));
				}else if(type == 3){
					convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_background_middle));
				}else{
					convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.text_background_single));
				}
			}else{
				mTextView = (TextView)convertView.getTag();
				priceTextV = (TextView)convertView.getTag(R.id.price_txt);
			}
			Button button = (Button)convertView.getTag(R.id.item_menber);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//查看成员
					Intent intent = getIntent();
					intent.setClass(SalonSpecialPeoplesGroupListActivity.this,SalonSpecialPeoplsListActivity.class);
					intent.putExtra("Special_Group_ID", jsonObject.getString("SPECIAL_GROUP_ID"));
					intent.putExtra("SPECIAL_PRICE", jsonObject.getString("SPECIAL_PRICE"));
					intent.putExtra("SPECIAL_GROUP", jsonObject.getString("SPECIAL_GROUP"));
					startActivity(intent);
				}
			});
//			 "CREATE_TIME": "20131215163027",
//		        "GROUP_ID": 114979,
//		        "SPECIAL_GROUP": "pppp",
//		        "SPECIAL_GROUP_ID": 53,
//		        "SPECIAL_PRICE": 1,
//		        "SPECIAL_PRICE_MONTH": 1
			priceTextV.setText(jsonObject.getString("SPECIAL_PRICE")+"元/日 ，"+jsonObject.getString("SPECIAL_PRICE_MONTH")+"元/月");
			mTextView.setText(jsonObject.getString("SPECIAL_GROUP"));
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			if(getCount() == 1){
				return 0;
			}
			if(position == 0){
				return 1;
			}else if((getCount()-1) == position){
				return 2;
			}
			return 3;
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}
		
	}
}
