package com.yksj.consultation.sonDoc.dictionary;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonHttpResponseHandler;
import com.yksj.consultation.sonDoc.R;

/**
 * 
 * 显示内容列表
 * @author zhao
 *
 */
public class DictionaryExpandListFragment extends Fragment {
	int toNext = -1;
	String type;
	String code;
	
	ExpandableListView mExpandableListView;
	DictionaryExpandableListAdapter adapter;
	DictionaryListItemClickListener clickListener;
	JSONObject mResultJsonObj;//请求返回数据
	
	public interface DictionaryListItemClickListener{
		/**
		 * @param name
		 * @param code 参数1
		 * @param code1 参数2 //当三级菜单的时候需要这个参数
		 * @param index
		 */
		void onItemClick(String name,String code,int index);
	}
	
	final JsonHttpResponseHandler httpResponseHandler = new JsonHttpResponseHandler(getActivity()){
		@Override
		public void onSuccess(int statusCode, JSONObject response) {
			super.onSuccess(statusCode, response);
			onParseData(response);
		}
	};
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt("toNext",toNext);
		outState.putString("type",type);
		
		if(mResultJsonObj != null)
			outState.putString("data", mResultJsonObj.toString());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(getActivity() instanceof DictionaryListItemClickListener){
			clickListener = (DictionaryListItemClickListener)getActivity();
		}
		
		adapter = new DictionaryExpandableListAdapter(getActivity());
		mExpandableListView.setAdapter(adapter);
		type = getArguments().getString("type");
		toNext = getArguments().getInt("toNext");
		
		if(savedInstanceState != null){
			toNext = savedInstanceState.getInt("toNext");
			String type = savedInstanceState.getString("type");
			String data = null;
			if(savedInstanceState.containsKey("data")){
				data = savedInstanceState.getString("data");
			}
			if(type.equals(this.type) && data != null){
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(data);
					onParseData(jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				initData();
			}
		}else{
			initData();
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dictionary_content_expandlist_layout,null);
		mExpandableListView = (ExpandableListView)view.findViewById(android.R.id.list);
		mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				JSONObject jsonObject = adapter.getChild(groupPosition, childPosition);
				String name = adapter.getName(jsonObject);
				String code = adapter.getCode(jsonObject);
				v.setSelected(true);
				if(clickListener != null)clickListener.onItemClick(name, code,toNext);
				return false;
			}
		});
		//点击组的时候
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				int size = adapter.getChildrenCount(groupPosition);
				if(size == 0){
					JSONObject jsonObject = adapter.getGroup(groupPosition);
					String name = adapter.getName(jsonObject);
					String code = adapter.getCode(jsonObject);
					if(clickListener != null)clickListener.onItemClick(name, code,toNext);
					return true;
				}
				return false;
			}
		});
		return view;
	}
	
	/**
	 * 初始化一级菜单数据
	 */
	private void initData(){
		if("1".equals(type)){
			ApiService.doHttpQuerySituations(null, null,null,httpResponseHandler);
		}else if("10001".equals(type)){
			ApiService.doHttpQueryDiseasesServlet("1", null, null,null,null,null, httpResponseHandler);
		}else if("10002".equals(type)){
			ApiService.doHttpQueryDiseasesServlet("2", null, null,null,null,null, httpResponseHandler);
		}else if("10003".equals(type)){
			ApiService.doHttpQueryDiseasesServlet("3", null, null,null,null,null, httpResponseHandler);
		}else if("5".equals(type) && toNext == 1){//医院地区查询
			ApiService.doHttpQueryUnitsServlet("1", null, null, null, null,null, httpResponseHandler);
		}else if("10004".equals(type) && toNext == 1){
			ApiService.doHttpQueryMedicinesServlet("1", null, null,null,null,httpResponseHandler);
		}else if("10005".equals(type) && toNext == 1){
			ApiService.doHttpQueryMedicinesServlet("2", null, null,null,null, httpResponseHandler);
		}else if("10005".equals(type) && toNext == 2){
			ApiService.doHttpQueryMedicinesServlet("2", null, null,code,null, httpResponseHandler);
		}else if("10006".equals(type)){//按用途
			ApiService.doHttpQueryMedicinesServlet("3", null, null,null,null, httpResponseHandler);
		}
	}
	
	/**
	 * 解析数据
	 * @param response
	 */
	private void onParseData(JSONObject response){
		if(response == null)return;
		mResultJsonObj = response;
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		if(response != null){
			try {
				JSONArray jsonArray = response.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
						JSONArray jsonArray2;
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if(jsonObject.has("SUBMENUS")){
							jsonArray2 = jsonObject.getJSONArray("SUBMENUS");
						}else{
							jsonArray2 = new JSONArray();
						}
						groupList.add(jsonObject);
						childList.add(jsonArray2);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		adapter.onDataChange(groupList,childList);
	}
	
	private class DictionaryExpandableListAdapter extends BaseExpandableListAdapter{
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		final LayoutInflater mInflater;
		
		public DictionaryExpandableListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}
		
		private void onDataChange(List<JSONObject> groupList,List<JSONArray> childList){
			this.groupList.addAll(groupList);
			this.childList.addAll(childList);
			notifyDataSetChanged();
		}
		
		private String getName(JSONObject jsonObject){
			if(jsonObject == null)return "";
			try {
				String name = jsonObject.getString("NAME");
				return name;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}
		
		private String getCode(JSONObject jsonObject){
			if(jsonObject == null)return "";
			try {
				String code = jsonObject.getString("CODE");
				return code;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}
		
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return childList.get(groupPosition).length();
		}

		@Override
		public JSONObject getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groupList.get(groupPosition);
		}

		@Override
		public JSONObject getChild(int groupPosition, int childPosition) {
			try {
				return childList.get(groupPosition).getJSONObject(childPosition);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.dictionary_group_title, null);
			}
			TextView textView = (TextView)convertView;
			JSONObject jsonObject = getGroup(groupPosition);
			String name  = getName(jsonObject);
			textView.setText(name);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.dictionary_child_title, null);
			}
			TextView textView = (TextView)convertView;
			JSONObject jsonObject = getChild(groupPosition, childPosition);
			String name  = getName(jsonObject);
			textView.setText(name);
			return textView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	
}
