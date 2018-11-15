package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.outpatient.AtyCancelReasons;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;


/**
 * Created by HEKL on 2015/9/18.
 * Used for 查看预约详情
 */
public class AtyOutPatientDetail extends BaseActivity implements View.OnClickListener {
    private ImageView imageHead;//头像
    private TextView textTip;//温馨提示
    private TextView textName;//患者姓名
    private TextView textPhone;//患者手机
    private TextView textTime;//时间
    private TextView textAddress;//地址
    private TextView textPrice;//价格
    private TextView mState;//状态
    private TextView mrefund;//退款显示
    private TextView textRemark;//备注
    private boolean Expanded = false;//true展开,false隐藏
    private ImageView mImageMore;//查看更多

    private String cancelMan;
    private String cancelTime;
    private String cancelReason;

    private JSONObject object;
    private Button mCancel;
    private int state, docId;
    private String orderId;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int cusId;//患者id

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_outpatient_detail);
        if (null != arg0) {
            orderId = arg0.getString("orderId");
        } else {
            orderId = getIntent().getStringExtra("ORIDERID");
        }
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("orderId", orderId);
    }

    private void initView() {
        initializeTitle();
        imageHead = (ImageView) findViewById(R.id.image_patienthead);
        textName = (TextView) findViewById(R.id.tv_patientname);
        textPhone = (TextView) findViewById(R.id.tv_phone);
        textRemark = (TextView) findViewById(R.id.tv_remarks);
        mImageMore = (ImageView) findViewById(R.id.image_more);
        mrefund = (TextView) findViewById(R.id.tv_refund);


        textAddress = (TextView) findViewById(R.id.tv_address);
        textPrice = (TextView) findViewById(R.id.tv_price);
        textTime = (TextView) findViewById(R.id.tv_time);
        mCancel = (Button) findViewById(R.id.btn_cancel);
        mState = (TextView) findViewById(R.id.tv_state);
        textTip = (TextView) findViewById(R.id.tv_warn);
        mImageLoader = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createHeadDisplayImageOptions(this);
        titleTextV.setText("查看详情");
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.rl_refund).setOnClickListener(this);
        findViewById(R.id.btn_gopay).setOnClickListener(this);
        findViewById(R.id.rl_patientinfo).setOnClickListener(this);
        titleLeftBtn.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()) {
            case R.id.title_back://回退
                onBackPressed();
                break;
            case R.id.rl_patientinfo://患者个人信息查询
                i = new Intent(AtyOutPatientDetail.this, AtyPatientMassage.class);
                i.putExtra("PID", cusId + "");
                i.putExtra("ORDER", 0);
                startActivity(i);
                break;
            case R.id.btn_cancel://取消预约
                i = new Intent(this, AtyCancelOutPatient.class);
                i.putExtra("orderId", orderId);
                i.putExtra("docId", docId);
                startActivity(i);
                break;
            case R.id.rl_refund://查看取消原因/退款
                switch (state) {
                    case 175:
                    case 180:
                        i = new Intent(this, AtyCancelReasons.class);
                        i.putExtra("MAN", cancelMan);
                        i.putExtra("TIME", cancelTime);
                        i.putExtra("REASONS", cancelReason);
                        startActivity(i);
                        break;
                }
                break;
            case R.id.image_more://更多
                if (Expanded) {
                    Expanded = false;
                    textRemark.setMaxLines(2);
                    mImageMore.setImageResource(R.drawable.gengduos);
                } else {
                    Expanded = true;
                    textRemark.setMaxLines(100);
                    mImageMore.setImageResource(R.drawable.shouqis);
                }
                break;
        }
    }

    private void initData() {
        RequestParams params = new RequestParams();
        params.put("ORDER_ID", orderId);
        params.put("Type", "queryOrderMessage");
        ApiService.doHttpSERVICESETSERVLETRJ420(params, new AsyncHttpResponseHandler(this) {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                if (!TextUtils.isEmpty(content)) {
                    try {
                        object = new JSONObject(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (object.optInt("code") == 1) {
                        JSONObject obj = null;
                        try {
                            obj = object.getJSONObject("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        state = obj.optInt("SERVICE_STATUS");
                        docId = obj.optInt("SERVICE_CUSTOMER_ID");
                        textTime.setText(TimeUtil.getDemo(obj.optString("SERVICE_START"), obj.optString("SERVICE_END")));
                        textAddress.setText(obj.optString("SERVICE_PLACE"));
                        textPrice.setText(obj.optString("SERVICE_GOLD"));
                        textName.setText(obj.optString("PATIENT_NAME"));
                        textPhone.setText(obj.optString("PATIENT_PHONE"));
                        textRemark.setText(obj.optString("ADVICE_CONTENT"));
                        cusId = obj.optInt("ENJOY_CUSTOMER_ID");
                        cancelTime = obj.optString("CANCEL_TIME");
                        cancelReason = obj.optString("CANCEL_REASON");
                        if (!"".equals(obj.optString("ADVICE_CONTENT"))&&(!"null".equals(obj.optString("ADVICE_CONTENT")))) {
                            findViewById(R.id.rl_remark).setVisibility(View.VISIBLE);
                        }
                        if (state == 175) {
                            cancelMan = obj.optString("PATIENT_NAME");
                        } else if (state == 180) {
                            try {
                                cancelMan = obj.getJSONObject("DOCTOR").optString("NAME");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //判断是否展开
                        if (textRemark.getText().length() < 50) {
                            findViewById(R.id.image_more).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.image_more).setOnClickListener(AtyOutPatientDetail.this);
                        }
                        mImageLoader.displayImage(obj.optString("PATIENTICON"), imageHead, mOptions);
                        findViewById(R.id.ll_tip).setVisibility(View.VISIBLE);
                        textTip.setText(obj.optString("DMESSAGE"));
//                            textDocHosipital.setText(obj.getJSONObject("DOCTOR").optString("HOSPITAL"));
                        findViewById(R.id.rl_gopay).setVisibility(View.GONE);
                        findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        switch (state) {
                            case 50:
                                if (TimeUtil.formatMillion(obj.optString("SERVICE_START")) <= TimeUtil.formatMillion(obj.optString("SYSTEMTIME"))) {
                                    mState.setText("服务中");
                                } else if ((TimeUtil.formatMillion(obj.optString("SERVICE_START")) > TimeUtil.formatMillion(obj.optString("SYSTEMTIME"))) && (TimeUtil.formatMillion(obj.optString("SERVICE_START")) < TimeUtil.formatMillion(obj.optString("SYSTEMTIME")) + 24 * 60 * 60 * 1000)) {
                                    mState.setText("待服务");
//                                        textTip.setText("温馨提示: " + "当前时间距预约服务开始时间已不足24个小时,不能取消预约服务");
                                } else {
                                    mState.setText("待服务");
                                    findViewById(R.id.rl_gopay).setVisibility(View.VISIBLE);
                                    findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
                                }
                                break;
                            case 175:
                            case 180:
                                findViewById(R.id.rl_refund).setVisibility(View.VISIBLE);
                                mrefund.setText("取消原因");
                                mState.setText("已取消");
                                break;
                            case 198:
                                mState.setText("已终止");
                                break;
                            case 232:
                                mState.setText("待退款");
                                break;
                            case 199:
                                findViewById(R.id.rl_gopay).setVisibility(View.GONE);
                                findViewById(R.id.ll_tip).setVisibility(View.GONE);
                                mState.setText("已完成");
                                break;
                        }
                    } else {
                        ToastUtil.showShort(object.optString("message"));
                    }

                }

            }
        });
    }
}
