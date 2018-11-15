package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.widget.ExpandableDescView;
import com.library.base.widget.SuperTextView;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.comm.ZoomImgeDialogFragment;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctor.RecommendPatientActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import java.util.List;

import okhttp3.Request;

;

/**
 * 患者个人信息
 * Created by zheng on 2015/9/17.
 */
public class AtyPatientMassage extends BaseTitleActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView patientHeader;
    private TextView name;//名称
    private TextView agesex;//性别
    private TextView phone;//电话
    private TextView address;//地址
    private TextView textAge;//年龄

    private TextView agesexKey;//性别
    private TextView textAgeKey;//年龄
    private TextView phoneKey;//电话
    private TextView addressKey;//地址

    private SuperTextView followup;//随访计划

    private ExpandableDescView duty;
    private ExpandableDescView allergy;//过敏史


    private String pid = null;
    private JSONObject result;
    private ImageLoader mInstance;
    private boolean dutyExpanded = false;
    private boolean allergyExpanded = false;

    private List<JSONObject> jsonLst;
    private String sexStr = "", ageStr = "", nameStr = "", phoneStr = "", addressStr = "";
    private NestedScrollView topSv;
    public static final String OPEN_PLAN = "open_plan";

    @Override
    public int createLayoutRes() {
        return R.layout.aty_patient_massage;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("我的信息");
        agesexKey = ((TextView) findViewById(R.id.include_sex).findViewById(R.id.key));
        textAgeKey = ((TextView) findViewById(R.id.include_age).findViewById(R.id.key));
        phoneKey = ((TextView) findViewById(R.id.include_phone).findViewById(R.id.key));
        addressKey = ((TextView) findViewById(R.id.include_address).findViewById(R.id.key));
        agesexKey.setText("性别");
        textAgeKey.setText("年龄");
        phoneKey.setText("联系方式");
        addressKey.setText("地址");

        agesex = ((TextView) findViewById(R.id.include_sex).findViewById(R.id.value));
        textAge = ((TextView) findViewById(R.id.include_age).findViewById(R.id.value));
        phone = ((TextView) findViewById(R.id.include_phone).findViewById(R.id.value));
        address = ((TextView) findViewById(R.id.include_address).findViewById(R.id.value));

        followup = findViewById(R.id.follow_up_content);
        followup.setOnClickListener(this);

        topSv = findViewById(R.id.scrollView_top);
        patientHeader = ((ImageView) findViewById(R.id.patient_header));
        mInstance = ImageLoader.getInstance();
        name = ((TextView) findViewById(R.id.pname));

        duty = findViewById(R.id.clinic_specialty_content);
        duty.setMaxLine(2);
        allergy = findViewById(R.id.clinic_specialty_content2);
        allergy.setMaxLine(2);

        findViewById(R.id.select_p).setOnClickListener(this);
        findViewById(R.id.follow_up_content2).setOnClickListener(this);
        findViewById(R.id.follow_up_content3).setOnClickListener(this);


        if (getIntent().hasExtra(OPEN_PLAN)) {
            findViewById(R.id.follow_up_content).setVisibility(View.VISIBLE);
        }
        if (getIntent().hasExtra("ORDER")) {
            findViewById(R.id.select_p).setVisibility(View.GONE);
        }
        if (getIntent().hasExtra("MAIN")) {
            findViewById(R.id.select_p).setVisibility(View.GONE);
        }
//        mListView = (ListView) findViewById(R.id.case_list);
//        mAdapter = new PatientConsuListAdapter(this);
//        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(this);
        initDate();
    }

    //获取资料
    private void initDate() {
        if (getIntent().hasExtra("PID")) {
            pid = getIntent().getStringExtra("PID");
        }
        onBandData();
    }

    String array = "";

    //绑定资料
    private void onBandData() {
        ApiService.OKHttpFindPatientInfot2(pid, DoctorHelper.getId(), new MyApiCallback<JSONObject>(this) {
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
                        if (patientInfo.optBoolean("patientCase")) {
                            findViewById(R.id.follow_up_content3).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.follow_up_content3).setVisibility(View.GONE);
                        }
                        result = patientInfo.optJSONObject("patientInfo");
                        mInstance.displayImage(result.optString("CUSTOMER_SEX"), result.optString("CLIENT_ICON_BACKGROUND"), patientHeader);
                        patientHeader.setOnClickListener(AtyPatientMassage.this);
                        if ("null".equals(result.optString("REAL_NAME"))) {
                            nameStr = "暂无姓名";
                        } else {
                            nameStr = result.optString("REAL_NAME");
                        }
                        name.setText(nameStr);
                        titleTextV.setText(nameStr);
                        if ("M".equals(result.optString("CUSTOMER_SEX"))) {
                            sexStr = "男";
                        } else if ("W".equals(result.optString("CUSTOMER_SEX"))) {
                            sexStr = "女";
                        } else {
                            sexStr = "未知";
                        }
                        if ("null".equals(result.optString("AGE"))) {
                            ageStr = "暂无年龄";
                        } else {
                            ageStr = result.optString("AGE");
                        }
                        agesex.setText(sexStr);
                        textAge.setText(ageStr + "岁");
                        if (HStringUtil.isEmpty(result.optString("PHONE_NUMBER"))) {
                            phoneStr = "暂无手机";
                        } else {
                            phoneStr = result.optString("PHONE_NUMBER");
                        }
                        phone.setText(phoneStr);
                        if ("null".equals(result.optString("CUSTOMER_LOCUS"))) {
                            addressStr = "暂无地区";
                        } else {
                            addressStr = result.optString("CUSTOMER_LOCUS");
                        }
                        address.setText(addressStr);
                        if ("null".equals(result.optString("DISEASE_DESC"))) {
                            findViewById(R.id.clinic_layout_item1).setVisibility(View.GONE);
                        } else {
                            more();
                        }
                        if ("null".equals(result.optString("ALLERGY"))) {
                            findViewById(R.id.clinic_layout_item2).setVisibility(View.GONE);
                        } else {
                            more1();
                        }

                        array = String.valueOf(patientInfo.optJSONArray("consultationRecord"));
//                        JSONArray array = patientInfo.optJSONArray("consultationRecord");
//                        jsonLst = new ArrayList<JSONObject>();
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject item = array.optJSONObject(i);
//                            jsonLst.add(item);
//                        }
////                        mAdapter.removeAll();
////                        mAdapter.addAll(jsonLst);
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                topSv.fullScroll(ScrollView.FOCUS_UP);
//                            }
//                        });
                    }
                }
            }
        }, this);
    }

    private void more() {
        if (!TextUtils.isEmpty(result.optString("DISEASE_DESC"))) {
            StringBuilder b = new StringBuilder();
            StringBuilder c = new StringBuilder();
            b.append(result.optString("DISEASE_DESC"));
            c.append(result.optString("ALLERGY"));

            duty.setContent(b.toString());
//            allergy.setContent(c.toString());
//            if (duty.getLineCount() < 2)//行数小于2,将展开按钮隐藏
//                findViewById(R.id.clinic_specialty_index).setVisibility(View.INVISIBLE);
//            else
//                findViewById(R.id.clinic_layout_item1).setOnClickListener(this);
        } else {
            findViewById(R.id.clinic_layout_item1).setVisibility(View.GONE);
        }
    }

    private void more1() {
        if (!TextUtils.isEmpty(result.optString("ALLERGY"))) {
            StringBuilder c = new StringBuilder();
            c.append(result.optString("ALLERGY"));
            allergy.setContent(c.toString());
//            if (allergy.getLineCount() < 2)//行数小于2,将展开按钮隐藏
//                findViewById(R.id.clinic_specialty_index2).setVisibility(View.INVISIBLE);
//            else
//                findViewById(R.id.clinic_layout_item2).setOnClickListener(this);
        } else {
            findViewById(R.id.clinic_layout_item2).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.follow_up_content://随访计划
                intent = new Intent(this, AtyFollowUpPlan.class);
                intent.putExtra("customer_id", pid);
                startActivity(intent);
                break;
            case R.id.follow_up_content2://会诊记录
                intent = new Intent(this, AtyFollowUpPlan2.class);
                intent.putExtra("customer_id", pid);
                intent.putExtra(AtyFollowUpPlan2.CONTENT, array);
                startActivity(intent);
                break;
            case R.id.follow_up_content3://病历
                intent = new Intent(this, AtyFollowUpPlan3.class);
                intent.putExtra("customer_id", pid);
                startActivity(intent);
                break;
            case R.id.title_right2://推荐
                intent = new Intent(this, RecommendPatientActivity.class);
                startActivity(intent);
                break;
            case R.id.clinic_layout_item1://职务文本收缩展开
                if (dutyExpanded) {
                    dutyExpanded = false;
//                    duty.setMaxLines(2);
                } else {
                    dutyExpanded = true;
//                    duty.setMaxLines(100);
                }
                break;
            case R.id.clinic_layout_item2://职务文本收缩展开
                if (allergyExpanded) {
                    allergyExpanded = false;
//                    allergy.setMaxLines(2);
                } else {
                    allergyExpanded = true;
//                    allergy.setMaxLines(100);
                }
                break;
            case R.id.select_p:
                intent = new Intent(this, PConsultMainActivity.class);
                intent.putExtra("PID", pid);
                startActivity(intent);
                break;
            case R.id.patient_header:
                ZoomImgeDialogFragment.show(result.optString("BIG_ICON_BACKGROUND"), getSupportFragmentManager());
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(AtyPatientMassage.this, AtyOrderRecord.class);
//        JSONObject object = mAdapter.datas.get(i);
//        intent.putExtra("CONID", object.optInt("CONSULTATION_ID"));
//        intent.putExtra("PERSONID", pid);
//        startActivity(intent);
    }
}
