package com.yksj.consultation.sonDoc.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.CirclePageIndicator;
import com.yksj.healthtalk.entity.ChooseTagsEntity;
import com.yksj.healthtalk.entity.TagEntity;
import com.yksj.healthtalk.utils.XMLUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseTagsActivity extends BaseActivity implements OnClickListener{
	private GridView mGridView;
	private ViewPager mPager;
	private TagsSelectedAdapter mTagsSelectedAdapter;
	private TagsPagerAdapter mPagerAdapter;
	private Map<String,String> mMapData;
	private ArrayList<TagEntity> data;
	
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.choose_tags_layout);
		super.onCreate(arg0);
		initData();
		initUI();
	}
	
	private void initUI(){
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText("选择分类");
		titleRightBtn2.setOnClickListener(this);
		titleRightBtn2.setText(getString(R.string.crop_save_text));
		titleRightBtn2.setVisibility(View.VISIBLE);
		
		int type=getIntent().getIntExtra("type", 1);
		List<ChooseTagsEntity> tags = XMLUtils.parseUserLablesTags(getResources(),R.xml.user_chooice_tags,type);
		mGridView = (GridView)findViewById(R.id.gridview);
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mPager = (ViewPager)findViewById(R.id.pager);
		mGridView.setAdapter((mTagsSelectedAdapter = new TagsSelectedAdapter(this,null)));
		mPager.setAdapter(mPagerAdapter = new TagsPagerAdapter(this,spliteGroup(tags,15)));
		indicator.setViewPager(mPager);
		onSelectedTags(tags);
	}
	/**
	 * 初始化已选择的标签
	 */
	private void initData(){
		data = getIntent().getParcelableArrayListExtra("attentionInfo");
		mMapData = new HashMap<String, String>();
		if(data != null){
			for (int i=0;i<data.size();i++) {
				mMapData.put(data.get(i).getName(),data.get(i).getName());
			}
		}
	}
	/**
	 * 确定后,把标签对象传递过去
	 */
	private void onSaveTags(){
		List<ChooseTagsEntity> list = mTagsSelectedAdapter.getList();
		Intent intent =new Intent();
		data.clear();
		for (int i = 0; i < list.size(); i++) {
			TagEntity entity=new TagEntity(list.get(i).getId(), list.get(i).getName(),true);
			data.add(entity);
			
		}
		intent.putParcelableArrayListExtra("attention", data);
		intent.setClass(ChooseTagsActivity.this,PersonInfoActivity.class);
		setResult(100, intent);
		finish();
		overridePendingTransition(R.anim.anim_activity_close_enter,R.anim.anim_activity_close_exit);
	}
	
	
	private void onSelectedTags(List<ChooseTagsEntity> tags){
		for (int i = 0; i < tags.size(); i++) {
			ChooseTagsEntity tags2 = tags.get(i);
			if(mMapData.containsValue(tags2.getName())){
				tags2.setSelected(true);
				mTagsSelectedAdapter.add(tags2);
			}
		}
	}
	
	/**
	 * 分组拆分
	 * @param list
	 * @return
	 */
	private List<List<ChooseTagsEntity>> spliteGroup(List<ChooseTagsEntity> list,int groupSize){
		int length;
		List<List<ChooseTagsEntity>> lists = new ArrayList<List<ChooseTagsEntity>>();
		if((length=list.size()) <= groupSize || length == 0){
			lists.add(list);
			return lists;
		}
		boolean isEnd = true;
		int indexStart = 0;
		int indexEnd = groupSize;
		while (isEnd) {
			if(indexEnd >= length){
				lists.add(list.subList(indexStart,length-1));
				break;
			}else{
				lists.add(list.subList(indexStart,indexEnd));
				indexStart = indexEnd;
				indexEnd += groupSize;
			}
		}
		return lists;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_activity_close_enter,R.anim.anim_activity_close_exit);
			break;
		case R.id.title_right2:
			onSaveTags();
			break;
		}
	}
	/**
	 * 
	 * 已经选择的所有标签
	 * @author zhao
	 *
	 */
	public  class  TagsSelectedAdapter extends BaseAdapter{
		final List<ChooseTagsEntity> list = new ArrayList<ChooseTagsEntity>();
		final LayoutInflater mInflater;
		final Context context;
		public TagsSelectedAdapter(Context context,List<ChooseTagsEntity> tags) {
			mInflater = LayoutInflater.from(context);
			this.context = context;
			if(tags != null)list.addAll(tags);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public ChooseTagsEntity getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		public void add(ChooseTagsEntity tags){
			list.add(tags);
			notifyDataSetChanged();
		}
		
		public void removeTag(ChooseTagsEntity tag){
			list.remove(tag);
			notifyDataSetChanged();
		}
		
		public void addAll(Collection<ChooseTagsEntity> collection){
			list.addAll(collection);
		}
		
		public List<ChooseTagsEntity> getList(){
			return list;
		}
		
		public void onItemClick(ChooseTagsEntity tag){
			tag.setSelected(false);
			list.remove(tag);
			notifyDataSetChanged();
			mPagerAdapter.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ChooseTagsEntity tag = getItem(position);
			final CheckBox checkBox;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.chooce_tags_item,null);
				checkBox = (CheckBox)convertView.findViewById(R.id.tag_name);
				checkBox.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.biaoqian_bg));
			}else{
				checkBox = (CheckBox)convertView.findViewById(R.id.tag_name);
			}
			checkBox.setText(tag.getName());
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(tag);
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 下面滑动部分
	 * @author zhao
	 *
	 */
	private class TagsPagerAdapter extends PagerAdapter{
		final Context mContext;
		final LayoutInflater mInflater;
		private final List<GridView> mGridViews;
		
		public TagsPagerAdapter(Context context,List<List<ChooseTagsEntity>> list){
			this.mContext = context;
			this.mInflater = LayoutInflater.from(mContext);
			this.mGridViews = new ArrayList<GridView>();
			for (List<ChooseTagsEntity> list2 : list) {
				GridView gridView = (GridView)mInflater.inflate(R.layout.tags_gridview_layout,null);
				mGridViews.add(gridView);
				gridView.setAdapter(new TagsPagerItemAdapter(list2,mInflater));
			}
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			View view = mGridViews.get(position);
			GridView gridView = (GridView)view;
			BaseAdapter adapter = (BaseAdapter)gridView.getAdapter();
			adapter.notifyDataSetChanged();
			((ViewPager)container).addView(view,0);
			return view;
		}
		
		@Override
		public int getCount() {
			return mGridViews.size();
		}

		@Override  
		public int getItemPosition(Object object) {  
			return POSITION_NONE;  
		}  

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
		}
		
	}
	
	private class TagsPagerItemAdapter extends BaseAdapter{
		final List<ChooseTagsEntity> mList;
		final LayoutInflater mInflater;
		
		public TagsPagerItemAdapter(List<ChooseTagsEntity> list,LayoutInflater inflater){
			this.mInflater = inflater;
			this.mList = list;
		}
		
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public ChooseTagsEntity getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ChooseTagsEntity tags = getItem(position);
			final ViewHolder viewHolder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.chooce_tags_item,null);
				viewHolder = new ViewHolder();
				viewHolder.checkBox = (CheckBox)convertView.findViewById(R.id.tag_name);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			if(tags.isSelected()){
				viewHolder.checkBox.setChecked(true);
			}else{
				viewHolder.checkBox.setChecked(false);
			}
			viewHolder.checkBox.setText(tags.getName());
			viewHolder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(viewHolder.checkBox,tags);
				}
			});
			return convertView;
		}
		
		/**
		 * 标签点击事件
		 * @param box
		 * @param tags
		 */
		public void onItemClick(CheckBox box,ChooseTagsEntity tags){
			if(box.isChecked()){
				tags.setSelected(true);
				mTagsSelectedAdapter.add(tags);
			}else{
				tags.setSelected(false);
				mTagsSelectedAdapter.removeTag(tags);
			}
		}
	}
	
	private static class ViewHolder{
		CheckBox checkBox;
	}
}
