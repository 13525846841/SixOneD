package com.yksj.consultation.doctor;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.bean.DoctorServiceBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线会诊
 */
public class OnLineConsultActivity extends BaseTitleActivity {

    private View mainView;

    private SuperTextView mIsOpenStv;
    private SuperTextView mPriceStv;
    private SuperTextView mCountStv;
    private SuperTextView mFreeIsOpenStv;
    private SuperTextView mFreeStartTimeStv;
    private SuperTextView mFreeEndTimeStv;
    private SuperTextView mFreePriceStv;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_on_line_consult;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("在线会诊");
        initView();
    }

    private void initView() {
        setRight("确认", this::onConfrim);

        mIsOpenStv = findViewById(R.id.is_open_stv);
        mPriceStv = findViewById(R.id.price_stv);
        mCountStv = findViewById(R.id.count_stv);
        mIsOpenStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                mPriceStv.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mCountStv.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        mFreeIsOpenStv = findViewById(R.id.is_free_stc);
        mFreeStartTimeStv = findViewById(R.id.free_start_time_stv);
        mFreeEndTimeStv = findViewById(R.id.free_end_time_stv);
        mFreePriceStv = findViewById(R.id.free_price_stv);
        mFreeStartTimeStv.setOnClickListener(this::onStarDate);
        mFreeEndTimeStv.setOnClickListener(this::onEndDate);
        mFreeIsOpenStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                mFreeStartTimeStv.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mFreeEndTimeStv.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mFreePriceStv.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        mainView = getLayoutInflater().inflate(R.layout.activity_on_line_consult, null);
        initData();
    }

    /**
     * 开始时间
     * @param v
     */
    public void onStarDate(View v) {
        showDatePickDialog(mFreeStartTimeStv);
    }

    /**
     * 结束时间
     * @param v
     */
    public void onEndDate(View v) {
        showDatePickDialog(mFreeStartTimeStv);
    }

    /**
     * 确认
     * @param view
     */
    private void onConfrim(View view) {
        addConsultation();
    }

    /**
     * 加载数据，主要判断是否开通会诊
     */
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());
        map.put("op", "queryConsultationCenterInfo");
        ApiService.OKHttpIsConsultation(map, new ApiCallbackWrapper<ResponseBean<DoctorServiceBean>>(true) {
            @Override
            public void onResponse(ResponseBean<DoctorServiceBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    DoctorServiceBean bean = response.result;
                    mIsOpenStv.setSwitchIsChecked(bean.CONSULTATION_FLAG == 1);
                    mPriceStv.setRightEditString(String.valueOf(bean.SERVICE_PRICE));
                    mCountStv.setRightEditString(String.valueOf(bean.MAX_SERVICE));

                    if (bean.FREE_MEDICAL_FLAG == 1) {
                        mFreeIsOpenStv.setSwitchIsChecked(true);
                        String starDate = new SimpleDateFormat("yyyy年MM月dd日")
                                .format(parseDate(bean.FREE_MEDICAL_START_TIME).getTime());
                        mFreeStartTimeStv.setRightString(starDate);
                        String endDate = new SimpleDateFormat("yyyy年MM月dd日")
                                .format(parseDate(bean.FREE_MEDICAL_END_TIME).getTime());
                        mFreeEndTimeStv.setRightString(endDate);
                        mFreePriceStv.setRightEditString(String.valueOf(bean.FREE_MEDICAL_PRICE));
                    }
                }
            }
        }, this);
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
     * 添加会诊
     */
    private void addConsultation() {
        String price = mPriceStv.getRightEditString();
        String number = mCountStv.getRightEditString();
        String freePrice = mFreePriceStv.getRightEditString();
        String startDate = String.valueOf(parseDate(mFreeStartTimeStv.getRightString()).getTimeInMillis());
        String endDate = String.valueOf(parseDate(mFreeEndTimeStv.getRightString()).getTimeInMillis());

        if (mIsOpenStv.switchIsChecked()) {
            if (TextUtils.isEmpty(price)) {
                ToastUtils.showShort("请填写会诊价格");
                return;
            }

            if (TextUtils.isEmpty(number)) {
                ToastUtils.showShort("请填写会诊人数");
                return;
            }
        }

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

        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());
        map.put("service_price", price);
        map.put("max_service", number);
        map.put("consultation_flag", mIsOpenStv.switchIsChecked() ? "1" : "0");
        map.put("free_medical_flag", mFreeIsOpenStv.switchIsChecked() ? "1" : "0");
        map.put("free_medical_price", freePrice);
        map.put("free_medical_start", startDate);
        map.put("free_medical_end", endDate);

        ApiService.OKHttpConsultation(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (HttpResult.FAILED.equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }
}
