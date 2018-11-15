package com.yksj.consultation.sonDoc.consultation.salon;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

import java.util.HashMap;


/**
 * 适配只有一个文字和一个向右的箭头的ListView
 * @author Administrator
 *
 */
public class TextImageIndexAdapter extends SimpleBaseAdapter<HashMap<String, Object>> {

	public TextImageIndexAdapter(Context context) {
		super(context);
	}

	@Override
	public int getItemResource() {
		return R.layout.attention_item;
	}

	@Override
	public View getItemView(int position, View convertView,
			SimpleBaseAdapter.ViewHolder holder) {
		TextView tv=(TextView) convertView.findViewById(R.id.att_title);
		tv.setText((String) datas.get(position).get("name"));
		return convertView;
	}

}
