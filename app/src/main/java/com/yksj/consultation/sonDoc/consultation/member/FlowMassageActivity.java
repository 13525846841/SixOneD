package com.yksj.consultation.sonDoc.consultation.member;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import okhttp3.Request;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.DoctorSimpleBean;
import com.yksj.healthtalk.db.DictionaryHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 填写信息（提交申请）
 * Created by zheng on 2015/9/15.
 */
public class FlowMassageActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private LinearLayout selectedExLL;//所选专家
    private TextView selectedName,selectedDuty,selectedHospital,selectedSpeciality,CURRENTVIEW,suozaidi,pSexEdit,pAgeEdit;
    private ImageView selectedHeader;
    private EditText pNameEdit,pPhoneEdit,pCodeEdit;
    private EditText pIllness;
    private Button addDoctor,codeBtn;
    private Runnable runnable;
    private boolean Sendcode = false,isApplying=false;//验证码是否发送 true为发送
    Handler handler = new Handler();
    private View lineView;
    private ImageLoader mInstance;
    private DisplayImageOptions mOptions;
    private View wheelView;
    private PopupWindow pop;
    private List<Map<String, String>> proList = null;
    private Map<String, List<Map<String, String>>> cityMap = new LinkedHashMap<String, List<Map<String, String>>>();
    private View mainView;
    private String locationCode = "";//所在地编码

    private JSONObject lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_person_message);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("填写信息");
        titleLeftBtn.setOnClickListener(this);
//      mInstance = GlideHelper.getInstance();
        findViewById(R.id.tijiao_btn).setOnClickListener(this);
        selectedExLL = ((LinearLayout) findViewById(R.id.seleted_person_layout));
        lineView=findViewById(R.id.line_doc);
        selectedHeader = ((ImageView) findViewById(R.id.seleted_person));
        selectedName = ((TextView) findViewById(R.id.seleted_person_name));
        selectedDuty = ((TextView) findViewById(R.id.seleted_person_duty));
        selectedHospital = ((TextView) findViewById(R.id.seleted_person_hospital));
        selectedSpeciality = ((TextView) findViewById(R.id.seleted_person_speciality));
        pNameEdit = ((EditText) findViewById(R.id.full_name_edit));
        pSexEdit = ((TextView) findViewById(R.id.full_sex_edit));
        pAgeEdit = ((TextView) findViewById(R.id.full_age_edit));
        pPhoneEdit = ((EditText) findViewById(R.id.full_phone_edit));
        pCodeEdit = ((EditText) findViewById(R.id.full_code_edit));
        pIllness = ((EditText) findViewById(R.id.illness_state));
        addDoctor = ((Button) findViewById(R.id.add_doctor));
        codeBtn = ((Button) findViewById(R.id.btn_code));
        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        pop = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainView= getLayoutInflater().inflate(R.layout.full_person_message, null);
        findViewById(R.id.location_action1).setOnClickListener(this);
        suozaidi = (TextView) findViewById(R.id.location1);

        initLast();
        queryData();
        selectedDoc();

        codeBtn.setOnClickListener(this);
        pPhoneEdit.setOnTouchListener(this);

    }

    private void selectedDoc() {
        if(getIntent().hasExtra("data")){
            if(getIntent()!=null){
                selectedExLL.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
                mInstance = ImageLoader.getInstance();
                mOptions= DefaultConfigurationFactory.createSeniorDoctorDisplayImageOptions(FlowMassageActivity.this);
                DoctorSimpleBean dsb = (DoctorSimpleBean)getIntent().getSerializableExtra("data");
                mInstance.displayImage(dsb.ICON_DOCTOR_PICTURE,selectedHeader,mOptions);
                selectedName.setText(dsb.DOCTOR_REAL_NAME);
                selectedDuty.setText(dsb.TITLE_NAME);
                selectedHospital.setText(dsb.UNIT_NAME);
                selectedSpeciality.setText(dsb.DOCTOR_SPECIALLY);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.tijiao_btn:
                sendData();
                break;
            case R.id.btn_code:
                getAuthCode();
                break;
            case R.id.wheel_cancel:
                if (pop != null)
                    pop.dismiss();
                break;

            case R.id.wheel_sure:
                if (pop != null)
                    pop.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    setText();
                }
                break;
            case R.id.location_action1://位置选择
                CURRENTVIEW = suozaidi;
                setCity();
                break;
        }
    }

    //发送数据
    private void sendData() {
        String content = "申请成功，会诊医生正在赶来为您服务，请您耐心等候";
        SingleBtnFragmentDialog.show(getSupportFragmentManager(),"六一健康",content,"查看订单",new SingleBtnFragmentDialog.OnClickSureBtnListener() {
            @Override
            public void onClickSureHander() {
                startActivity(new Intent());
            }
        });

    }

    /**
     * 获取验证码
     */
    private void getAuthCode() {
        if (!SystemUtils.isNetWorkValid(this)) {
            ToastUtil.showShort(this, R.string.getway_error_note);
            return;
        }
        String phone = pPhoneEdit.getText().toString();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.showToastPanl("请填写手机号码");
            return;
        }
        if(!ValidatorUtil.checkMobile(phone)){
            ToastUtil.showToastPanl("手机号码有误");
            return;
        }
        if ( ValidatorUtil.checkMobile(phone)) {
            ApiService.doHttpSendApplyConsuCode(phone, new JsonsfHttpResponseHandler(this) {
                @Override
                public void onSuccess(int statusCode, com.alibaba.fastjson.JSONObject object) {
                    super.onSuccess(statusCode, object);
                    if (object.containsKey("error_code")) {
                        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), object.getString("error_message"));
                    } else {
                        Sendcode = true;
                        timerTaskC();
                        ToastUtil.showShort(FlowMassageActivity.this, object.getString("message"));
                    }
                }
            });
        } else {
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), getString(R.string.phone_toastSpecialcharter));
        }
    }
    /**
     * 设置六十秒
     */
    private void timerTaskC() {
        runnable = new Runnable() {
            int i = 60;
            @Override
            public void run() {
                if (i == 0) {
                    codeBtn.setText("发送验证码");
                    codeBtn.setEnabled(true);
                    Sendcode=false;
                    return;
                } else {
                    --i;
                    handler.postDelayed(this, 1000);
                    codeBtn.setText(i + "");
                    codeBtn.setEnabled(false);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            codeBtn.setVisibility(View.VISIBLE);
        }
        return false;
    }
    /**
     * 所在地
     */
    private void setCity() {
        if (proList == null || cityMap == null) {
        } else {
            WheelUtils.setDoubleWheel(this, proList, cityMap, mainView, pop,
                    wheelView);
        }
    }
    /**
     * 获取地区
     */
    private void queryData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                proList = DictionaryHelper.getInstance(FlowMassageActivity.this).setCityData(
                        FlowMassageActivity.this, cityMap);
            }
        }).start();
    }
    /**
     * 设置内容
     */
    public void setText() {
        if (CURRENTVIEW.equals(suozaidi)) {
            suozaidi.setText(WheelUtils.getCurrent());
        }
        locationCode = WheelUtils.getCode();
    }
//    BasicNameValuePair
    private void initLast(){
        ApiService.doFindbeforeConsuPatientInfo(new MyApiCallback<JSONObject>(this){

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                try {
                    if(response!=null){
                        if("1".equals(response.optString("code"))){
                            lastMessage = response.optJSONObject("result");
                        }else if("2".equals(response.optString("code"))){
                            ToastUtil.showToastPanl(response.optString("message"));
                        }else {
                            ToastUtil.showToastPanl(response.toString());
                        }
                    }else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAfter() {
                super.onAfter();
                onBandData();
            }
        },this);

    }

    /**
     * 上次所填
     */
    private void onBandData() {
        try {
            pNameEdit.setText(lastMessage.optString("REAL_NAME"));
            pSexEdit.setText(lastMessage.optString("PATIENT_SEX"));
            pPhoneEdit.setText(lastMessage.optString("PATIENT_TELPHONE"));
            pAgeEdit.setText(lastMessage.optString("AGE"));
//            suozaidi.setText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
