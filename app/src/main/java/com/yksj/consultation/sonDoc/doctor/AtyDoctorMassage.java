package com.yksj.consultation.sonDoc.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.EvelateAdapter;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.ZoomImgeDialogFragment;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.DoctorWriteCaseActivity;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.sonDoc.friend.ConsultMessageActivity;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.DoctorSimpleBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.yksj.consultation.sonDoc.R.id.consult_money;

/**
 * 医生个人信息
 * Created by android_zl on 15/9/16.
 */
public class AtyDoctorMassage extends BaseActivity implements View.OnClickListener {
    private TextView dName, dPosition, dSpecialty, dHospital, dSpecial, dInfo, consultMoney;
    private ImageView dHeader;
    private Button selectDoc, selectAss;
    private String doctorType;
    private RatingBar mBar;// 评价星级
    private LinearLayout selectedHim, starLL, evalatell;
    private Intent intent;
    private JSONObject doctorMassage;
    private ImageLoader mInstance;
    private ListView lv;
    private EvelateAdapter mAdapter;
    private DoctorSimpleBean dsb;
    private List<Map<String, String>> mLists;
    private Map<String, String> mMap;
    private String pid = null;
    private String TITLE_NAME = "", OFFICE_NAME = "", UNIT_NAME = "", DOCTOR_SPECIALLY = "", INTRODUCTION = "";
    private TextView customerNum, evelateNum;
    private String customerNumStr, evelateNumStr;
    private int index = 0, start = 0, end = 0, color;
    private SpannableStringBuilder spBuilder;
    //    private PatientConsuListAdapter EAdapter;
//    private List<JSONObject> jsonLst;
    private String flag;
    private String doctorId;
    private ScrollView topSv;
    private Handler handler = new Handler();
    private String consultId;
    private int goalType = 0;//来这个界面的目的类型 0帮助患者找专家 2填完病历重选专家   1医生主动创建会诊帮患者找专家 3看医生资料
    private String officeCode = "", officeName = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.doctor_massage_aty);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        mInstance = ImageLoader.getInstance();
        dName = ((TextView) findViewById(R.id.select_name));
        topSv = (ScrollView) findViewById(R.id.scrollview_top1);
        dPosition = ((TextView) findViewById(R.id.select_position));
        dSpecialty = ((TextView) findViewById(R.id.select_specialty));
        dHospital = ((TextView) findViewById(R.id.select_hospital));
        dHeader = ((ImageView) findViewById(R.id.select_header));
        dSpecial = ((TextView) findViewById(R.id.doctor_special_m));
        dInfo = ((TextView) findViewById(R.id.doctor_info));
        //专家
        selectedHim = ((LinearLayout) findViewById(R.id.select_him_ll));
        consultMoney = ((TextView) findViewById(consult_money));
        selectDoc = ((Button) findViewById(R.id.select_him2));
        //医生
        starLL = ((LinearLayout) findViewById(R.id.select_star_ll));
        evalatell = ((LinearLayout) findViewById(R.id.evaluate_ll));
        customerNum = (TextView) findViewById(R.id.customer_num);
        evelateNum = (TextView) findViewById(R.id.evelate_num);
        selectAss = ((Button) findViewById(R.id.all_select_him1));
        doctorId = getIntent().getStringExtra("id");
        lv = (ListView) findViewById(R.id.listview_test);
        lv.setFocusable(false);
        if ("0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition())) {
            flag = "1";
        } else {
            flag = "2";
        }
        mBar = (RatingBar) findViewById(R.id.star);
        mBar.setStepSize(1f);
        if (getIntent().hasExtra("goalType")) {
            goalType = getIntent().getIntExtra("goalType", 0);
            if (goalType == 3) {
                selectDoc.setVisibility(View.GONE);
                selectAss.setVisibility(View.GONE);
                consultMoney.setVisibility(View.GONE);
            }
        }

        if (getIntent().hasExtra("OFFICECODE")) {
            officeCode = getIntent().getStringExtra("OFFICECODE");
        }
        if (getIntent().hasExtra("OFFICENAME")) {
            officeName = getIntent().getStringExtra("OFFICENAME");
        }
        selectDoc.setOnClickListener(this);
        selectAss.setOnClickListener(this);
        if (getIntent().hasExtra("type")) {
            int type = getIntent().getIntExtra("type", 0);
            switch (type) {
                case 0:
                    doctorType = "findDocInfo";
                    initData();
                    break;
                case 1:
                    doctorType = "findAssiInfo";
                    initData();
                    break;
            }
        }

    }

    private void initData() {
        ApiService.doHttpFindInfo(flag, doctorId, doctorType, new ObjectHttpResponseHandler(AtyDoctorMassage.this) {
            @Override
            public Object onParseResponse(String content) {
                if (content != null) {
                    return content;
                } else {
                    return null;
                }
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                try {
                    if (response != null) {
                        JSONObject object = new JSONObject(response.toString());
                        if (object.optString("code").equals("1")) {
                            JSONObject massage = object.optJSONObject("result");
                            LogUtils.d("TAG", "医生的信息" + response.toString());
                            onBandMassage(massage);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //绑定数据
    private void onBandMassage(JSONObject object) {
        doctorMassage = object;
        dsb = new DoctorSimpleBean();
        String dNameText = null;
        switch (doctorType) {
            case "findDocInfo"://专家
                if (goalType==3){
                    dNameText = object.optString("DOCTOR_REAL_NAME");
                }else {
                    dNameText = object.optString("DOCTOR_REAL_NAME").concat("(剩余").concat(object.optString("NUMS").concat("名额)"));
                }

                selectedHim.setVisibility(View.VISIBLE);
                if (!HStringUtil.isEmpty(object.optString("SERVICE_PRICE"))) {
                    consultMoney.setText(object.optString("SERVICE_PRICE") + "元");
                } else {
                    consultMoney.setText("0元");
                }
                if (getIntent().hasExtra("ORDER")) {
                    findViewById(R.id.select_him2).setVisibility(View.GONE);
                }
                if (HStringUtil.isEmpty(object.optString("SERVICE_PRICE"))){
                    dsb.SERVICE_PRICE = "";
                }else{
                    dsb.SERVICE_PRICE = object.optString("SERVICE_PRICE");
                }

                if (getIntent().hasExtra("PID")) {
                    pid = getIntent().getStringExtra("PID");
                }
                dsb.DOCTOR_SERVICE_NUMBER = Integer.parseInt(object.optString("NUMS"));
//                evelateNum.setText("历史会诊记录");
//                EAdapter = new PatientConsuListAdapter(this);
//                lv.setAdapter(EAdapter);
//                lv.setOnItemClickListener(this);
//                JSONArray arrayE = object.optJSONArray("commentList");
//                jsonLst = new ArrayList<JSONObject>();
//                for (int i = 0; i < arrayE.length(); i++) {
//                    JSONObject item = arrayE.optJSONObject(i);
//                    jsonLst.add(item);
//                }
//                EAdapter.removeAll();
//                EAdapter.addAll(jsonLst);
                break;
            case "findAssiInfo"://医生
                dNameText = object.optString("DOCTOR_REAL_NAME");
                starLL.setVisibility(View.VISIBLE);
                evalatell.setVisibility(View.VISIBLE);
                if (!"".equals(object.optString("averageValue"))) {
                    mBar.setRating(Float.parseFloat(object.optString("averageValue")));
                }
                if (getIntent().hasExtra("ORDER")) {
                    findViewById(R.id.all_select_him1).setVisibility(View.GONE);
                }
                mAdapter = new EvelateAdapter(this);
                lv.setAdapter(mAdapter);
                JSONArray array = object.optJSONArray("commentList");
                mLists = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    mMap = new HashMap<>();
                    mMap.put("COMMENT_RESULT", item.optString("COMMENT_RESULT"));//
                    mMap.put("SERVICE_LEVEL", item.optString("SERVICE_LEVEL"));//
                    mMap.put("REAL_NAME", item.optString("REAL_NAME"));//
                    mLists.add(mMap);
                }
                mAdapter.add(mLists);
//                evelateNumStr="共参与"+object.optString("num")+"次会诊";
//                evelateNum.setText(changeColor(evelateNumStr));
                customerNumStr = "用户评价(" + object.optString("num") + ")";
                evelateNum.setText(changeColor(customerNumStr));
                break;
        }
        titleTextV.setText(object.optString("DOCTOR_REAL_NAME") + "医生");
        dName.setText(dNameText);
        mInstance.displayImage("", object.optString("ICON_DOCTOR_PICTURE"), dHeader);
        dHeader.setOnClickListener(this);
        if ("null".equals(object.optString("TITLE_NAME"))) {
            TITLE_NAME = "暂无";
        } else {
            TITLE_NAME = object.optString("TITLE_NAME");
        }
//        dPosition.setText(TITLE_NAME);
        dSpecialty.setText(TITLE_NAME);
        if ("null".equals(object.optString("OFFICE_NAME"))) {
            OFFICE_NAME = "暂无";
        } else {
            OFFICE_NAME = object.optString("OFFICE_NAME");
        }
//        dSpecialty.setText(OFFICE_NAME);
        dPosition.setText(OFFICE_NAME);
        if ("null".equals(object.optString("UNIT_NAME"))) {
            UNIT_NAME = "暂无";
        } else {
            UNIT_NAME = object.optString("UNIT_NAME");
        }
        dHospital.setText(UNIT_NAME);
        if ("null".equals(object.optString("DOCTOR_SPECIALLY"))) {
            DOCTOR_SPECIALLY = "暂无";
        } else {
            DOCTOR_SPECIALLY = object.optString("DOCTOR_SPECIALLY");
        }
        dSpecial.setText(DOCTOR_SPECIALLY);
        if ("null".equals(object.optString("INTRODUCTION"))) {
            INTRODUCTION = "暂无";
        } else {
            INTRODUCTION = object.optString("INTRODUCTION");
        }
        dInfo.setText(INTRODUCTION);
        dsb.DOCTOR_REAL_NAME = object.optString("DOCTOR_REAL_NAME");
        dsb.CUSTOMER_ID = Integer.parseInt(getIntent().getStringExtra("id"));
        dsb.ICON_DOCTOR_PICTURE = object.optString("ICON_DOCTOR_PICTURE");
        dsb.TITLE_NAME = object.optString("TITLE_NAME");
        dsb.DOCTOR_SPECIALLY = object.optString("DOCTOR_SPECIALLY");
        dsb.UNIT_NAME = object.optString("UNIT_NAME");
        dsb.INTRODUCTION = object.optString("INTRODUCTION");
        handler.post(new Runnable() {
            @Override
            public void run() {
                topSv.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.select_him2:
                if (getIntent().hasExtra("goalType")) {
                    goalType = getIntent().getIntExtra("goalType", 0);
                    switch (goalType) {
                        case 0://帮我找
                        case 2://重选
                            consultId = getIntent().getStringExtra("consultId");
                            selectHim(dsb);
                            break;
                        case 1://主动发起
                            intent = new Intent(AtyDoctorMassage.this, ConsultMessageActivity.class);
                            intent.putExtra("data", dsb);
                            intent.putExtra("PID", pid);
                            intent.putExtra("OFFICECODE", officeCode);
                            intent.putExtra("OFFICENAME", officeName);
                            intent.putExtra("PROMTER", "10");
                            startActivity(intent);
                            break;

                    }
                }
////                intent = new Intent(AtyDoctorMassage.this,FlowMassageActivity.class);
//                if(getIntent().hasExtra("consultId")){
//                    consultId =getIntent().getStringExtra("consultId");
//                    List<BasicNameValuePair> valuePairs=new ArrayList<>();
//                    valuePairs.add(new BasicNameValuePair("TYPE","reSelectedExpert"));
//                    valuePairs.add(new BasicNameValuePair("CUSTOMERID", dsb.CUSTOMER_ID+""));
//                    valuePairs.add(new BasicNameValuePair("CONSULTATIONID", consultId));
//                    valuePairs.add(new BasicNameValuePair("SERVICE_PRICE", "" + dsb.SERVICE_PRICE));
//                    ApiService.doGetConsultationInfoSet(valuePairs, new MyApiCallback<String >(this) {
//                        @Override
//                        public void onError(Request request, Exception e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            ResponseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, ResponseBean.class);
//
//                            if ("1".equals(bb.code)) {
//                                EventBus.getDefault().post(new MyEvent("refresh", 2));
//                                Intent intent=new Intent(AtyDoctorMassage.this, AtyOrderDetails.class);
//                                intent.putExtra("CONID", Integer.parseInt(consultId));
//                                intent.putExtra("BACK", 2);
//                                startActivity(intent);
//                            }else
//                                ToastUtil.showShort(AtyDoctorMassage.this, bb.message);
//                        }
//                    }, this);
//                }else {
//                    intent = new Intent(AtyDoctorMassage.this, ConsultMessageActivity.class);
//                    intent.putExtra("data", dsb);
//                    intent.putExtra("PID", pid);
//                    intent.putExtra("PROMTER", "10");
//                    startActivity(intent);
//                }
                break;
            case R.id.all_select_him1:
                intent = new Intent();
                intent.putExtra("data", dsb);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.select_header:
                ZoomImgeDialogFragment.show(doctorMassage.optString("DOCTOR_PICTURE"),
                        getSupportFragmentManager());
                break;
        }
    }

    private SpannableStringBuilder changeColor(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                if (index == 0) {
                    start = i;
                }
                index++;
            }
        }
        end = start + index;
        spBuilder = new SpannableStringBuilder(str);
        color = getResources().getColor(R.color.red);//charge_red
        CharacterStyle charaStyle = new ForegroundColorSpan(color);
        spBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        index = 0;
        return spBuilder;
    }

    //    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(AtyDoctorMassage.this, AtyOrderDetails.class);
//        JSONObject object = EAdapter.datas.get(i);
//        intent.putExtra("CONID", object.optInt("CONSULTATION_ID"));
//        intent.putExtra("PERSONID", doctorId);
//        startActivity(intent);
//    }
    private void selectHim(DoctorSimpleBean dsb) {
        ///DuoMeiHealth/ConsultationInfoSet?TYPE=reSelectedExpert&CUSTOMERID=&CONSULTATIONID=
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE", "reSelectedExpert"));
        valuePairs.add(new BasicNameValuePair("CUSTOMERID", dsb.CUSTOMER_ID + ""));
        valuePairs.add(new BasicNameValuePair("CONSULTATIONID", consultId));
        valuePairs.add(new BasicNameValuePair("SERVICE_PRICE", "" + dsb.SERVICE_PRICE));
        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {

                    EventBus.getDefault().post(new MyEvent("refresh", 2));
                    if (goalType == 2) {
                        Intent intent = new Intent(AtyDoctorMassage.this, AtyOrderDetails.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        SingleBtnFragmentDialog.showSinglebtn(AtyDoctorMassage.this, "您已为患者选择专家,现在请为患者填写病历吧。", "填写病历", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                            @Override
                            public void onClickSureHander() {
                                Intent intent = new Intent(AtyDoctorMassage.this, DoctorWriteCaseActivity.class);
                                intent.putExtra("consultId", consultId);
                                startActivity(intent);
                            }
                        }).show();
                    }
                } else
                    ToastUtil.showShort(AtyDoctorMassage.this, bb.message);
            }
        }, this);

    }
}
