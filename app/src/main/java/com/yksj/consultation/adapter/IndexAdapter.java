package com.yksj.consultation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.PinnedHeaderListView.PinnedHeaderAdapter;

public class IndexAdapter extends BaseAdapter implements SectionIndexer,PinnedHeaderAdapter{
	private LayoutInflater inflater;
	ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	private HashMap<String, Integer> alphaIndexer; //<A,0>
	private HashMap<Integer, String> indexerSelects; //<0,a>
	private String[] sections;
	public static String SORT_KEY = "sort_key";
	private int layoutID = R.layout.list_item;
	// private OnIndexInitialListener listener;
//	private String[] letters = new String[] { "A", "B", "C", "D", "E", "F",
//			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
//			"T", "U", "V", "W", "X", "Y", "Z" };
	public IndexAdapter(Context context,
			HashMap<String, ArrayList<HashMap<String, Object>>> list) {
		this.inflater = LayoutInflater.from(context);
		this.sections = new String[list.size()];
		this.alphaIndexer = new HashMap<String, Integer>();
		this.indexerSelects = new HashMap<Integer, String>();
		ArrayList<String> arr = new ArrayList<String>(list.keySet());
		Collections.sort(arr);

		for (int i = 0; i < arr.size(); i++) {
			data.addAll(list.get(arr.get(i)));
		}

		for (int j = 0; j < data.size(); j++) {
			String name = (String) data.get(j).get(SORT_KEY);
			if (!alphaIndexer.containsKey(name)) {
				alphaIndexer.put(name, j);
				indexerSelects.put(j, name);
			}
		}

		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public HashMap<String, Object> getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(layoutID, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.text);

			if (layoutID == R.layout.knoweledge_factory_item) {
				holder.text = (TextView) convertView
						.findViewById(R.id.text_count);
				holder.factory = (TextView) convertView
						.findViewById(R.id.text_factory);
			} else{
				holder.header = (TextView) convertView.findViewById(R.id.text_header);
				holder.image = (ImageView) convertView.findViewById(R.id.icon);
				holder.area = (TextView) convertView.findViewById(R.id.text_middle);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, Object> cv = data.get(position);
		String name = (String) cv.get("zname");
		String text = (String) cv.get("text");
		if(holder.area != null){
			if(text != null){
				holder.area.setVisibility(View.VISIBLE);
				holder.area.setText(text);
			}else
				holder.area.setVisibility(View.GONE);
		}
			
		holder.name.setText(name);
		if (holder.image != null)
			holder.image.setVisibility(View.GONE);
		if (holder.factory != null) {
			holder.text.setText((String) cv.get("count"));
			holder.factory.setText((String) cv.get("factory"));
		}
		
		if(holder.header != null)
			 holder.header.setVisibility(View.GONE);
		return convertView;
	}

	public HashMap<String, Integer> getIndex() {
		return alphaIndexer;
	}

	public String[] getIndexs() {
		return sections;
	}

	private static class ViewHolder {
		TextView factory;
		TextView name;
		TextView text;
		ImageView image;
		TextView header;
		TextView area;
	}

	public interface OnIndexInitialListener {
		public void OnInitial(View view, ImageView imageView, int position);
	}

	/*
	 * public void setOnInitialListener(OnIndexInitialListener listener){
	 * this.listener = listener; }
	 */

	@Override
	public int getPositionForSection(int section){
		if(section < 0 || section >= sections.length)
			return -1;
		return alphaIndexer.get(sections[section]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		
	}
}
