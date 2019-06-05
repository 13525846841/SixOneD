package com.yksj.consultation.sonDoc.home;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Request;

/**
 * 添加提醒界面
 */
public class AddNoteActivity extends BaseTitleActivity {

    private SuperTextView rl_select_data;
    private SuperTextView rl_select_time;
    private SuperTextView rl_write_text;//输入提醒内容
    private Calendar mNowDate;
    private String mWarnContent;
    private String mWarnDate;
    private String date1;
    private String time;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_add_note;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("添加提醒");
        setRight("完成", this::onSubmitNote);
        mNowDate = getWeeOfToday();
        initView();
    }

    private void initView() {
        rl_select_data = findViewById(R.id.rl_select_data);
        rl_select_time = findViewById(R.id.rl_select_time);
        rl_write_text = findViewById(R.id.rl_write_text);
        rl_select_data.setOnClickListener(this);
        rl_select_time.setOnClickListener(this);
        rl_write_text.setOnClickListener(this);
        date1 = TimeUtils.millis2String(mNowDate.getTimeInMillis(), new SimpleDateFormat("yyyyMMdd"));
        rl_select_data.setRightString(TimeUtils.millis2String(mNowDate.getTimeInMillis(), new SimpleDateFormat("yyyy年MM月dd日")));
        time = TimeUtils.millis2String(mNowDate.getTimeInMillis(), new SimpleDateFormat("HHmm"));
        rl_select_time.setRightString(TimeUtils.millis2String(mNowDate.getTimeInMillis(), new SimpleDateFormat("HH:mm")));
    }

    /**
     * 获取当天凌晨时间
     * @return
     */
    private Calendar getWeeOfToday() {
        Calendar nowDate = Calendar.getInstance();
        nowDate.set(Calendar.HOUR_OF_DAY, 0);
        nowDate.set(Calendar.SECOND, 0);
        nowDate.set(Calendar.MINUTE, 0);
        nowDate.set(Calendar.MILLISECOND, 0);
        return nowDate;
    }

    /**
     * 完成编辑
     * @param view
     */
    private void onSubmitNote(View view) {
        addPlan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_data:
                showDatePicker();
                break;
            case R.id.rl_select_time:
                showTimePicker();
                break;
            case R.id.rl_write_text://输入提醒内容
                inputAddText();
                break;
        }
    }

    private void inputAddText() {
        AddTextActivity.from(this)
                .setTitle("提醒内容")
                .setListener((v1, content, activity) -> {
                    mWarnContent = content;
                    rl_write_text.setRightString(content);
                    activity.finish();
                }).startActivity();
    }

    /**
     * 显示时间选择
     */
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DatePickerDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                temp.set(Calendar.MINUTE, minute);
                if (temp.getTimeInMillis() >= mNowDate.getTimeInMillis()) {
                    String date = TimeUtils.millis2String(temp.getTimeInMillis(), new SimpleDateFormat("HH:mm"));
                  time= TimeUtils.millis2String(temp.getTimeInMillis(), new SimpleDateFormat("HHmm"));
                    rl_select_time.setRightString(date);
                    temp = null;
                }
            }
        }, mNowDate.get(Calendar.HOUR), mNowDate.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * 显示日期选择
     */
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar temp = Calendar.getInstance();
                temp.set(year, month, dayOfMonth, 0, 0, 0);
                if (temp.getTimeInMillis() >= mNowDate.getTimeInMillis()) {
                    String date = TimeUtils.millis2String(temp.getTimeInMillis(), new SimpleDateFormat("yyyy年MM月dd日"));
                    date1 = TimeUtils.millis2String(temp.getTimeInMillis(), new SimpleDateFormat("yyyyMMdd"));
                    rl_select_data.setRightString(date);
                    temp = null;
                }
            }
        }, mNowDate.get(Calendar.YEAR), mNowDate.get(Calendar.MONTH), mNowDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * 添加计划
     */
    private void addPlan() {
        if (TextUtils.isEmpty(mWarnContent)) {
            ToastUtil.showToastPanl("请填写提醒内容");
            return;
        }
        mWarnDate = date1+ time;
        ApiService.notAddPlan(mWarnDate, mWarnContent, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    finish();
                }
                ToastUtils.showShort(response.message);
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Log.e("2222222", "onError: "+e.toString() );
            }
        });
    }
}
