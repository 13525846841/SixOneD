package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
public class SelectExpertListAdapter extends SimpleBaseAdapter<DoctorSimpleBean> {

	private ImageLoader mInstance;
	private OnClickSelectListener followListener;
    private String pid=null;
	public SelectExpertListAdapter(Context context,String pid) {
		super(context);
        this.pid=pid;
		mInstance=ImageLoader.getInstance();
	}

	@Override
	public int getItemResource() {
		return R.layout.select_expert_list_item;
	}

	@Override
	public View getItemView(final int position, View convertView,
			ViewHolder holder) {
		final DoctorSimpleBean cus=datas.get(position);
		TextView tvName=(TextView) convertView.findViewById(R.id.select_expert_list_item_name);
		TextView tvTitle=(TextView) convertView.findViewById(R.id.select_expert_list_item_doctitle);
		TextView tvHospital=(TextView) convertView.findViewById(R.id.select_expert_list_item_hospital);
		TextView tvSpecial=(TextView) convertView.findViewById(R.id.select_expert_list_item_spetical);
		TextView tvPrice=(TextView) convertView.findViewById(R.id.select_expert_list_item_price);
		TextView tvRemainNum=(TextView) convertView.findViewById(R.id.select_expert_list_item_num);
		Button imgFollow=(Button) convertView.findViewById(R.id.select_expert_list_item_select);
		ImageView icon=(ImageView) convertView.findViewById(R.id.select_expert_list_item_headicon);
		mInstance.displayImage("",cus.ICON_DOCTOR_PICTURE, icon);
		tvName.setText(cus.DOCTOR_REAL_NAME);
		tvTitle.setText(cus.TITLE_NAME);
		tvHospital.setText(cus.UNIT_NAME);
		tvSpecial.setText(cus.DOCTOR_SPECIALLY);
		if (cus.DOCTOR_SERVICE_NUMBER>0) {
			tvRemainNum.setTextColor(context.getResources().getColor(R.color.color_text_gray));
			tvRemainNum.setText("剩余" + cus.DOCTOR_SERVICE_NUMBER + "个服务名额");
			imgFollow.setBackgroundResource(R.drawable.icon_btn_bg_80);
			imgFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					followListener.onClickSelect(cus);

				}
			});
		}else {
			imgFollow.setOnClickListener(null);
			imgFollow.setBackgroundResource(R.drawable.icon_bg_gray_72);
			tvRemainNum.setTextColor(context.getResources().getColor(R.color.red));
			tvRemainNum.setText("服务名额已满");
		}
		tvPrice.setText(cus.SERVICE_PRICE + "元");


//		icon.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//			}
//		});

		return convertView;
	}

	public void setSelectListener(OnClickSelectListener followListener) {
		this.followListener = followListener;
	}

	public interface OnClickSelectListener{
		public void onClickSelect(DoctorSimpleBean dsb);
	}

}
