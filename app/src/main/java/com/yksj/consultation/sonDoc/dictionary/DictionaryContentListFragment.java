package com.yksj.consultation.sonDoc.dictionary;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.PingYinUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DictionaryContentListFragment extends Fragment implements OnClickListener {
	
	ExpandableListView mExpandableListView;
	DictionnaryExpandListAdapter mAdapter;
	DictionnaryContentListItemClickListener mClickListener;
	Map<String,JSONArray> mTypeMap;//数据分类
	String mType;
	int mIndex;
	LinearLayout mIndexLayout;
	
	public static DictionaryContentListFragment instantiate(Context context,Bundle bundle){
		return (DictionaryContentListFragment)Fragment.instantiate(context,DictionaryContentListFragment.class.getName(),bundle);
	}
	
	public interface DictionnaryContentListItemClickListener{
		void onContentItemClick(String name,String code,int index);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dictionary_content_expandlist_layout,null);
		mIndexLayout = (LinearLayout)view.findViewById(R.id.indexView);
		mExpandableListView = (ExpandableListView)view.findViewById(android.R.id.list);
		//子项点击
		mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				JSONObject jsonObject = mAdapter.getChild(groupPosition, childPosition);
				String name = mAdapter.getName(jsonObject);
				String code = mAdapter.getCode(jsonObject);
				mAdapter.onSelect(jsonObject);
				mAdapter.notifyDataSetChanged();
				if(mClickListener != null)mClickListener.onContentItemClick(name,code,mIndex);
				return false;
			}
		});
		
		//点击组的时候
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				int size = mAdapter.getChildrenCount(groupPosition);
				if(size == 0){
					JSONObject jsonObject = mAdapter.getGroup(groupPosition);
					String name = mAdapter.getName(jsonObject);
					String code = mAdapter.getCode(jsonObject);
					mAdapter.onSelect(jsonObject);
					mAdapter.notifyDataSetChanged();
					if(mClickListener != null)mClickListener.onContentItemClick(name,code,mIndex);
					return true;
				}
				return false;
			}
		});
		mExpandableListView.setEmptyView(view.findViewById(R.id.list_empty));
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getActivity() instanceof DictionnaryContentListItemClickListener){
			mClickListener = (DictionnaryContentListItemClickListener)getActivity();
		}
		
		mIndex = getArguments().getInt("index");
		String content = getArguments().getString("parame");
		mType = getArguments().getString("type");
		if(mAdapter == null){
			mExpandableListView.setAdapter(mAdapter = new DictionnaryExpandListAdapter(getActivity()));
			if("10004".equals(mType) && mIndex == 0){
				onParseDataByPinyin(content);
			}else{
				onParseData(content);
			}
		}else{
			mExpandableListView.setAdapter(mAdapter);
		}
	}
	
	
	
	/**
	 * 
	 * 通过拼音解析数据
	 * @param content
	 */
	private void onParseDataByPinyin(String content){
		mExpandableListView.setEmptyView(null);
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		new AsyncTask<String, Void, Void>() {
			//进行英文首字母排序
			@Override
			protected Void doInBackground(String... params) {
				try{
					Object object1 = new JSONTokener(params[0]).nextValue();
					TreeMap<String,Object> treeMap = new TreeMap<String, Object>(new Comparator<String>() {
						//排序
						@Override
						public int compare(String o1, String o2) {
							return o1.compareTo(o2);
						}
					});
					if(object1 instanceof JSONObject){
						JSONArray jsonArray = ((JSONObject)object1).getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							String name = jsonObject.getString("NAME");
							name = PingYinUtil.getPingYin(getContext(),name.length() == 0 ? name:name.substring(0,1));
							if(treeMap.containsKey(name)){
								JSONArray array = (JSONArray)treeMap.get(name);
								array.put(jsonObject);
							}else{
								JSONArray array = new JSONArray();
								array.put(jsonObject);
								treeMap.put(name,array);
							}
						}
					}
					Set<String> sets = treeMap.keySet();
					for(String ketStr : sets){
						JSONArray array = (JSONArray)treeMap.get(ketStr);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("NAME", ketStr);
						groupList.add(jsonObject);
						childList.add(array);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				mAdapter.onDataChange(groupList, childList);
				for (int i = 0; i < groupList.size(); i++) {
					mExpandableListView.expandGroup(i);
					JSONObject jsonObject = groupList.get(i);
					String name = mAdapter.getName(jsonObject);
					final TextView textView = new TextView(getActivity());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					params.weight = 1;
					textView.setTag(i);
					textView.setText(name);
					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch(event.getAction()){
							case MotionEvent.ACTION_DOWN:
								mExpandableListView.setSelectedGroup((Integer)(textView.getTag()));
								break;
							}
							return false;
						}
					});
					mIndexLayout.addView(textView);
				}
			}
			protected void onCancelled(Void result) {
				
			};
		}.execute(content);
	}
	
	/**
	 * 解析数据
	 * @param content
	 */
	private void onParseData(String content){
		if(content == null)return;
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		try {
			Object object1 = new JSONTokener(content).nextValue();
			if(object1 instanceof JSONObject){
				JSONObject response = (JSONObject)object1;
				JSONArray jsonArray = response.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONArray jsonArray2;
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject.has("SUBMENUS")){
						jsonArray2 = jsonObject.getJSONArray("SUBMENUS");
						if("5".equals(mType)){//添加全部
							JSONObject object = new JSONObject();
							object.put("NAME", "全部");
							object.put("CODE", "0");
							jsonArray2.put(0,object);
						}
					}else{
						jsonArray2 = new JSONArray();
					}
					groupList.add(jsonObject);
					childList.add(jsonArray2);
				}
			}else if(object1 instanceof JSONArray){//附近医院
				JSONArray jsonArray = (JSONArray)object1;
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
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mAdapter.onDataChange(groupList, childList);
	}
	
	
	
	
	
	class DictionnaryExpandListAdapter extends BaseExpandableListAdapter{
		final List<JSONObject> groupList = new ArrayList<JSONObject>();
		final List<JSONArray> childList = new ArrayList<JSONArray>();
		final LayoutInflater mInflater;
		final List<Object> mSelectedList = new ArrayList<Object>();//选择集合
		
		
		public DictionnaryExpandListAdapter(Context context) {
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
		
		public String getCode(JSONObject jsonObject){
			if(jsonObject == null)return "";
			try {
				String code = jsonObject.getString("CODE");
				return code;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}
		
		/**
		 * 选中
		 * @param object
		 */
		public void onSelect(Object object){
			mSelectedList.clear();
			mSelectedList.add(object);
		}
		
		public boolean isSelected(Object jsonObject){
			return mSelectedList.contains(jsonObject);
		}
		
		@Override
		public int getGroupCount() {
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
			boolean  isSelected = isSelected(jsonObject);
			if(isSelected){
				textView.setBackgroundColor(getResources().getColor(R.color.gray_color2));
				convertView.setSelected(true);
			}else{
				textView.setBackgroundResource(R.color.dictionary_selector2);
				convertView.setSelected(false);
			}
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
			boolean  isSelected = isSelected(jsonObject);
			if(isSelected){
				textView.setBackgroundColor(getResources().getColor(R.color.gray_color2));
				convertView.setSelected(true);
			}else{
				textView.setBackgroundResource(R.color.dictionary_selector2);
				convertView.setSelected(false);
			}
			String name  = getName(jsonObject);
			textView.setText(name);
			return convertView;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		
	}
}
