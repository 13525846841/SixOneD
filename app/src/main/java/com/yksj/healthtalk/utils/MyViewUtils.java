///**
// * 
// */
//package com.yksj.healthtalk.utils;
//
//import android.media.Image;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.yksj.healthtalk.entity.SeniorExpertsEntity;
//import com.yksj.consultation.ui.R;
//
///**
// * @author Administrator
// *
// */
//public class MyViewUtils implements OnClickListener {
//	private TextView tv1;
//	private ImageView v1;
//	private RelativeLayout rl;
//	private boolean specialExpanded=false,noticeExpanded=false;
//	private SeniorExpertsEntity see;
//	
//	public MyViewUtils(TextView tv1, ImageView v1, RelativeLayout rl,
//			SeniorExpertsEntity see) {
//		super();
//		this.tv1 = tv1;
//		this.v1 = v1;
//		this.rl = rl;
//		this.see = see;
//	}
//
//	public void unfold(){
//	//医生专长
//	if (!TextUtils.isEmpty(see.getSpeciality())) {
//		StringBuilder b = new StringBuilder();
//		b.append(see.getSpeciality());
//		tv1.setText(b);
//		
//		if(tv1.getLineCount()<3)//行数小于3,将展开按钮隐藏
//			v1.setVisibility(View.INVISIBLE);
//		else
//			rl.setOnClickListener(this);
//	} 
//}
//
//	@Override
//	public void onClick(View v) {
//		if(specialExpanded){
//			specialExpanded=false;
//			tv1.setMaxLines(2);
//			v1.setImageResource(R.drawable.tv_arrow_expand);
//		}else{
//			specialExpanded=true;
//			tv1.setMaxLines(100);
//			v1.setImageResource(R.drawable.tv_arrow_pack_up);
//		}
//	}
//}
