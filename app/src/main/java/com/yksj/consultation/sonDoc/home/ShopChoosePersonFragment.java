package com.yksj.consultation.sonDoc.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;
import org.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.healthtalk.entity.SimpleUserEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.home.ShopOpenContactFragment.OnBackClickListener;

/**
 * 选择指定人
 * @author crj
 *
 */
public class ShopChoosePersonFragment extends Fragment implements OnGroupClickListener,OnClickListener,OnChildClickListener{

	public static final int TAG_SHOPCHOOSEPERSON = 2;
	//控件
	private ExpandableListView personListView;
	private ExplandableDataAdapter mAdapter;
	/**
	 * 选择指定开放到人的分组id
	 */
	private String classId;
	/**
	 * 进入该界面设置开放到人的馆的id
	 * shopid 和classid 用于获取是否已经对改分组设置了某些关注联系人。
	 */
	private String shopid;
	/**
	 * 数据源
	 */
	private List<List<SimpleUserEntity>> expandableDatas;
	/**
	 * 图片加载类
	 */
	private ImageLoader mImageLoader;
	private String imageLoadRoot;
	//监听器
	private OnBackClickListener backClickListener;
	private HashMap<Integer, String>  isAllSelectCheckedMap;
	
	
	public static ShopChoosePersonFragment getInstance(String classId,String shopId) {
		ShopChoosePersonFragment f = new ShopChoosePersonFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString("classId", classId);
		mBundle.putString("shopId", shopId);
		f.setArguments(mBundle);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null) return;
		classId = getArguments().getString("classId");
		shopid = getArguments().getString("shopId");
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.shop_choose_main, null);
		initView(view);
		getUserData();
		return view;
	}
	
	public void initData() {
		mAdapter = new ExplandableDataAdapter(getActivity());
		expandableDatas = new ArrayList<List<SimpleUserEntity>>();
		for(int i=0;i<3;i++) {
			expandableDatas.add(new ArrayList<SimpleUserEntity>());
		}
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getActivity());
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(configuration);
		imageLoadRoot = ApiService.getRepository().URL_QUERYHEADIMAGE;
	}
	
	public void initView(View view) {
		initTitle(view);
		personListView = (ExpandableListView) view.findViewById(R.id.personlistview);
		personListView.setAdapter(mAdapter);
		personListView.setOnGroupClickListener(this);
		personListView.setOnChildClickListener(this);
	}
	
	public void initTitle(View view) {
		Button backButton = (Button) view.findViewById(R.id.title_back);
		backButton.setOnClickListener(this);
		backButton.setText("返回");
	}	
	public class ExplandableDataAdapter extends BaseExpandableListAdapter {

		private String[] str;
		private LayoutInflater inflater;
		private boolean isChildBeClick = false;
		
		public ExplandableDataAdapter(Context context) {
			str = new String[] {"关注好友","我的粉丝","我的医生"};
			inflater = LayoutInflater.from(context);
			isAllSelectCheckedMap = new HashMap<Integer, String>();
		}
		
		@Override
		public int getGroupCount() {
			return str.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			//如果有数据则有全选，没数据则没全选
			if(expandableDatas.get(groupPosition).size()>0)
			return expandableDatas.get(groupPosition).size()+1;
			else return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return str[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return str[groupPosition];
		}
		
		@Override
		public int getChildType(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return super.getChildType(groupPosition, childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
			ViewHolder groupHolder;
			if(convertView==null) {
				convertView = inflater.inflate(R.layout.shop_person_group_item,null);
				groupHolder = new ViewHolder();
				groupHolder.mTextView = (TextView) convertView.findViewById(R.id.group_name);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (ViewHolder) convertView.getTag();
			}
			groupHolder.mTextView.setText(str[groupPosition]);
			if(isExpanded) {
				groupHolder.mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.friend_group_arrows_down, 0);
			} else {
				groupHolder.mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.friend_group_arrows, 0);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
				ChildHolder holder;
				if(convertView==null) {
					holder = new ChildHolder();
					convertView = inflater.inflate(R.layout.shop_person_child_item,null);
					holder.mTextView = (TextView) convertView.findViewById(R.id.headText);
					holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.personcheck);
					holder.headImage = (ImageView) convertView.findViewById(R.id.headImage);
					convertView.setTag(holder);
				} else {
					holder = (ChildHolder) convertView.getTag();
				}
				//如果有数据才显示全选
				if(getChildrenCount(groupPosition)>1) {
					if(childPosition==0) {
						//第一项为全选项
						holder.headImage.setVisibility(View.INVISIBLE);
						holder.mTextView.setText("");
						holder.mCheckBox.setText("全选");
						holder.mCheckBox.setChecked(false);
						if (isAllSelectCheckedMap.get(groupPosition) != null) {
							if (isAllSelectCheckedMap.get(groupPosition).equals("Y")) {
								holder.mCheckBox.setChecked(true);
							}
						}
					} else {
						//其他项设置信息
						holder.headImage.setVisibility(View.VISIBLE);
						holder.mCheckBox.setText("");
						if(expandableDatas.get(groupPosition)!=null&&expandableDatas.get(groupPosition).size()>0) {
							holder.mTextView.setText(expandableDatas.get(groupPosition).get(childPosition-1).getCusName());
						}
							holder.mCheckBox.setChecked(expandableDatas.get(groupPosition).get(childPosition-1).getIsBeChoose().equals("Y"));
						holder.headImage.setImageResource(R.drawable.default_head_mankind);
						mImageLoader.displayImage(imageLoadRoot+expandableDatas.get(groupPosition).get(childPosition-1).getUser_icon_address(), holder.headImage);
					}
					holder.mCheckBox.setOnClickListener(new OnGroupCheckChangeListener(holder.mCheckBox,groupPosition,childPosition));
				}
				return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		private class ChildHolder{
			TextView mTextView;
			CheckBox mCheckBox;
			ImageView headImage;
		}
		
		private class ViewHolder {
			TextView mTextView;
		}

		public class OnGroupCheckChangeListener implements OnClickListener {

			private int groupPosition;
			private int childPosition;
			private CheckBox box;
			
			OnGroupCheckChangeListener(CheckBox box ,int groupPosition , int childPosition) {
				this.box = box;
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}

			@Override
			public void onClick(View v) {
				if (box.isChecked()) {
					box.setChecked(true);
				}else {
					box.setChecked(false);
				}
				if (childPosition == 0) {
					List<SimpleUserEntity> users = expandableDatas.get(groupPosition);
					String str = "";
					if(box.isChecked()) {
						str = "Y";
					} else {
						str = "N";
					}
					isAllSelectCheckedMap.put(groupPosition, str);
					for(int i=0;i<users.size();i++) {
						users.get(i).setIsBeChoose(str);
					}
					mAdapter.notifyDataSetChanged();
				}else {
					if (box.isChecked()) {
						expandableDatas.get(groupPosition).get(childPosition-1).setIsBeChoose("Y");
					}else {
						expandableDatas.get(groupPosition).get(childPosition-1).setIsBeChoose("N");
					}
				}
			}
			
		}
		
	}
	
	/**
	 * 分别解析好友，粉丝，医生信息
	 * @param content
	 * @return
	 */
	public List<List<SimpleUserEntity>> onParseAllData(String content) {
		List<List<SimpleUserEntity>> datas  = new ArrayList<List<SimpleUserEntity>>();
		try {
			JSONObject jsonObject = new JSONObject(content);
			JSONArray jsonArray = jsonObject.getJSONArray("FRIENDS");
			datas.add(onParseSimpleData(jsonArray));
			JSONArray jsonArray1 = jsonObject.getJSONArray("FANS");
			datas.add(onParseSimpleData(jsonArray1));
			JSONArray jsonArray2 = jsonObject.getJSONArray("DOCTORS");
			datas.add(onParseSimpleData(jsonArray2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datas;
	}
	
	/**
	 * Json简单解析
	 * @param content
	 * @return
	 */
	public List<SimpleUserEntity> onParseSimpleData(JSONArray content) {
		List<SimpleUserEntity> datas = new ArrayList<SimpleUserEntity>();
		try {
			for(int i=0;i<content.length();i++) {
				SimpleUserEntity entity = new SimpleUserEntity();
				JSONObject jsonObject = content.getJSONObject(i);
				entity.setCusid(jsonObject.getString("CUSTOMERID"));
				entity.setUser_icon_address(jsonObject.getString("CLIENT_ICON_BACKGROUND"));
				entity.setCusName(jsonObject.getString("CUSTOMER_NICKNAME"));
				entity.setIsBeChoose(jsonObject.getString("ISSETTING"));
				datas.add(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datas;
	}

	/**
	 * 将被选中的cusid用逗号分隔串联起来
	 * @return
	 */
	public String buildCusIds() {
		String cusids = "";
		for(int i=0;i<expandableDatas.size();i++) {
			for(int j=0;j<expandableDatas.get(i).size();j++) {
				if(expandableDatas.get(i).get(j).getIsBeChoose().equals("Y")) {
					cusids+=expandableDatas.get(i).get(j).getCusid()+",";
				}
			}
		}
		int position = cusids.lastIndexOf(",");
		if(position!=-1) {
			cusids = cusids.substring(0, position);
			return cusids;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		return false;
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		backClickListener = (OnBackClickListener) activity;
	}
	
	@Override
	public void onClick(View v) { 
		switch(v.getId()) {
		case R.id.title_back:
//			backClickListener.onBackClick(TAG_SHOPCHOOSEPERSON,-1,buildCusIds(),classId);
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(childPosition!=0) {
			//点击某一项就修改某一项的状态
			if(expandableDatas.get(groupPosition).get(childPosition-1).getIsBeChoose().equals("N")) {
				expandableDatas.get(groupPosition).get(childPosition-1).setIsBeChoose("Y");
			} else {
				expandableDatas.get(groupPosition).get(childPosition-1).setIsBeChoose("N");
			}
			mAdapter.notifyDataSetChanged();
		} 
		return false;
	}

	/*********************************************************
	 * 网络请求
	 **********************************************************/
	private final ObjectHttpResponseHandler handler = new ObjectHttpResponseHandler(getActivity()) {
		
		@Override
		public Object onParseResponse(String cotent) {
			return onParseAllData(cotent);
		}
		
		@Override
		public void onSuccess(int statusCode, Object response) {
			expandableDatas.clear();
			expandableDatas.addAll((List<ArrayList<SimpleUserEntity>>)response);
			mAdapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * 获得与登录用户所有有关联的用户信息
	 */
	private void getUserData() {
		ApiService.doHttpQueryUserData(SmartFoxClient.getLoginUserId(),shopid,classId,handler);
	}
	
	
}
