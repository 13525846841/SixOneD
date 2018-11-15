package com.yksj.consultation.sonDoc.consultation.consultationorders;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.OrderDetailAdapter;
import com.yksj.consultation.bean.ExpertStatus;
import com.yksj.consultation.bean.IntentConstant;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.ObjectType;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseShowFragment;
import com.yksj.consultation.sonDoc.casehistory.DoctorEditCaseActivity;
import com.yksj.consultation.sonDoc.casehistory.DoctorWriteCaseActivity;
import com.yksj.consultation.sonDoc.doctor.SelectExpertMainUI;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by HEKL on 15/9/23.
 * 订单详情_
 */
public class AtyOrderDetails extends BaseActivity implements View.OnClickListener, SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener, PullToRefreshBase.OnRefreshListener<ScrollView>, AdapterView.OnItemClickListener {
    private TextView tProcess1, tProcess2, tProcess3, tProcess4, mTip, mPrice, mCaseSupply;
    private ImageView iProcess1, iProcess2, iProcess3;
    //mImageHeadP, mImageHeadD, mImageHeadE;
    private Button mOpinion, mOutPatient, mEvaluate;
    private Button mAccept, mReject;
    private int type;
    private String inviteType = "";//被邀请的专家状态 邀请状态(10-已邀请未回应,20-长时间未回应，邀请失败,30－拒绝邀请,40-接受邀请,50-超时接受邀请)
    private int Ptype;
    private int conId;
    private String patientIcon;
    private boolean ISEXPERT = true;
    private boolean ISRECORD = true;
    private String conName;
    private String creDocId;
    private JSONObject object;
    private int officeId = 0;
    private String docPosition;//0 医生 非0 专家
    private int clickCount = 0;
    private int patientId;
    private int promoterType;
    private int beforeSupply;//专家端待接诊是否有补充病历按钮
    private int afterSupply;//专家端待同意是否有补充病历按钮
    private int patient, docId, expId;//医生和专家id
    private int loadCount;
    private String patientName;
    private ArrayList<HashMap<String, String>> list = null;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOption;//异步加载图片的操作
    private CaseShowFragment fragment;
    private Bundle bundle;
    private String value;
    private FragmentTransaction beginTransaction;
    private int loadDataCount;
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局

    private GridView gv;
    private OrderDetailAdapter mAdapter;


    private List<JSONObject> mList;//会诊相关人物信息
    private String mInvitedExpert = "";//被邀请专家信息
    private String groupId = "";//群聊id

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_orderdetails);
        conId = getIntent().getIntExtra("CONID", 0);
        if (getIntent().hasExtra("PERSONID")) {
            creDocId = getIntent().getStringExtra("PERSONID");
        } else {
            creDocId = DoctorHelper.getId();
        }
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            docPosition = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition();
            if (!("0".equals(docPosition))) {
                Ptype = 1;
            } else {
                Ptype = 0;
            }
            loadData(Ptype, creDocId, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        initializeTitle();
        titleTextV.setText("会诊详情");
        titleLeftBtn.setOnClickListener(this);
//        titleRightBtn2.setText("日志");
//        titleRightBtn2.setVisibility(View.VISIBLE);
//        titleRightBtn2.setOnClickListener(this);

        //日志图标
//        mImageViewD.setVisibility(View.VISIBLE);
//        mImageViewD.setOnClickListener(this);

        findViewById(R.id.btn_talk).setOnClickListener(this);
        list = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        mOption = DefaultConfigurationFactory.createHeadDisplayImageOptions(this);
        fragment = new CaseShowFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        beginTransaction = fragmentManager.beginTransaction();
        bundle = new Bundle();
        mOpinion = (Button) findViewById(R.id.opinion);
        mOpinion.setOnClickListener(this);
        mOutPatient = (Button) findViewById(R.id.outpatient);
        mOutPatient.setOnClickListener(this);
        mEvaluate = (Button) findViewById(R.id.evaluate);
        mEvaluate.setOnClickListener(this);
        mAccept = (Button) findViewById(R.id.accept);
        mAccept.setOnClickListener(this);
        mReject = (Button) findViewById(R.id.reject);
        mReject.setOnClickListener(this);
        mCaseSupply = (TextView) findViewById(R.id.tv_casesupply);
        mPrice = (TextView) findViewById(R.id.tv_price);//价钱
        mTip = (TextView) findViewById(R.id.tv_consul);//提示
        tProcess1 = (TextView) findViewById(R.id.tv_process1);
        tProcess2 = (TextView) findViewById(R.id.tv_process2);
        tProcess3 = (TextView) findViewById(R.id.tv_process3);
        tProcess4 = (TextView) findViewById(R.id.tv_process4);
        iProcess1 = (ImageView) findViewById(R.id.iv_process1);
        iProcess2 = (ImageView) findViewById(R.id.iv_process2);
        iProcess3 = (ImageView) findViewById(R.id.iv_process3);
//        mImageHeadP = (ImageView) findViewById(R.id.image_head_p);
//        mImageHeadD = (ImageView) findViewById(R.id.image_head_d);
//        mImageHeadE = (ImageView) findViewById(R.id.image_head_e);
//        mImageHeadP.setOnClickListener(this);
//        mImageHeadD.setOnClickListener(this);
//        mImageHeadE.setOnClickListener(this);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        mPullToRefreshScrollView.scrollTo(0, 0);
        gv = (GridView) findViewById(R.id.number_head);
        mAdapter = new OrderDetailAdapter(this);
        gv.setAdapter(mAdapter);
        gv.setOnItemClickListener(this);

        mList = new ArrayList<>();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
//            case R.id.main_listmenuD://日志
//                i = new Intent(AtyOrderDetails.this, ConLogActivity.class);
//                i.putExtra("conId", conId + "");
//                startActivity(i);
//                break;
            case R.id.btn_talk:
                if (loadCount == 0) {
                    loadData(Ptype, creDocId, 1);
                }
                break;
            case R.id.opinion:
                if ("0".equals(docPosition)) {//医生
                    switch (type) {
                        case 20:
                            if ((!ISEXPERT) && (!ISRECORD)) {
                                if (officeId != 0) {
                                    i = new Intent(AtyOrderDetails.this, SelectExpertMainUI.class);
                                    i.putExtra("consultId", conId + "");
                                    i.putExtra("OFFICECODE", officeId + "");
                                    startActivity(i);
                                }
                            } else if (ISEXPERT && !ISRECORD) {
                                i = new Intent(AtyOrderDetails.this, DoctorWriteCaseActivity.class);
                                i.putExtra("consultId", conId + "");
                                i.putExtra("otherId", patientId + "");
                                i.putExtra("otherName", patientName);
                                startActivity(i);
                            }
                            break;
                        case 30://修改病历
                        case 55:
                        case 85:
                            if ((ISEXPERT) && (ISRECORD)) {
                                i = new Intent(AtyOrderDetails.this, DoctorEditCaseActivity.class);
                                i.putExtra("consultId", conId + "");
                                i.putExtra("otherId", patientId + "");
                                i.putExtra("otherName", patientName);
                                startActivity(i);
                            }
                            break;
                        case 60://重选专家
                            if (officeId != 0) {
                                i = new Intent(AtyOrderDetails.this, SelectExpertMainUI.class);
                                i.putExtra("consultId", conId + "");
                                i.putExtra("OFFICECODE", officeId + "");
                                i.putExtra("goalType", 2);
                                startActivity(i);
                            }
                            break;
                    }
                } else {//专家
                    switch (type) {
                        case 50://专家同意接诊
                            accExpert();
                            break;
                        case 80://专家给意见
                        case 88:
                            i = new Intent(AtyOrderDetails.this, AtyExpertOpinion.class);
                            i.putExtra("TYPE", 1);
                            i.putExtra("conId", conId);
                            startActivity(i);
                            break;
                    }
                }
                break;
            case R.id.outpatient:
                if ("0".equals(docPosition)) {//医生
                    switch (type) {
                        case 20:
                            if ((!ISEXPERT) && (!ISRECORD)) {
                                if (officeId != 0) {
                                    i = new Intent(AtyOrderDetails.this, SelectExpertMainUI.class);
                                    i.putExtra("consultId", conId + "");
                                    i.putExtra("OFFICECODE", officeId + "");
                                    startActivity(i);
                                }
                            } else if (ISEXPERT && !ISRECORD) {
                                i = new Intent(AtyOrderDetails.this, DoctorWriteCaseActivity.class);
                                i.putExtra("consultId", conId + "");
                                i.putExtra("otherId", patientId + "");
                                i.putExtra("otherName", patientName);
                                startActivity(i);
                            }
                            break;
                        case 99:
                            i = new Intent(AtyOrderDetails.this, AtyConsultOpinionD.class);
                            i.putExtra("conId", conId);
                            startActivity(i);
                            break;
                    }
                    break;
                } else {//专家
                    switch (type) {
                        case 50://要求补充
                            i = new Intent(AtyOrderDetails.this, AtyExpertOpinion.class);
                            i.putExtra("conId", conId);
                            i.putExtra("TYPE", 0);
                            startActivity(i);
                            break;
                        case 80://专家给意见
                        case 88:
                            i = new Intent(AtyOrderDetails.this, AtyExpertOpinion.class);
                            i.putExtra("TYPE", 1);
                            i.putExtra("conId", conId);
                            startActivity(i);
                            break;
                        case 99:
                            i = new Intent(AtyOrderDetails.this, AtyConsultOpinion.class);
                            i.putExtra("conId", conId);
                            startActivity(i);
                            break;
                    }
                }
                break;
            case R.id.evaluate:
                if ("0".equals(docPosition)) {
                    switch (type) {
                        case 30://发给专家
                        case 55:
                        case 85:
                            sendToExpert();
                            break;
                    }

                } else {
                    switch (type) {
                        case 50://专家拒绝接单
                            i = new Intent(AtyOrderDetails.this, AtyConsultReject.class);
                            i.putExtra("conId", conId + "");
                            startActivity(i);
                            break;
                        case 80:
                        case 88:
                            i = new Intent(AtyOrderDetails.this, AtyExpertOpinion.class);
                            i.putExtra("conId", conId);
                            i.putExtra("TYPE", 0);
                            startActivity(i);
                            break;
                    }
                }

                break;
            case R.id.accept://专家接受邀请
                setCon("40");
                break;
            case R.id.reject://专家拒绝邀请
                setCon("30");
                break;
//            case R.id.image_head_p:
//                if (!"".equals(patientIcon)) {
//                    i = new Intent(AtyOrderDetails.this, AtyPatientMassage.class);
//                    i.putExtra("PID", patient + "");
//                    i.putExtra("ORDER", 0);
//                    startActivity(i);
//                }
//                break;
//            case R.id.image_head_d:
//                if (docId != 0) {
//                    i = new Intent(AtyOrderDetails.this, AtyDoctorMassage.class);
//                    i.putExtra("id", docId + "");
//                    i.putExtra("type", 1);
//                    i.putExtra("ORDER", 0);
//                    startActivity(i);
//                }
//                break;
//            case R.id.image_head_e:
//                if (expId != 0) {
//                    i = new Intent(AtyOrderDetails.this, AtyDoctorMassage.class);
//                    i.putExtra("id", expId + "");
//                    i.putExtra("type", 0);
//                    i.putExtra("ORDER", 0);
//                    startActivity(i);
//                }
//                break;
        }
    }

    /**
     * 加载订单数据
     */

    private void loadData(int sptype, String id, final int loadType) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("CUSTID", id));
        ApiService.OKHttpConsultInfo(sptype, pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                loadCount = 1;
                if (loadType == 0) {
                    mPullToRefreshScrollView.setRefreshing();
                }
            }

            @Override
            public void onAfter() {
                super.onAfter();
                loadCount = 0;
                if (loadType == 0) {
                    mPullToRefreshScrollView.onRefreshComplete();
                } else {
                    if ("0".equals(docPosition)) {//医生
                        switch (type) {
                            case 20:
                            case 30:
                            case 50:
                            case 55:
                            case 60:
                            case 70:
                                jumpChat(patientId + "", patientName);
                                break;
                            case 80:
                            case 85:
                            case 88:
                            case 99:
                                doChat(conId + "", conName);
                                break;
                            case 90:
                            case 95:
                            case 222:
                            case 232:
                            case 243:
                            case 252:
                                ToastUtil.showShort("对不起,服务已取消,不能对话");
                                break;
                        }
                    } else {
                        switch (type) {
                            case 50:
                            case 55:
                                if (DoctorHelper.isSelf(expId + "")) {
                                    ToastUtil.showShort("对不起,您还没有接诊,不能参与对话");
                                } else {
                                    ToastUtil.showShort("对不起,主诊专家还没有接诊,不能参与对话");
                                }
                                break;
                            case 70:
                            case 80:
                            case 85:
                            case 88:
                            case 99:
                                doChat(conId + "", conName);
                                break;
                            case 90:
                            case 95:
                            case 222:
                            case 232:
                            case 243:
                            case 252:
                                ToastUtil.showShort("对不起,服务已取消,不能对话");
                                break;
                        }
                    }
                }
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    handleData(response);
                }
            }
        }, this);
    }

    /**
     * 发给专家
     */
    private void sendToExpert() {
        if (clickCount > 0) {
            return;
        }
        clickCount++;
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("OPTION", 6 + ""));
        pairs.add(new BasicNameValuePair("DOCTORID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.OKHttpSendToExpert(pairs, new MyApiCallback<String>(this) {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        ToastUtil.showShort(object.optString("message"));
                        if (object.optInt("code") == 1) {
                            loadData(Ptype, creDocId, 0);
                        } else {
                            clickCount = 0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    clickCount = 0;
                }
            }
        }, this);
    }

    /**
     * 专家接诊
     */
    private void accExpert() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", 7 + ""));
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("DOCTORID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.OKHttpAccept(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        ToastUtil.showShort(obj.optString("message"));
                        if (obj.optInt("code") == 1) {
                            loadData(Ptype, creDocId, 0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, this);
    }

    public void onEvent(MyEvent event) {
        if ("refresh".equals(event.what)) {
            if (!("0".equals(docPosition))) {
                Ptype = 1;
            } else {
                Ptype = 0;
            }
            loadData(Ptype, creDocId, 0);
        }
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        loadData(Ptype, creDocId, 0);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("BACK")) {
            int backMain = getIntent().getIntExtra("BACK", 0);
            if (backMain == 2) {
                Intent intent = new Intent(AtyOrderDetails.this, MyConsultationActivity.class);
                intent.putExtra("BACK", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    private void handleData(String response) {
        try {
            JSONObject oject = new JSONObject(response);
            if (oject.optInt("code") == 1) {
                TextView pName = (TextView) findViewById(R.id.tv_patientname);
                object = oject.getJSONObject("result");
                type = object.optInt("STATUS");
                inviteType = object.optString("inv_status");
                beforeSupply = object.optInt("SERVICE_BEFORE_SUPPLY");
                afterSupply = object.optInt("SERVICE_AFTER_SUPPLY");
                value = object.toString();
                officeId = object.optInt("OFFICE_ID");
                patientId = oject.getJSONObject("result").getJSONObject("PATIENT").optInt("PATIENTID");
                patientName = oject.getJSONObject("result").getJSONObject("PATIENT").optString("PATIENTNAME");

                if (HStringUtil.isEmpty(object.optString("PRICE"))) {
                    mPrice.setText("0");
                } else {
                    mPrice.setText(object.optString("PRICE"));
                }
                mTip.setText(object.optString("STATUSNAME"));
                conName = object.optString("CON_NAME");
                promoterType = object.optInt("PROMOTER_TYPE");
                groupId = object.optString("GROUP_ID");

                if (loadDataCount < 1) {
                    bundle.putString("result", value);
                    fragment.setArguments(bundle);// 将bundle数据加到Fragment中
                    beginTransaction.add(R.id.ll_case, fragment, "fragment");
                    beginTransaction.commitAllowingStateLoss();
                } else {
                    fragment.setResult(value);
                }
                loadDataCount++;

                if (oject.getJSONObject("result").has("PROCESS")){
                    int count = oject.getJSONObject("result").optJSONArray("PROCESS").length();
                    JSONArray array = oject.getJSONObject("result").getJSONArray("PROCESS");
                    for (int t = 0; t < count; t++) {
                        JSONObject object = array.getJSONObject(t);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("color", "" + object.optInt("color"));
                        map.put("name", object.optString("name"));
                        list.add(map);
                    }
                    tProcess1.setText(list.get(0).get("name"));
                    tProcess2.setText(list.get(1).get("name"));
                    tProcess3.setText(list.get(2).get("name"));
                    if ("0".equals(list.get(0).get("color"))) {
                        tProcess1.setTextColor(getResources().getColor(R.color.color_blue));
//                            iProcess4.setImageDrawable(getResources().getDrawable(R.drawable.arrow_gray));
                    } else if ("1".equals(list.get(0).get("color"))) {
                        tProcess1.setTextColor(getResources().getColor(R.color.red));
                    }
                    if ("0".equals(list.get(1).get("color"))) {
                        tProcess2.setTextColor(getResources().getColor(R.color.color_blue));
                        iProcess1.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                    } else if ("1".equals(list.get(1).get("color"))) {
                        tProcess2.setTextColor(getResources().getColor(R.color.red));
                        iProcess1.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                    }
                    if ("0".equals(list.get(2).get("color"))) {
                        tProcess3.setTextColor(getResources().getColor(R.color.color_blue));
                        iProcess2.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                    } else if ("1".equals(list.get(2).get("color"))) {
                        tProcess3.setTextColor(getResources().getColor(R.color.red));
                        iProcess2.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                    }
                    if (Ptype != 1) {
                        tProcess4.setText(list.get(3).get("name"));
                        if ("0".equals(list.get(3).get("color"))) {
                            tProcess4.setTextColor(getResources().getColor(R.color.color_blue));
                            iProcess3.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                        } else if ("1".equals(list.get(3).get("color"))) {
                            tProcess4.setTextColor(getResources().getColor(R.color.red));
                            iProcess3.setImageDrawable(getResources().getDrawable(R.drawable.arrow_green));
                        }
                    } else {
                        findViewById(R.id.tv_process4).setVisibility(View.GONE);
                        findViewById(R.id.iv_process3).setVisibility(View.GONE);
                    }
                }else {
                    findViewById(R.id.ll_consultprocess).setVisibility(View.GONE);
                }




                if ("".equals(object.optString("EXPERT"))) {
                    ISEXPERT = false;
                } else {
                    ISEXPERT = true;
                }
                //被邀请的专家信息
                if (!HStringUtil.isEmpty(object.optString("inviteExpert"))) {
                    mInvitedExpert = object.optString("inviteExpert");
                }
                //会诊相关人物信息
                mList.clear();
                //患者
                if (!HStringUtil.isEmpty(object.optString("PATIENT"))) {
                    patient = object.getJSONObject("PATIENT").optInt("PATIENTID");
                    String patientName = object.getJSONObject("PATIENT").optString("PATIENTNAME");
                    String patientIcon = object.getJSONObject("PATIENT").optString("PATIENTICON");
                    JSONObject patientObj = new JSONObject();
                    patientObj.put("id", patient + "");
                    patientObj.put("name", patientName);
                    patientObj.put("icon", patientIcon);
                    patientObj.put(Constant.EXPERT_STATE, ExpertStatus.NO_STATE);
                    mList.add(patientObj);
                }
                //医生
                if (!HStringUtil.isEmpty(object.optString("DOCTOR"))) {
                    docId = object.getJSONObject("DOCTOR").optInt("DOCTORID");
                    String doctorName = object.getJSONObject("DOCTOR").optString("DOCTORNAME");
                    String doctorIcon = object.getJSONObject("DOCTOR").optString("DOCTORICON");
                    JSONObject doctorObj = new JSONObject();
                    doctorObj.put("id", docId + "");
                    doctorObj.put("name", doctorName);
                    doctorObj.put("icon", doctorIcon);
                    doctorObj.put(Constant.EXPERT_STATE, ExpertStatus.NO_STATE);
                    mList.add(doctorObj);
                }
                //专家
                if (!HStringUtil.isEmpty(object.optString("EXPERT"))) {
                    expId = object.getJSONObject("EXPERT").optInt("EXPERTID");
                    String expertName = object.getJSONObject("EXPERT").optString("EXPERTNAME");
                    String expertIcon = object.getJSONObject("EXPERT").optString("EXPERTICON");
                    JSONObject expertObj = new JSONObject();
                    expertObj.put("id", expId + "");
                    expertObj.put("name", expertName);
                    expertObj.put("icon", expertIcon);
                    expertObj.put(Constant.EXPERT_STATE, ExpertStatus.NO_STATE);
                    mList.add(expertObj);
                }

                JSONArray inviteArray = new JSONArray();

                inviteArray = object.getJSONArray("inviteExpert");
                for (int i = 0; i < inviteArray.length(); i++) {
                    JSONObject obj = inviteArray.getJSONObject(i);
                    JSONObject newObj = new JSONObject();
                    newObj.put("id", obj.optString("INV_DOCTOR_ID"));
                    newObj.put("name", obj.optString("DOCTOR_REAL_NAME"));
                    newObj.put("icon", obj.optString("BIG_ICON_BACKGROUND"));
                    newObj.put(Constant.EXPERT_STATE, obj.optString("INV_STATUS"));
                    mList.add(newObj);
                }
                if (!"0".equals(docPosition) && creDocId.equals(expId + "") && type > 55) {//主诊专家
                    mAdapter.onBoundData(mList, true);
                } else {//被邀请专家或者医生
                    mAdapter.onBoundData(mList, false);
                }
                if ("".equals(object.optString("RECORD"))) {
                    ISRECORD = false;
                } else {
                    ISRECORD = true;

                }
                findViewById(R.id.ll_done).setVisibility(View.GONE);
                mOpinion.setVisibility(View.GONE);
                mOutPatient.setVisibility(View.GONE);
                mEvaluate.setVisibility(View.GONE);
                if ("0".equals(docPosition)) {
                    switch (type) {
                        case 20:
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOutPatient.setVisibility(View.VISIBLE);
                            if ((!ISEXPERT) && (!ISRECORD)) {
                                mOutPatient.setText("选择专家");
                            } else if (ISEXPERT && (!ISRECORD)) {
                                mOutPatient.setText("填写病历");
                            } else if (!ISEXPERT && ISRECORD) {
                                mOutPatient.setText("重选专家");
                            }
                            break;
                        case 30:
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOpinion.setVisibility(View.VISIBLE);
                            mEvaluate.setVisibility(View.VISIBLE);
                            mOpinion.setText("修改病历");
                            mEvaluate.setText("发给专家");
                            break;
                        case 55:
                        case 85:
                            if (!"".equals(object.optString("RECORDSUPPLY"))) {
                                findViewById(R.id.rl_casesupply).setVisibility(View.VISIBLE);
                            }
                            mCaseSupply.setText(object.optString("RECORDSUPPLY"));
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOpinion.setVisibility(View.VISIBLE);
                            mEvaluate.setVisibility(View.VISIBLE);
                            mOpinion.setText("补充病历");
                            mEvaluate.setText("发给专家");
                            break;
                        case 60:
                            if (promoterType != 10) {
                                findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                                mOpinion.setVisibility(View.VISIBLE);
                                mOpinion.setText("重选专家");
                            }
                            break;
                        case 99:
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOutPatient.setVisibility(View.VISIBLE);
                            mOutPatient.setText("查看意见");
                            break;
                    }
                }
                if (!"0".equals(docPosition) && DoctorHelper.isSelf(expId + "")) {
                    switch (type) {
                        case 50:
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOpinion.setVisibility(View.VISIBLE);
                            mEvaluate.setVisibility(View.VISIBLE);
                            mOpinion.setText("同意");
                            mEvaluate.setText("拒绝");
                            if (beforeSupply == 0) {
                                mOutPatient.setVisibility(View.VISIBLE);
                                mOutPatient.setText("要求补充");
                            }
                            break;
                        case 55:
                            if (!"".equals(object.optString("RECORDSUPPLY"))) {
                                findViewById(R.id.rl_casesupply).setVisibility(View.VISIBLE);
                            }
                            mCaseSupply.setText(object.optString("RECORDSUPPLY"));
                            findViewById(R.id.ll_done).setVisibility(View.GONE);
                            mOpinion.setVisibility(View.GONE);
                            mEvaluate.setVisibility(View.GONE);
                            mOutPatient.setVisibility(View.GONE);
                            break;
                        case 80:
                        case 88:
                            findViewById(R.id.rl_casesupply).setVisibility(View.GONE);
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            //主诊专家
                            if (afterSupply == 0) {
                                mOpinion.setVisibility(View.VISIBLE);
                                mOpinion.setText("给意见");
                                mEvaluate.setVisibility(View.VISIBLE);
                                mEvaluate.setText("要求补充");
                            } else {
                                mOutPatient.setVisibility(View.VISIBLE);
                                mOutPatient.setText("给意见");
                            }
                            break;
                        case 85:
                            findViewById(R.id.ll_done).setVisibility(View.GONE);
                            if (!"".equals(object.optString("RECORDSUPPLY"))) {
                                findViewById(R.id.rl_casesupply).setVisibility(View.VISIBLE);
                            }
                            mCaseSupply.setText(object.optString("RECORDSUPPLY"));
                            break;
                        case 99:
                            findViewById(R.id.ll_done).setVisibility(View.GONE);
                            mOpinion.setVisibility(View.GONE);
                            mOutPatient.setVisibility(View.GONE);
                            mEvaluate.setVisibility(View.GONE);
                            findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                            mOutPatient.setVisibility(View.VISIBLE);
                            mOutPatient.setText("查看意见并解答疑难");
                            break;
                    }
                }
                if (!"0".equals(docPosition) && !DoctorHelper.isSelf(expId + "") && "10".equals(inviteType)) {
                    findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_price).setVisibility(View.GONE);
                    findViewById(R.id.ll_consultprocess).setVisibility(View.GONE);
                    findViewById(R.id.tv_consul).setVisibility(View.GONE);
                    mAccept.setVisibility(View.VISIBLE);
                    mAccept.setText("接受");
                    mReject.setVisibility(View.VISIBLE);
                    mReject.setText("拒绝");
                }
            } else {
                ToastUtil.showShort(oject.optString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群聊天
     */
    public void doChat(String conId, String conName) {
        Intent intent = new Intent();
        if (type > 80) {
            intent.putExtra(Constant.Chat.CONSULTATION_TYPE, "1");
        }
        intent.putExtra(Constant.Chat.CONSULTATION_ID, conId);
        intent.putExtra(Constant.Chat.GROUP_ID, groupId);
        intent.putExtra(Constant.Chat.CONSULTATION_NAME, conName);
        intent.putExtra(Constant.Chat.OBJECT_TYPE, ObjectType.CONSULT);
        intent.setClass(AtyOrderDetails.this, ChatActivity.class);
        startActivity(intent);
    }

    /**
     * 获取会诊相关人id
     *
     * @return
     */
    private String getAllIds() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < mAdapter.mData.size(); i++) {
            if (!ExpertStatus.INVITING_STATE.equals(mAdapter.mData.get(i).optString(Constant.EXPERT_STATE))) {
                if (i == 0) {
                    buffer.append(mAdapter.mData.get(i).optString("id"));
                } else {
                    buffer.append("," + mAdapter.mData.get(i).optString("id"));
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 私聊
     */
    private void jumpChat(String otherId, String otherName) {
        Intent intent = new Intent();
        intent.putExtra(Constant.Chat.CONSULTATION_ID, conId + "");
        intent.putExtra(Constant.Chat.SINGLE_ID, otherId);
        intent.putExtra(Constant.Chat.SINGLE_NAME, otherName);
        intent.putExtra(Constant.Chat.OBJECT_TYPE, ObjectType.CONSULT);
        intent.setClass(AtyOrderDetails.this, ChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!"0".equals(docPosition) && position == parent.getChildCount() - 1) {
            Intent intent = new Intent(AtyOrderDetails.this, InviteKnowActivity.class);
            intent.putExtra(IntentConstant.ConsultationId, conId + "");
            intent.putExtra(IntentConstant.ConsultationName, conName);
            intent.putExtra(IntentConstant.ConsultationMainExpert, expId + "");
            intent.putExtra(IntentConstant.InvitedExpert, mInvitedExpert);
            startActivity(intent);
        } else if (!LoginBusiness.getInstance().getLoginEntity().getId().equals(mAdapter.mData.get(position).optString("id"))) {
            String talkName = mAdapter.mData.get(position).optString("name");
            String talkId = mAdapter.mData.get(position).optString("id");
            jumpChat(talkId, talkName);
        }
    }


    /**
     * 会诊邀请状态改变
     *
     * @param status 30 拒绝  40 接受邀请
     */
    private void setCon(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateInviteStatus");
        map.put("inv_status", status);
        map.put("consultation_id", conId + "");
        map.put("group_id", groupId + "");
        map.put("invite_doctor_id", DoctorHelper.getId());

        ApiService.OKHttpConInvited(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    if (!HStringUtil.isEmpty(response)) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            ToastUtil.showShort(obj.optString("message"));
                            if (HttpResult.SUCCESS.equals(obj.optString("code"))) {
                                loadData(Ptype, creDocId, 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }, this);
    }
}
