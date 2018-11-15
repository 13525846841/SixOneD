package com.yksj.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.doctor.MyOrdersMenuActivity;
import com.yksj.healthtalk.net.http.ApiConnection;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HEKL on 2015/9/18.
 * Used for 我的订单_
 */
public class MainOrdersFragment extends BaseFragment implements View.OnClickListener {
    private TextView mOrderName;//订单名称
    private TextView mNoOrders;//没有订单
    private TextView mOrderState;//订单状态
    private ImageView imageDot;//消息红点提示
    private String type;//请求类型
    JSONObject object;
    JSONObject obj;
    SpannableStringBuilder ss;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fgt_mainorders, null);
        EventBus.getDefault().register(MainOrdersFragment.this);
        view.setOnClickListener(this);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mOrderName = (TextView) view.findViewById(R.id.order_message);
        mOrderState = (TextView) view.findViewById(R.id.order_state);
        mNoOrders = (TextView) view.findViewById(R.id.no_order);
        imageDot = (ImageView) view.findViewById(R.id.dot);
        ss = new SpannableStringBuilder();
//        if (LoginBusiness.getInstance().getLoginEntity() != null) {
//            if (!"0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition())) {
//                type = "homePageInfoExpert";
//            } else {
//                type = "homePageInfoAssi";
//            }
//            if (!MainOrdersFragment.this.isDetached()) {
//                showOrder(type);
//            }
//        }
        mNoOrders.setVisibility(View.VISIBLE);
//        mNoOrders.setText("您还没有会诊");
        mNoOrders.setText("我的订单");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiConnection.cancelTag(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), MyOrdersMenuActivity.class));
    }

    /**
     * 加载提示
     */
    private void sendTip(String str) {
        mOrderName.setVisibility(View.GONE);
        mOrderState.setVisibility(View.GONE);
        mNoOrders.setVisibility(View.VISIBLE);
        mNoOrders.setText(str);
        imageDot.setImageDrawable(getResources().getDrawable(R.drawable.gray_dot));
    }

    private void refresh(final String str) {
        mOrderName.setVisibility(View.VISIBLE);
        mOrderState.setVisibility(View.VISIBLE);
        mNoOrders.setVisibility(View.GONE);
        object = null;
        ss.clear();
        try {
            object = new JSONObject(str);
            mOrderName.setText(object.optString("CONSULTATION_NAME"));
            mOrderState.setText(ss.append("(" + object.optString("SERVICE_STATUS_NAME") + ")"));
            imageDot.setImageDrawable(getResources().getDrawable(R.drawable.red_dot));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
