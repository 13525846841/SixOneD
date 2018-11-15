package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.DoctorSimpleBean;

import org.universalimageloader.core.ImageLoader;

/**
 * 选择会诊专家列表的适配器
 * @author lmk
 *
 */
public class RecommendListAdapter extends SimpleBaseAdapter<DoctorSimpleBean> {

	private ImageLoader mInstance;
	private OnClickSelectListener followListener;
    private String pid=null;
	public RecommendListAdapter(Context context, String pid) {
		super(context);
        this.pid=pid;
		mInstance=ImageLoader.getInstance();
	}

	@Override
	public int getItemResource() {
		return R.layout.item_recommend_expert_list_item;
	}

	@Override
	public View getItemView(final int position, View convertView,
			ViewHolder holder) {
		final DoctorSimpleBean cus=datas.get(position);
		TextView tvName=(TextView) convertView.findViewById(R.id.select_expert_list_item_name);
//		TextView tvTitle=(TextView) convertView.findViewById(R.id.select_expert_list_item_doctitle);
		TextView tvHospital=(TextView) convertView.findViewById(R.id.select_expert_list_item_hospital);
		TextView tvSpecial=(TextView) convertView.findViewById(R.id.select_expert_list_item_spetical);
		ImageView icon=(ImageView) convertView.findViewById(R.id.select_expert_list_item_headicon);
		mInstance.displayImage("",cus.ICON_DOCTOR_PICTURE, icon);
		tvName.setText(cus.DOCTOR_REAL_NAME + " " + cus.TITLE_NAME);
//		tvTitle.setText(cus.TITLE_NAME);
		tvHospital.setText(cus.UNIT_NAME);
		tvSpecial.setText(cus.DOCTOR_SPECIALLY);



		return convertView;
	}

	public void setSelectListener(OnClickSelectListener followListener) {
		this.followListener = followListener;
	}

	public interface OnClickSelectListener{
		public void onClickSelect(DoctorSimpleBean dsb);
	}

}
