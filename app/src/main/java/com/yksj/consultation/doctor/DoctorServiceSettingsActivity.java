package com.yksj.consultation.doctor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.bean.DoctorServiceBean;
import com.yksj.consultation.bean.ResponseDoctorServiceBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResultZero;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 服务设置界面
 */
public class DoctorServiceSettingsActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener {

    public final static String TITLE = "TITLE";

    public final static String SERVICE_TYPE_ID = "SERVICE_TYPE_ID";//5图文 6电话 7包月 8视频

    @ServiceType.Type
    public String mServiceType;//服务ID

    private TextView mText;
    private LinearLayout mYiZhenLay;
    private View mainView;
    private SuperTextView mIsOpenStv;
    private SuperTextView mPriceStv;
    private SuperTextView mFreeIsOpenStv;
    private SuperTextView mFreeStartDateStv;
    private SuperTextView mFreeEndDateStv;
    private SuperTextView mFreePriceStv;

    public static Intent getCallingIntent(Context context, @ServiceType.Type String type) {
        Intent intent = new Intent(context, DoctorServiceSettingsActivity.class);
        intent.putExtra(Constant.Station.SERVICE_TYPE_ID, type);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_consult;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mServiceType = getIntent().getStringExtra(Constant.Station.SERVICE_TYPE_ID);
        setTitle(ServiceType.toString(mServiceType));
        setRight("确定", this::onConfrim);
        initView();
        requestData();
    }

    private void initView() {
        mText = findViewById(R.id.text);
        if (ServiceType.TW.equals(mServiceType)) {
            mText.setText("注意\n开通图文咨询，需注意如下内容：\n1、请在患者购买服务的24小时内为患者服务\n2、每次服务，患者最多提问20条\n3、24小时内，未服务，患者可申请退款\n4、如有纠纷，交由平台仲裁");
        }
        if (ServiceType.BY.equals(mServiceType)) {
            mText.setText("注意\n开通包月咨询，需注意如下内容：\n1、请在患者发送信息的24小时内为患者服务\n2、每月服务，患者最多提问1000条\n3、患者发送信息的24小时内未回复，患者可申请退款\n" +
                    "4、如有纠纷，交由平台仲裁");
        }

        mPriceStv = findViewById(R.id.price_stv);

        mIsOpenStv = findViewById(R.id.is_open_stv);
        mIsOpenStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {
                    mPriceStv.setVisibility(View.VISIBLE);
                } else {
                    mPriceStv.setVisibility(View.GONE);
                }
            }
        });

        mFreeIsOpenStv = findViewById(R.id.is_yizhen_stc);
        mFreeIsOpenStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {
                    mYiZhenLay.setVisibility(View.VISIBLE);
                } else {
                    mYiZhenLay.setVisibility(View.GONE);
                }
            }
        });

        mFreeStartDateStv = findViewById(R.id.start_time_stv);
        mFreeStartDateStv.setOnClickListener(this::onStartTime);

        mFreeEndDateStv = findViewById(R.id.end_time_stv);
        mFreeEndDateStv.setOnClickListener(this::onEndTime);

        mFreePriceStv = findViewById(R.id.yizhen_price_stv);

        mYiZhenLay = findViewById(R.id.ll_addyizhen);

        mainView = getLayoutInflater().inflate(R.layout.activity_consult, null);
    }

    /**
     * 义诊开始时间
     * @param view
     */
    private void onStartTime(View view) {
        showDatePickDialog(mFreeStartDateStv);
    }

    /**
     * 义诊结束时间
     * @param v
     */
    private void onEndTime(View v) {
        showDatePickDialog(mFreeEndDateStv);
    }

    /**
     * 显示时间选择框
     * @param stv
     */
    private void showDatePickDialog(SuperTextView stv) {
        Calendar calendar = parseDate(stv.getRightString());
        DatePickerDialog pickerDialog = new DatePickerDialog(this, R.style.DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar temp = Calendar.getInstance();
                temp.set(year, month, dayOfMonth);
                stv.setRightString(new SimpleDateFormat("yyyy年MM月dd日").format(temp.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    /**
     * 解析时间
     * @param str
     * @return
     */
    private Calendar parseDate(String str) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy年MM月dd日").parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return calendar;
        }
        return calendar;
    }

    /**
     * 加载服务
     */
    private void requestData() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "findServiceSetting");
        map.put("doctor_id", DoctorHelper.getId());

        ApiService.OKHttpFindServiceSetting(map, new ApiCallbackWrapper<ResponseDoctorServiceBean<List<DoctorServiceBean>>>() {
            @Override
            public void onResponse(ResponseDoctorServiceBean<List<DoctorServiceBean>> response) {
                super.onResponse(response);
                if (response.isSucees()) {
                    List<DoctorServiceBean> services = response.service;
                    for (DoctorServiceBean bean : services) {
                        if (bean.SERVICE_TYPE_ID.equals(mServiceType)) {
                            mIsOpenStv.setSwitchIsChecked(bean.ORDER_ON_OFF == 1);
                            mPriceStv.setRightEditString(String.valueOf(bean.SERVICE_PRICE));
                            if (bean.FREE_MEDICAL_FLAG == 1) {
                                mFreeIsOpenStv.setSwitchIsChecked(true);
                                mFreePriceStv.setRightEditString(String.valueOf(bean.FREE_MEDICAL_PRICE));
                                String starDate = new SimpleDateFormat("yyyy年MM月dd日")
                                        .format(parseDate(bean.FREE_MEDICAL_START_TIME).getTime());
                                mFreeStartDateStv.setRightString(starDate);
                                String endDate = new SimpleDateFormat("yyyy年MM月dd日")
                                        .format(parseDate(bean.FREE_MEDICAL_END_TIME).getTime());
                                mFreeEndDateStv.setRightString(endDate);
                                mFreePriceStv.setRightEditString(String.valueOf(bean.FREE_MEDICAL_PRICE));
                            }
                        }
                    }
                }
            }
        }, this);
    }

    /**
     * 是否开通
     */
    private void onConfrim(View v) {
        String freePrice = mFreePriceStv.getRightEditString();
        String startDate = String.valueOf(parseDate(mFreeStartDateStv.getRightString()).getTimeInMillis());
        String endDate = String.valueOf(parseDate(mFreeStartDateStv.getRightString()).getTimeInMillis());
        if (mFreeIsOpenStv.switchIsChecked()) {
            if (TextUtils.isEmpty(freePrice)) {
                ToastUtils.showShort("请填写义诊价格");
                return;
            }

            if (TextUtils.isEmpty(startDate)) {
                ToastUtils.showShort("请填写开始时间");
                return;
            }
            if (TextUtils.isEmpty(endDate)) {
                ToastUtils.showShort("请填写结束时间");
                return;
            }
        }

        String servicePrice = mPriceStv.getRightEditString();
        if (mIsOpenStv.switchIsChecked()) {
            if (TextUtils.isEmpty(servicePrice)) {
                ToastUtils.showShort("请填写咨询价格");
                return;
            }
        }
        String serviceToggle = mIsOpenStv.switchIsChecked() ? "1" : "0";
        String freeToggle = mFreeIsOpenStv.switchIsChecked() ? "1" : "0";
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());
        map.put("order_on_off", serviceToggle);
        map.put("service_Price", servicePrice);
        map.put("service_type_id", mServiceType);
        map.put("free_medical_flag", freeToggle);
        map.put("free_medical_price", freePrice);
        map.put("free_medical_start_time", startDate);
        map.put("free_medical_end_time", endDate);

        ApiService.OKHttpOpenDoctorService(map, new ApiCallbackWrapper<String>(true) {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("修改失败");
            }

            @Override
            public void onResponse(String response) {
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        ToastUtil.showShort(jsonObject.optString("message"));
                        if (HttpResultZero.SUCCESS.equals(jsonObject.optString("code"))) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mPriceStv.setVisibility(View.VISIBLE);
        } else {
            mPriceStv.setVisibility(View.GONE);
        }
    }
}
