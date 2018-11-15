package com.yksj.consultation.sonDoc.friend;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.Request;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrdersDetails;
import com.yksj.consultation.sonDoc.doctor.SelectExpertMainUI;
import com.yksj.healthtalk.bean.DoctorSimpleBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * 确认会诊信息
 * Created by zheng on 15/9/29.
 */
public class ConsultMessageActivity extends BaseActivity implements View.OnClickListener, DoubleBtnFragmentDialog.OnOfficFullListener {
    private ImageView headImage, eHeadImage;
    private TextView nameTv, genderTv, ageTv, teleTv, addressTv;
    private TextView eNameTv, eDoctitleTv, eHospitalTv, eSpeticalTv, eNumlTv, ePriceTv;
    private TextView tAllergy;//过敏史
    private TextView duty;
    private Button selectBtn;//,sendBtn;
    private DoctorSimpleBean dsb;
    private ImageLoader mInstance;
    private JSONObject result;
    private String pid = "", officeCode = "";
    private String sexStr = "", ageStr = "", nameStr = "", phoneStr = "", addressStr = "";
    private String allergy = "";
    private Dialog dialog;
    private String officeName;

    private boolean dutyExpanded = false;
    private boolean allergyExpanded = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.consult_message_aty_layout);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("确认会诊信息");
        titleLeftBtn.setOnClickListener(this);
        headImage = ((ImageView) findViewById(R.id.imag_head));
        eHeadImage = ((ImageView) findViewById(R.id.select_expert_list_item_headicon));
        nameTv = ((TextView) findViewById(R.id.tv_name));
        genderTv = ((TextView) findViewById(R.id.tv_gender));
        ageTv = ((TextView) findViewById(R.id.tv_age));
        teleTv = ((TextView) findViewById(R.id.tv_tele));
        addressTv = ((TextView) findViewById(R.id.tv_address));
        eNameTv = ((TextView) findViewById(R.id.select_expert_list_item_name));
        eDoctitleTv = ((TextView) findViewById(R.id.select_expert_list_item_doctitle));
        eHospitalTv = ((TextView) findViewById(R.id.select_expert_list_item_hospital));
        eSpeticalTv = ((TextView) findViewById(R.id.select_expert_list_item_spetical));
        eNumlTv = ((TextView) findViewById(R.id.select_expert_list_item_num));
        ePriceTv = ((TextView) findViewById(R.id.select_expert_list_item_price));
        selectBtn = ((Button) findViewById(R.id.select_expert_list_item_select));//select_expert_list_item_select
//        selectBtn.setVisibility(View.GONE);
        selectBtn.setText("重选专家");
        selectBtn.setOnClickListener(this);
        duty = (TextView) findViewById(R.id.clinic_specialty_content_info);
        tAllergy = (TextView) findViewById(R.id.clinic_specialty_content_info2);

//        sendBtn = ((Button) findViewById(R.id.commit));
        ((Button) findViewById(R.id.commit)).setOnClickListener(this);
        mInstance = ImageLoader.getInstance();
        if (getIntent().hasExtra("PID")) {
            pid = getIntent().getStringExtra("PID");
        }
        if (getIntent().hasExtra("OFFICECODE")) {
            officeCode = getIntent().getStringExtra("OFFICECODE");
        }
        if (getIntent().hasExtra("OFFICENAME")) {
            officeName = getIntent().getStringExtra("OFFICENAME");
        }
        showData();
    }

    private void showData() {
        if (getIntent().hasExtra("data")) {
            dsb = (DoctorSimpleBean) getIntent().getSerializableExtra("data");
            eNameTv.setText(dsb.DOCTOR_REAL_NAME);
            eDoctitleTv.setText(dsb.TITLE_NAME);
            eHospitalTv.setText(dsb.UNIT_NAME);
            eSpeticalTv.setText(dsb.DOCTOR_SPECIALLY);
            eNumlTv.setText("剩余" + dsb.DOCTOR_SERVICE_NUMBER + "个名额");
            ePriceTv.setText(dsb.SERVICE_PRICE + "元");
            mInstance.displayImage("", dsb.ICON_DOCTOR_PICTURE, eHeadImage);
        }
        ApiService.OKHttpFindPatientInfot(pid, new MyApiCallback<JSONObject>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    String code = response.optString("code");
                    if ("1".equals(code)) {
                        JSONObject patientInfo = response.optJSONObject("result");
                        result = patientInfo.optJSONObject("patientInfo");
                        mInstance.displayImage(result.optString("CUSTOMER_SEX"), result.optString("CLIENT_ICON_BACKGROUND"), headImage);
                        if ("null".equals(result.optString("REAL_NAME"))) {
                            nameStr = "暂无";
                        } else {
                            nameStr = result.optString("REAL_NAME");
                        }
                        nameTv.setText(nameStr);
                        if ("M".equals(result.optString("CUSTOMER_SEX"))) {
                            sexStr = "男";
                        } else if ("W".equals(result.optString("CUSTOMER_SEX"))) {
                            sexStr = "女";
                        } else {
                            sexStr = "暂无";
                        }
                        genderTv.setText(sexStr);
                        if ("null".equals(result.optString("AGE"))) {
                            ageStr = "暂无";
                        } else {
                            ageStr = result.optString("AGE");
                        }
                        ageTv.setText(ageStr);
                        if ("null".equals(result.optString("PHONE_NUMBER"))) {
                            phoneStr = "暂无";
                        } else {
                            phoneStr = result.optString("PHONE_NUMBER");
                        }
                        teleTv.setText(phoneStr);
                        if ("null".equals(result.optString("CUSTOMER_LOCUS"))) {
                            addressStr = "暂无";
                        } else {
                            addressStr = result.optString("CUSTOMER_LOCUS");
                        }
                        addressTv.setText(addressStr);
                        if ("null".equals(result.optString("DISEASE_DESC"))) {
                        } else {
                            more();
                        }

                        if (!HStringUtil.isEmpty(result.optString("ALLERGY"))) {
                            allergy = result.optString("ALLERGY");
                            moreAllergy();
                        }
                    }
                }
            }
        }, this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.commit:
                if (dsb.DOCTOR_SERVICE_NUMBER < 1) {
                    String numHint = "对不起,您所选的专家会诊名额已满,您可以重新选择专家.";
                    dialog = DoubleBtnFragmentDialog.showDoubleBtn1(ConsultMessageActivity.this, numHint, "知道了", this);
                    dialog.show();
                } else {
                    sendData();
                }
                break;
            case R.id.select_expert_list_item_select:
                Intent intent = new Intent(ConsultMessageActivity.this, SelectExpertMainUI.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("OFFICECODE", officeCode);
                intent.putExtra("OFFICENAME", officeName);
                intent.putExtra("goalType", 1);
                startActivity(intent);
                break;

            case R.id.rl_illnesscontent://文本收缩展开
                if (dutyExpanded) {
                    dutyExpanded = false;
                    duty.setMaxLines(2);
                } else {
                    dutyExpanded = true;
                    duty.setMaxLines(100);
                }
                break;

            case R.id.rl_illnesscontent2://文本收缩展开
                if (allergyExpanded) {
                    allergyExpanded = false;
                    tAllergy.setMaxLines(2);
                } else {
                    allergyExpanded = true;
                    tAllergy.setMaxLines(100);
                }
                break;
        }
    }

    private void sendData() {
        JSONObject object = new JSONObject();
        LogUtil.d("TAG", "医生发起患者信息" + result.toString());
        LogUtil.d("TAG", "医生发起患者信息" + dsb.toString());
        try {
            if ("null".equals(result.optString("REAL_NAME")) | "".equals(result.optString("REAL_NAME"))) {
                object.put("CONSULTATION_NAME", "未知" + officeName + "的会诊");
            } else {
                object.put("CONSULTATION_NAME", result.optString("REAL_NAME") + officeName + "的会诊");
            }
            object.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
            object.put("PATIENTID", pid);
            object.put("EXPERT_ID", dsb.CUSTOMER_ID);
            object.put("SERVICE_PRICE", dsb.SERVICE_PRICE);
            object.put("DOCTORID", LoginBusiness.getInstance().getLoginEntity().getId());
            object.put("PROMOTER_TYPE", "40");
            object.put("OFFICEID", officeCode);
//            object.put("DISEASE_DESC", duty.getText().toString());//加疾病详情
            object.put("ALLERGY", allergy);
            String json = object.toString();
            List<BasicNameValuePair> params = new ArrayList<>();
            BasicNameValuePair param = new BasicNameValuePair("PARAMETER", json);
            params.add(param);
            ApiService.OKHttpApplyConsuByAssistant(params, new MyApiCallback<JSONObject>(this) {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    super.onResponse(response);
                    if (response != null) {
                        if ("1".equals(response.optString("code"))) {
                            LogUtil.d("TAG", "医生发起成功返回" + response.optString("result"));
                            final JSONObject result = response.optJSONObject("result");
                            SingleBtnFragmentDialog.show(getSupportFragmentManager(), "会诊创建成功", response.optString("message"), "查看会诊", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                                @Override
                                public void onClickSureHander() {
                                    Intent intent = new Intent(ConsultMessageActivity.this, AtyOrdersDetails.class);
                                    String conid = result.optString("CONSULTATION_ID");
                                    LogUtil.d("TAG", conid);
                                    intent.putExtra("CONID", Integer.parseInt(conid));
                                    intent.putExtra("BACK", 2);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            ToastUtil.showShort(response.optString("message"));
                        }
                    }
                }
            }, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void more() {
        if (!TextUtils.isEmpty(result.optString("DISEASE_DESC"))) {
            StringBuilder b = new StringBuilder();
            b.append(result.optString("DISEASE_DESC"));
            duty.setText(b);
            if (duty.getLineCount() <= 2)//行数小于2,将展开按钮隐藏
                findViewById(R.id.clinic_specialty_index_info).setVisibility(View.INVISIBLE);
            else
                findViewById(R.id.rl_illnesscontent).setOnClickListener(this);
        }
    }

    //过敏史
    private void moreAllergy() {
        if (!TextUtils.isEmpty(result.optString("ALLERGY"))) {
            StringBuilder b = new StringBuilder();
            b.append(result.optString("ALLERGY"));
            tAllergy.setText(b);
            if (tAllergy.getLineCount() <= 2)//行数小于2,将展开按钮隐藏
                findViewById(R.id.clinic_specialty_index_info2).setVisibility(View.INVISIBLE);
            else
                findViewById(R.id.rl_illnesscontent2).setOnClickListener(this);
        }
    }

    @Override
    public void onBtn3() {
        dialog.dismiss();
    }
}
