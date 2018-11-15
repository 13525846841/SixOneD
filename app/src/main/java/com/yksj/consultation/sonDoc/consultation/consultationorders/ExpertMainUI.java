package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.SelectExpertListInviteAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.IntentConstant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.home.InviteAlertDialog;
import com.yksj.consultation.sonDoc.listener.DialogOnClickListener;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.DoctorSimpleBeanInvite;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;


/**
 * 专家会诊可邀请的专家
 * <p>
 * Created by lmk on 2015/9/14.
 */
public class ExpertMainUI extends BaseActivity implements View.OnClickListener,
        ExpertNavigateFragment.SelectorResultListener, PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {

    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private SelectExpertListInviteAdapter mAdapter;
    private LinearLayout navLayout;
    private FragmentManager manager;
    private ExpertNavigateFragment navFragment;
    private int conPageSize = 1;//当前的页数
    private Bundle bundle;
    private String areaCode = "", unitCode = "", officeCode = "", consultId;
    private String conName = "";//会诊名称
    private int goalType = 0;//0为默认 1找医生 2为患者重选专家
    //下面是临时的
    ImageView icon;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.select_expert_main_ui_invite);
        initView();
        goalType = getIntent().getIntExtra("goalType", 0);
        consultId = getIntent().getStringExtra("consultId");
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction = manager.beginTransaction();
        navFragment = new ExpertNavigateFragment();
        navFragment.setSelectorListener(this);
        transaction.add(R.id.navigationbar_layout, navFragment);
        transaction.commit();
    }

    private void initView() {
        initializeTitle();
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleTextV.setText("会诊专家");
        titleRightBtn2.setText("确定");
        if (getIntent().hasExtra(IntentConstant.ConsultationId)) {
            conId = getIntent().getStringExtra(IntentConstant.ConsultationId);
        }
        if (getIntent().hasExtra(IntentConstant.ConsultationName)) {
            conName = getIntent().getStringExtra(IntentConstant.ConsultationName);
        }
        if (getIntent().hasExtra(IntentConstant.ConsultationMainExpert)) {
            expId = getIntent().getStringExtra(IntentConstant.ConsultationMainExpert);
        }
        if (getIntent().hasExtra(IntentConstant.InvitedExpert)) {
            inviteInfo = getIntent().getStringExtra(IntentConstant.InvitedExpert);
        }
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.select_expert_list);
        mListView = mPullRefreshListView.getRefreshableView();
        mListView.setOnItemClickListener(this);
        mPullRefreshListView.setOnRefreshListener(this);
        mAdapter = new SelectExpertListInviteAdapter(ExpertMainUI.this, 1);
        mAdapter.setFromType(1);
////	mListView.addFooterView(getLayoutInflater().inflate(R.layout.list_line_layout, null),null,false);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
//            case R.id.select_expert_list_item_headicon:
//                intent=new Intent(this, AtyDoctorMassage.class);
//                intent.putExtra("id","20");
//                startActivity(intent);
//                break;
            case R.id.title_right2://确定邀请
                confirmInvite();
                break;

        }
    }

    @Override
    public void goNotifyLoadData(String areaCode, String unitCode, String officeCode) {
        conPageSize = 1;
        this.areaCode = areaCode;
        this.unitCode = unitCode;
        this.officeCode = officeCode;
        loadData(areaCode, unitCode);
    }

    /**
     * 加载数据
     *
     * @param unitCode 医院
     * @param areaCode 地区编码
     */
    private void loadData(String areaCode, String unitCode) {

        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE", "findExpertByOfficeAndUnit"));
        valuePairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        valuePairs.add(new BasicNameValuePair("PAGENUM", "20"));
        valuePairs.add(new BasicNameValuePair("PAGESIZE", "" + conPageSize));
        valuePairs.add(new BasicNameValuePair("UPPER_OFFICE_ID", officeCode));
        valuePairs.add(new BasicNameValuePair("UNITCODE", unitCode));
        valuePairs.add(new BasicNameValuePair("AREACODE", areaCode));

        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    List<DoctorSimpleBeanInvite> list = JSON.parseArray(bb.result, DoctorSimpleBeanInvite.class);
//                ArrayList<DoctorSimpleBean> list= dld.result;
                    if (list != null) {
                        if (conPageSize == 1)//第一次加载
                        {
                            mAdapter.removeAll();
                        }
                        if (list.size() != 0) {//加载出了数据
                            mAdapter.addAll(checkExpert(list));
                        } else {
                            ToastUtil.showShort("没有更多了");
                        }
                        conPageSize++;
                    }
                } else {
                    ToastUtil.showShort(bb.message);
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullRefreshListView.onRefreshComplete();
                super.onAfter();
            }
        }, this);
    }

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        conPageSize = 1;
        loadData(areaCode, unitCode);
    }

    //上拉加载
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadData(areaCode, unitCode);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(ExpertMainUI.this, AtyDoctorMassage.class);
//        intent.putExtra("id", mAdapter.datas.get(position-1).CUSTOMER_ID+"");
//        intent.putExtra("type", 0);
//        intent.putExtra("CLINIC", "CLINIC");
//        if(goalType==2){
//            intent.putExtra("consultId", consultId);
//        }
//        intent.putExtra("OFFICECODE", officeCode);
//        startActivity(intent);

//        Intent intent = new Intent(ExpertMainUI.this, DoctorStudioActivity.class);
//       //intent.putExtra("DOCTOR_ID",mAdapter.datas.get(position-1).getCUSTOMER_ID());
//        intent.putExtra("DOCTOR_ID",mAdapter.datas.get(position-1).CUSTOMER_ID+"");
//        startActivity(intent);
    }


    /**
     * 获取被邀请专家的id
     */
    private List<String> getExpertIds() {
        List<String> expertIds = new ArrayList<>();
        int count = mAdapter.datas.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                DoctorSimpleBeanInvite bean = mAdapter.datas.get(i);
                if (bean.is_Checked()) {
                    expertIds.add(bean.getCUSTOMER_ID());
                }
            }
        }
        return expertIds;
    }

    /**
     * 确认邀请
     */
    private void confirmInvite() {
        List<String> mIds = getExpertIds();
        if (mIds.size() < 1) {
            ToastUtil.showShort("您还没有选择");
            return;
        }
        if (HStringUtil.isEmpty(conId) || HStringUtil.isEmpty(expId)) {
            ToastUtil.showShort("请尝试刷新会诊订单");
            return;
        }
        org.json.JSONObject object = new org.json.JSONObject();
        JSONArray array = new JSONArray();

        for (int i = 0; i < mIds.size(); i++) {
            org.json.JSONObject obj = new org.json.JSONObject();
            try {
                obj.put("consultation_id", conId);
                obj.put("main_doctor_id", expId);
                obj.put("consultation_name", conName);
                obj.put("inv_doctor_id", mIds.get(i));
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {
            object.put("doctorList", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<>();
        map.put("consultation_id", conId);
        map.put("main_doctor_id", expId);
        map.put("jsonArr", object.toString());
        map.put("op", "inviteExpert");
        ApiService.OKHttpAddTools(map, new MyApiCallback<JSONObject>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    if ("1".equals(response.getString("code"))) {

                        AlertDialog();
                        dialog.show();
                    } else {
                        ToastUtil.showShort(response.getString("message"));
                    }
                }
            }
        }, this);
    }

    private InviteAlertDialog dialog;

    /**
     * 弹框
     */
    private void AlertDialog() {
        dialog = new InviteAlertDialog.Builder(ExpertMainUI.this)
                .setTitleText("已经向专家发起邀请成功，请耐心等候专家接受。")
                .setHeight(0.21f)  //屏幕高度*0.21
                .setWidth(0.7f)  //屏幕宽度*0.7
                .setCanceledOnTouchOutside(false)
                .setOnclickListener(new DialogOnClickListener() {
                    @Override
                    public void clickButton(View view) {
                        ExpertMainUI.this.setResult(RESULT_OK);
                        ExpertMainUI.this.finish();
                    }
                })
                .build();
    }

    private String inviteInfo = "";//被邀请专家信息
    private String conId = "";//会诊id
    private String expId = "";//主诊专家id

    /**
     * 检查专家是否被邀约
     */
    private List<DoctorSimpleBeanInvite> checkExpert(List<DoctorSimpleBeanInvite> mList) {
        if (!HStringUtil.isEmpty(inviteInfo)) {
            try {
                JSONArray array = new JSONArray(inviteInfo);
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject obj = array.getJSONObject(i);
                    for (int j = 0; j < mList.size(); j++) {
                        if (obj.optString("INV_DOCTOR_ID").equals(mList.get(j).getCUSTOMER_ID())) {
                            mList.get(j).setExpert_Invited_Stated(obj.optString("INV_STATUS"));
                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;
    }

}
