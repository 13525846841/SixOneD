package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import okhttp3.Request;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseShowFragment;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEKL on 15/9/23.
 * 会诊记录详情_
 */
public class AtyOrderRecord extends BaseActivity implements View.OnClickListener, SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener, PullToRefreshBase.OnRefreshListener<ScrollView> {
    private ImageView mImageHeadP, mImageHeadD, mImageHeadE;
    private TextView mPatientName,mDoctorName,mExpertName;
    private int conId;
    private String conName;
    private String creDocId;
    private JSONObject object;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOption;//异步加载图片的操作
    private CaseShowFragment fragment;
    private Bundle bundle;
    private String value;
    private String mPName,mDName,mEName;
    private FragmentTransaction beginTransaction;
    private int loadDataCount;
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_orderdetail);
        initView();
        loadData(creDocId);
    }
    @SuppressWarnings("deprecation")
    private void initView() {
        conId = getIntent().getIntExtra("CONID", 0);
        if (getIntent().hasExtra("PERSONID")) {
            creDocId = getIntent().getStringExtra("PERSONID");
        } else {
            creDocId = LoginBusiness.getInstance().getLoginEntity().getId() + "";
        }
        mImageLoader = ImageLoader.getInstance();
        mOption = DefaultConfigurationFactory.createHeadDisplayImageOptions(this);
        findViewById(R.id.ll_price).setVisibility(View.GONE);
        findViewById(R.id.btn_talk).setVisibility(View.GONE);
        findViewById(R.id.ll_consultprocess).setVisibility(View.GONE);
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        findViewById(R.id.btn_talk).setOnClickListener(this);
        fragment = new CaseShowFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        beginTransaction = fragmentManager.beginTransaction();
        bundle = new Bundle();
        Button mOutPatient = (Button) findViewById(R.id.outpatient);
        mOutPatient.setOnClickListener(this);
        mImageHeadP = (ImageView) findViewById(R.id.image_head_p);
        mImageHeadD = (ImageView) findViewById(R.id.image_head_d);
        mImageHeadE = (ImageView) findViewById(R.id.image_head_e);
        mPatientName = (TextView) findViewById(R.id.tv_patientname);//患者姓名
        mDoctorName = (TextView) findViewById(R.id.tv_doctorName);//医生姓名
        mExpertName = (TextView) findViewById(R.id.tv_expertName);//专家姓名
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
        mOutPatient.setVisibility(View.VISIBLE);
        mOutPatient.setText("会诊意见");
    }

    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.outpatient:
                i = new Intent(AtyOrderRecord.this, AtyConsultOpinionD.class);
                i.putExtra("conId", conId);
                startActivity(i);
                break;
        }
    }

    /**
     * 加载订单数据
     */
    private void loadData(String id) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("CUSTID", id));
        ApiService.OKHttpConsultInfoP(pairs, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullToRefreshScrollView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject oject = new JSONObject(response);
                        if (oject.optInt("code") == 1) {
                            object = oject.getJSONObject("result");
                            value = object.toString();
                            conName = object.optString("CON_NAME");
                            titleTextV.setText(conName);
                            if (loadDataCount < 1) {
                                bundle.putString("result", value);
                                fragment.setArguments(bundle);// 将bundle数据加到Fragment中
                                beginTransaction.add(R.id.ll_case, fragment, "fragment");
                                beginTransaction.commitAllowingStateLoss();
                            } else {
                                fragment.setResult(value);
                            }
                            loadDataCount++;
                            //患者头像
                            if (!"".equals(object.optString("PATIENT"))) {
                                mImageLoader.displayImage(object.getJSONObject("PATIENT").optString("PATIENTICON"), mImageHeadP, mOption);
                                mPName=object.getJSONObject("PATIENT").optString("PATIENTNAME");
                                if ("null".equals(mPName)||("".equals(mPName))){
                                    mPatientName.setText("患者");
                                }else {
                                    mPatientName.setText(mPName);
                                }
                            }
                            //医生头像
                            if (!"".equals(object.optString("DOCTOR"))) {
                                mImageLoader.displayImage(object.getJSONObject("DOCTOR").optString("DOCTORICON"), mImageHeadD, mOption);
                                mDName=object.getJSONObject("DOCTOR").optString("DOCTORNAME");
                                if ("null".equals(mDName)||("".equals(mDName))){
                                    mDoctorName.setText("会诊医生");
                                }else {
                                    mDoctorName.setText(mDName);
                                }
                            }
                            //专家头像
                            if (!"".equals(object.optString("EXPERT"))) {
                                mImageLoader.displayImage(object.getJSONObject("EXPERT").optString("EXPERTICON"), mImageHeadE, mOption);
                                mEName=object.getJSONObject("EXPERT").optString("EXPERTNAME");
                                if ("null".equals(mEName)||("".equals(mEName))){
                                    mExpertName.setText("会诊专家");
                                }else {
                                    mExpertName.setText(mEName);
                                }
                            }
                        } else {
                            ToastUtil.showShort(oject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {

    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        loadData(creDocId);
    }
}
