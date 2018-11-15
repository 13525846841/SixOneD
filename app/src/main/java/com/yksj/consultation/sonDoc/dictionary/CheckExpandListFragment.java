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

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.dictionary.DictionaryExpandListFragment.DictionaryListItemClickListener;

/**
 * 
 * 检查内容显示
 * @author zhao
 *
 */
public class CheckExpandListFragment extends Fragment {
	DictionaryListItemClickListener clickListener;
	ExpandableListView mExpandableListView;
	DictionaryExpandableListAdapter adapter;
	private int mIndex;//当前的位置
	
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
				if(clickListener != null)clickListener.onItemClick(name,code,mIndex);
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
					if(clickListener != null)clickListener.onItemClick(name,code,mIndex);
					return true;
				}
				return false;
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getActivity() instanceof DictionaryListItemClickListener){
			clickListener = (DictionaryListItemClickListener)getActivity();
		}
		mIndex = getArguments().getInt("index");
		String content = getArguments().getString("parame");
		mExpandableListView.setAdapter(adapter = new DictionaryExpandableListAdapter(getActivity()));
		onParseData(content);
	}
	
	/**
	 * 解析数据
	 * @param response
	 */
	private void onParseData(String content){
		if(content == null)return;
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		try {
			JSONObject response = new JSONObject(content);
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
		adapter.onDataChange(groupList, childList);
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
			return true;
		}
	}
}
