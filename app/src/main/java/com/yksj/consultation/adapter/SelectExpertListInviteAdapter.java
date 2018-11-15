package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.bean.ExpertStatus;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.bean.DoctorSimpleBeanInvite;

import org.universalimageloader.core.ImageLoader;


/**
 * 选择会诊专家列表的适配器
 *
 * @author lmk
 */
public class SelectExpertListInviteAdapter extends SimpleBaseAdapter<DoctorSimpleBeanInvite> {

    private ImageLoader mInstance;
    private OnClickSelectListener followListener;
    private int type = 0;//0 专家  1医生
    private int fromType = 0;//0默认情况  1表示来自我的医生不显示选择他按钮

    public SelectExpertListInviteAdapter(Context context, int type) {
        super(context);
        mInstance = ImageLoader.getInstance();
        this.type = type;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    @Override
    public int getItemResource() {
        return R.layout.select_expert_list_item_invite;
    }

    @Override
    public View getItemView(final int position, View convertView,
                            ViewHolder holder) {
        //final DoctorSimpleBean cus=datas.get(position);
        TextView tvName = (TextView) convertView.findViewById(R.id.select_expert_list_item_name);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.select_expert_list_item_doctitle);
        TextView tvHospital = (TextView) convertView.findViewById(R.id.select_expert_list_item_hospital);
        TextView tvSpecial = (TextView) convertView.findViewById(R.id.select_expert_list_item_spetical);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.select_expert_list_item_price);
        TextView tvRemainNum = (TextView) convertView.findViewById(R.id.select_expert_list_item_num);
        Button imgFollow = (Button) convertView.findViewById(R.id.select_expert_list_item_select);
        CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.iv_choice);
        TextView mCheckText = (TextView) convertView.findViewById(R.id.choice_tip);

        final DoctorSimpleBeanInvite doctorSimple = datas.get(position);

        if (fromType == 1)
            imgFollow.setVisibility(View.GONE);
        ImageView icon = (ImageView) convertView.findViewById(R.id.select_expert_list_item_headicon);
        ImageView recommend = (ImageView) convertView.findViewById(R.id.recommend);
        icon.setImageResource(R.drawable.default_head_female);

        mInstance.displayImage(doctorSimple.getICON_DOCTOR_PICTURE(), icon);

        tvName.setText(doctorSimple.getDOCTOR_REAL_NAME());
        tvTitle.setText(doctorSimple.getTITLE_NAME());
        tvHospital.setText(doctorSimple.getDOCTOR_HOSPITAL());
        tvSpecial.setText(doctorSimple.getDOCTOR_SPECIALLY());

        //判断是否是推荐医生
//		if (doctorSimple.getISRECOMMNED().equals("1")){
//			recommend.setVisibility(View.VISIBLE);
//		}else if (doctorSimple.getISRECOMMNED().equals("0")){
//			recommend.setVisibility(View.GONE);
//		}


        if (type == 1) {
            convertView.findViewById(R.id.select_expert_list_item_price_layout).setVisibility(View.GONE);
            imgFollow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    followListener.onClickSelect(doctorSimple);

                }
            });
        } else {
            if (doctorSimple.getNUMS() != null) {
                //if (doctorSimple.getNUMS().length()>0) {
                tvRemainNum.setTextColor(context.getResources().getColor(R.color.text_mid_color));
                tvRemainNum.setText("剩余" + doctorSimple.getNUMS() + "个服务名额");
                imgFollow.setBackgroundResource(R.drawable.icon_btn_bg_80);
                imgFollow.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followListener.onClickSelect(doctorSimple);
                    }
                });
            } else {
                imgFollow.setBackgroundResource(R.drawable.icon_bg_gray_72);
                imgFollow.setOnClickListener(null);
                tvRemainNum.setTextColor(context.getResources().getColor(R.color.red_text));
                tvRemainNum.setText("服务名额已满");
            }
            if (doctorSimple.getSERVICE_PRICE() != null) {
                tvPrice.setText(doctorSimple.getSERVICE_PRICE() + "元");
            } else {
                tvPrice.setVisibility(View.GONE);
            }

        }

        mCheckText.setVisibility(View.GONE);
        mCheckBox.setVisibility(View.GONE);
        String state = doctorSimple.getExpert_Invited_Stated();
        if (!DoctorHelper.isSelf(doctorSimple.getCUSTOMER_ID())) {
            if (ExpertStatus.INVITING_STATE.equals(state)) {
                mCheckText.setVisibility(View.VISIBLE);
                mCheckText.setText("邀请中");
            } else if (ExpertStatus.ACCEPT_STATE.equals(state)) {
                mCheckText.setVisibility(View.VISIBLE);
                mCheckText.setText("已邀请");
            } else {
                mCheckBox.setVisibility(View.VISIBLE);
            }
        }

        mCheckBox.setChecked(doctorSimple.is_Checked());
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    doctorSimple.setIs_Checked(isChecked);
                }
            }
        });


//		if (fromType==1)
//			imgFollow.setVisibility(View.GONE);
//		ImageView icon=(ImageView) convertView.findViewById(R.id.select_expert_list_item_headicon);
//		icon.setImageResource(R.drawable.default_head_female);
//
//		mInstance.displayImage(cus.ICON_DOCTOR_PICTURE, icon);
//
//		tvName.setText(cus.DOCTOR_REAL_NAME);
//		tvTitle.setText(cus.TITLE_NAME);
//		tvHospital.setText(cus.DOCTOR_HOSPITAL);
//		tvSpecial.setText(cus.DOCTOR_SPECIALLY);
//
//		if(type==1){
//			convertView.findViewById(R.id.select_expert_list_item_price_layout).setVisibility(View.GONE);
//			imgFollow.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					followListener.onClickSelect(cus);
//
//				}
//			});
//		}else {
//			if (cus.NUMS>0) {
//				tvRemainNum.setTextColor(context.getResources().getColor(R.color.service_color_text));
//				tvRemainNum.setText("剩余" + cus.NUMS + "个服务名额");
//				imgFollow.setBackgroundResource(R.drawable.icon_btn_bg_80);
//				imgFollow.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						followListener.onClickSelect(cus);
//
//					}
//				});
//			}else {
//				imgFollow.setBackgroundResource(R.drawable.icon_bg_gray_72);
//				imgFollow.setOnClickListener(null);
//				tvRemainNum.setTextColor(context.getResources().getColor(R.color.red_text));
//				tvRemainNum.setText("服务名额已满");
//			}
//			tvPrice.setText(cus.SERVICE_PRICE+"元");
//		}
        return convertView;
    }

    public void setSelectListener(OnClickSelectListener followListener) {
        this.followListener = followListener;
    }

    public interface OnClickSelectListener {
        void onClickSelect(DoctorSimpleBeanInvite dsb);
    }

}
