package com.yksj.consultation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.ListDetails;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrdersDetails;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

/**
 * Created by HEKl on 2015/9/18.
 * Used for 会诊列表的适配
 */
public class AdtConsultationOrders extends SimpleBaseAdapter<ListDetails> {
    private FragmentActivity maActivity;
    private int Type;
    private String positionId;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOption;//异步加载图片的操作

    public AdtConsultationOrders(Context context, int type, String position) {
        super(context);
        this.context = context;
        this.Type = type;
        this.positionId = position;
        maActivity = (FragmentActivity) context;
        mImageLoader = ImageLoader.getInstance();
        mOption = DefaultConfigurationFactory.createHeadDisplayImageOptions(maActivity);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.myorder_item_layout;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        String strTime = TimeUtil.format(datas.get(position).getSTATUS_TIME());//会诊单时间

        final ImageView mImageHead = holder.getView(R.id.image_head);
        final TextView textName = holder.getView(R.id.tv_ordername);//会诊名称
        final TextView textContent = holder.getView(R.id.tv_content);//会诊简介
        final int state = datas.get(position).getCONSULTATION_STATUS();//会诊状态码
        final String str = datas.get(position).getSERVICE_OPERATION();//会诊操作名称
        final TextView textRText = holder.getView(R.id.right_text);//会诊显示时间
        final int conId = datas.get(position).getCONSULTATION_ID();//会诊ID
        final Button btnState = holder.getView(R.id.btn_status);//会诊状态按钮
        final Button handle = holder.getView(R.id.btn_handle);//会诊操作按钮
        final ImageView imageDot = holder.getView(R.id.image_dot);//会诊红点
        imageDot.setVisibility(View.INVISIBLE);
        mImageLoader.displayImage(datas.get(position).getBIG_ICON_BACKGROUND(), mImageHead, mOption);
        textContent.setText(datas.get(position).getCONSULTATION_DESC());
        btnState.setText(datas.get(position).getSERVICE_STATUS_NAME());
        textName.setText(datas.get(position).getCONSULTATION_NAME());
        textRText.setText(strTime);

        if ("findPatByAssistant".equals(positionId)) {
            if (1 == datas.get(position).getNEW_CHANGE_DOCTOR()) {
                imageDot.setVisibility(View.VISIBLE);
            }
        } else if ("findPatByExpert".equals(positionId)) {
            if (1 == datas.get(position).getNEW_CHANGE_EXPERT()) {
                imageDot.setVisibility(View.VISIBLE);
            }
        }
        if (Type == 2) {
            btnState.setBackgroundResource(R.drawable.leftstate_gray);
        }
        handle.setVisibility(View.GONE);
        if (!"".equals(str)) {
            handle.setVisibility(View.VISIBLE);
        }
        handle.setText(datas.get(position).getSERVICE_OPERATION());
        //会诊操作监听
        handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("findPatByAssistant".equals(positionId)) {
                    sendRead(conId, state, "1");
                } else if ("findPatByExpert".equals(positionId)) {
                    sendRead(conId, state, "2");
                }
            }
        });
        //会诊列表项监听
        convertView.findViewById(R.id.rl_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("findPatByAssistant".equals(positionId)) {
                    sendRead(conId, state, "1");
                } else if ("findPatByExpert".equals(positionId)) {
                    sendRead(conId, state, "2");
                }
            }
        });
        return convertView;
    }

    private void putAcitvity(int conId) {
        Intent intent = new Intent(maActivity, AtyOrdersDetails.class);
        intent.putExtra("CONID", conId);
        maActivity.startActivity(intent);
    }

    private void putAcitvitySec(int conId) {
        Intent intent = new Intent(maActivity, AtyOrderDetails.class);
        intent.putExtra("CONID", conId);
        maActivity.startActivity(intent);
    }

    private void sendRead(final int consultId, final int status, final String flag) {
        ApiService.OKHttpSendRead(consultId + "", flag, new ApiCallbackWrapper<String>(maActivity) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if ("1".equals(object.optString("code")) && "1".equals(flag)) {
                            switch (status) {
                                case 10://待接单
                                case 15://已为患者发起会诊，等待患者确认个人信息
                                    putAcitvity(consultId);
                                    break;
                                case 20://填病历
                                case 30://发送给专家
                                case 50://待接诊
                                case 55:
                                case 60://重选专家
                                case 70://待支付
                                case 80://待意见
                                case 85:
                                case 88:
                                case 90://取消
                                case 95://患者取消
                                case 99://已完成
                                case 222://待退款，未填账号
                                case 232://待退款，已填账号
                                case 242://退款完成
                                case 252://退款失败
                                    putAcitvitySec(consultId);
                                    break;
                            }
                        } else if ("1".equals(object.optString("code")) && "2".equals(flag)) {
                            switch (status) {
                                case 50://待接诊
                                case 55:
                                case 70://待支付
                                case 80://给意见
                                case 85:
                                case 88:
                                case 90://取消
                                case 95://患者取消
                                case 99://已完成
                                case 222://待退款，未填账号
                                case 232://待退款，已填账号
                                case 242://退款完成
                                case 252://退款失败
                                    putAcitvitySec(consultId);
                                    break;
                            }
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, this);
    }
}

