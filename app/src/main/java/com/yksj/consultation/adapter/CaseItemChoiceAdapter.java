package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.dossier.CaseItemEntity;

import java.util.HashMap;

/**
 * 六一健康 病历项选择适配器
 */
public class CaseItemChoiceAdapter extends SimpleBaseAdapter<CaseItemEntity> implements SectionIndexer {

	public HashMap<Integer, Boolean> isSelected;

	public CaseItemChoiceAdapter(Context context) {
		super(context);
		isSelected = new HashMap<Integer, Boolean>();
	}

	public void itemCheck(int pos){
		Boolean check=datas.get(pos).isChecked;
		datas.get(pos).isChecked=!check;
		notifyDataSetChanged();
	}

	@Override
	public int getItemResource() {
		return R.layout.template_multiple_choise_item_layout;
	}

	@Override
	public View getItemView(final int position, View convertView,
			ViewHolder holder) {
		CaseItemEntity entity=datas.get(position);
		CheckBox cb=(CheckBox) holder.getView(R.id.template_multiple_choise_item_checkbox);
		TextView tvName=(TextView) holder.getView(R.id.template_multiple_choise_item_name);
		tvName.setText( entity.ITEMNAME);
		cb.setChecked( entity.isChecked);
//		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				isSelected.put(position, isChecked);
//			}
//		});
//		if(isSelected.containsKey(position)){
//			cb.setChecked(isSelected.get(position));
//		}else{
//			cb.setChecked(false);
//		}
		
		return convertView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return datas.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = datas.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 *
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
