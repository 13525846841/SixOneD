package com.yksj.consultation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LevelListDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.consultation.DAtyConsultDetails;
import com.yksj.consultation.sonDoc.consultation.DAtyConsultSuggestion;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

/**
 * @author HEKL
 *         <p/>
 *         会诊管理 adapter
 */
public class AdtConsultationManager extends SimpleBaseAdapter<JSONObject> implements SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener {

    private LevelListDrawable drawable;// 分割线
    private FragmentActivity maActivity;
    private DisplayImageOptions mOptions;
    private PopupWindow messageChat;
    private ImageLoader instance;
    private JSONObject obj;
    private Button btnStatus;
    private String consultPosition;
    private int type;// 判断列表类型

    public AdtConsultationManager(Context context, String consultPosition, int type) {
        super(context);
        this.consultPosition = consultPosition;
        this.context = context;
        this.type = type;
        maActivity = (FragmentActivity) context;
        instance = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createSeniorDoctorDisplayImageOptions(maActivity);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getItemResource() {
        return R.layout.consultation_manager_item_layout;
    }

    @Override
    public View getItemView(final int position, final View convertView,
                            com.yksj.consultation.adapter.SimpleBaseAdapter.ViewHolder holder) {
        final JSONObject entity = (JSONObject) getItem(position);
        final int status = entity.optInt("CONSULTATION_STATUS");
        String strTime = null;
        final CustomerInfoEntity chatEntity = new CustomerInfoEntity();
        final ImageView chatting = (ImageView) holder.getView(R.id.iv_talkbg);
        TextView patientName = (TextView) holder.getView(R.id.tv_consultCenter);
        TextView applyTime = (TextView) holder.getView(R.id.tv_applyTime);
        btnStatus = (Button) holder.getView(R.id.btn_status);
        drawable = (LevelListDrawable) btnStatus.getBackground();
        TextView right_view = (TextView) holder.getView(R.id.right_view);
        TextView getTime = (TextView) holder.getView(R.id.tv_receive);
        TextView consultName = (TextView) holder.getView(R.id.tv_patientName);
        // 会诊名称
        patientName.setText(entity.optString("CONSULTATION_NAME"));
        // 查看操作
        right_view.setText(entity.optString("SERVICE_OPERATION"));
        // 状态
        btnStatus.setText(entity.optString("SERVICE_STATUS_NAME"));
        // 前标签颜色
        if (judgeType(type) == 0) {// 会诊医生
            consultName.setText("会诊专家:" + entity.optString("CUSTOMER_NICKNAME"));
            getTime.setText("申请时间:");
            strTime = TimeUtil.format(entity.optString("CREATE_TIME"));
        } else if (judgeType(type) == 1) {// 专家
            consultName.setText("会诊医生:" + entity.optString("CUSTOMER_NICKNAME"));
            getTime.setText("接诊时间:");
            strTime = TimeUtil.format(entity.optString("SUBMIT_TIME"));
        }
        applyTime.setText(strTime);
        chatting.setOnClickListener(new OnClickListener() {// 会诊群聊
            @Override
            public void onClick(View v) {
                if ("findPatByAssistant".equals(consultPosition) && 10 == status) {
                    ToastUtil.showShort("您还未接诊，暂不能对话");
                } else if ("findPatByExpert".equals(consultPosition) && 50 == status) {
                    ToastUtil.showShort("您还未同意参加会诊，暂不能对话");
                } else if (("findPatByAssistant".equals(consultPosition)
                        && (20 == status || 25 == status || 30 == status || 40 == status|| 50 == status))) {
                    chatEntity.setName(datas.get(position).optString("PATIENT_NAME"));
                    chatEntity.setId(datas.get(position).optString("PATIENT_ID"));
                    FriendHttpUtil.chatFromPerson(maActivity, chatEntity);
                } else {
                    doChatGroup(entity.optString("CONSULTATION_ID"), "0", entity.optString("CONSULTATION_NAME"));
                }
            }
        });
        if (99 == status && "0".equals(datas.get(position).optString("ISTALK"))) {
            holder.getView(R.id.iv_talk).setVisibility(View.GONE);
            chatting.setVisibility(View.GONE);
        } else if (99 == status && "1".equals(datas.get(position).optString("ISTALK"))) {
            holder.getView(R.id.iv_talk).setVisibility(View.VISIBLE);
            chatting.setVisibility(View.VISIBLE);
        } else if (90 == status) {
            holder.getView(R.id.iv_talk).setVisibility(View.GONE);
            chatting.setVisibility(View.GONE);
        } else if (10 == status && "findPatByAssistant".equals(consultPosition)) {// 会诊医生待接诊
            drawable.setLevel(2);// 绿色
        } else if ((20 == status || 25 == status || 30 == status || 40 == status)
                && "findPatByAssistant".equals(consultPosition)) {// 20-基层医生接单、未选择病历，25-基层医生已选择病历，患者未填写，30-已填写病历，提交到基层医生，基层医生可修改--40-已填写病历，发回患者修改
            drawable.setLevel(7);// 紫色
        } else if (50 == status && "findPatByAssistant".equals(consultPosition)) {// 会诊医生待同意
            drawable.setLevel(4);// 蓝色
        } else if (50 == status && "findPatByExpert".equals(consultPosition)) {// 专家待同意
            drawable.setLevel(3);// 黄色
        } else if (70 == status) {// 待付款
            drawable.setLevel(1);// 粉色
        } else if ((80 == status || 88 == status) && "findPatByAssistant".equals(consultPosition)) {// 会诊医生待服务
            drawable.setLevel(3);// 黄色
        } else if ((80 == status || 88 == status) && "findPatByExpert".equals(consultPosition)) {// 专家待服务
            drawable.setLevel(2);// 绿色
        } else if (90 == status || 99 == status) {// 已完成和已取消
            drawable.setLevel(6);// 灰色
        } else {
            drawable.setLevel(6);
        }
        convertView.findViewById(R.id.fl_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                if ("findPatByAssistant".equals(consultPosition) && 10 == status) {// 会诊医生待接诊
                    i.putExtra("HEKL", 11);
                    putExtraDetails(i);
                } else if ("findPatByAssistant".equals(consultPosition)
                        && (20 == status || 25 == status || 30 == status || 40 == status)) {// 会诊医生填病历
                    i.putExtra("HEKL", 12);
                    putExtraDetails(i);
                } else if ("findPatByAssistant".equals(consultPosition) && 50 == status) {// 会诊医生待同意
                    i.putExtra("HEKL", 13);
                    putExtraDetails(i);
                } else if ("findPatByAssistant".equals(consultPosition) && 70 == status) {// 会诊医生待付款
                    i.putExtra("HEKL", 14);
                    putExtraDetails(i);
                } else if ("findPatByAssistant".equals(consultPosition) && (80 == status || 88 == status)) {// 会诊医生待服务
                    i.putExtra("HEKL", 15);
                    putExtraSuggestion(i);
                } else if ("findPatByExpert".equals(consultPosition) && 50 == status) {// 专家待同意
                    i.putExtra("HEKL", 0);
                    putExtraDetails(i);
                } else if ("findPatByExpert".equals(consultPosition) && 70 == status) {// 专家待付款
                    i.putExtra("HEKL", 1);
                    putExtraDetails(i);
                } else if ("findPatByExpert".equals(consultPosition) && (80 == status || 88 == status)) {// 专家给意见
                    i.putExtra("HEKL", 2);
                    putExtraSuggestion(i);
                } else if (99 == status) {// 专家和会诊医生已完成
                    i.putExtra("HEKL", 3);
                    putExtraSuggestion(i);
                } else if (90 == status) {// 专家已取消
                    i.putExtra("HEKL", 4);
                    putExtraDetails(i);
                }
            }

            public void putExtraDetails(Intent intent) {
                intent.setClass(maActivity, DAtyConsultDetails.class);
                int str = entity.optInt("CONSULTATION_ID");
                intent.putExtra("CONID", str);
                maActivity.startActivity(intent);
            }

            public void putExtraSuggestion(Intent intent) {
                intent.setClass(maActivity, DAtyConsultSuggestion.class);
                int str = entity.optInt("CONSULTATION_ID");
                intent.putExtra("CONID", str);
                maActivity.startActivity(intent);
            }
        });
        return convertView;

    }

    /**
     * 列表类型判断
     */
    private int judgeType(int type) {
        int one = 0;
        if (type == 0 || type == 1 || type == 2 || type == 3) {
            one = 1;
        } else if (type == 10 || type == 11 || type == 12 || type == 13 || type == 14 || type == 15) {
            one = 0;
        }
        return one;
    }

    /**
     * chat
     *
     * @param id   groupid
     * @param isBl ;//是否是病历：（1是病历，0是会诊）
     * @param name groupname
     */
    public void doChatGroup(String id, String isBl, String name) {
        GroupInfoEntity entity = new GroupInfoEntity();
        entity.setId(id);
        entity.setIsBL(isBl);
        entity.setName(name);
        SalonHttpUtil.onItemClick(context, AdtConsultationManager.this, maActivity.getSupportFragmentManager(), entity, false);
    }


    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
        if ("0".equals(state)) {
        } else if ("-1".equals(state)) {
            ToastUtil.showShort("服务器出错");
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
            intent1.setClass(context, ChatActivity.class);
            context.startActivity(intent1);
        }
    }
}
