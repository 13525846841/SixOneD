package com.yksj.consultation.sonDoc.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.yksj.consultation.adapter.SelectExpertListAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.DoctorWriteCaseActivity;
import com.yksj.consultation.sonDoc.consultation.member.FlowMassageActivity;
import com.yksj.consultation.sonDoc.friend.ConsultMessageActivity;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.DoctorSimpleBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 根据地区医院二级菜单选择会诊专家
 *
 * Created by lmk on 2015/9/14.
 */
public class SelectExpertMainUI extends BaseActivity implements View.OnClickListener,SelectExpertListAdapter.OnClickSelectListener,
        NavigateFragment.SelectorResultListener,PullToRefreshBase.OnRefreshListener2<ListView> ,AdapterView.OnItemClickListener{

    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private SelectExpertListAdapter mAdapter;
    private LinearLayout navLayout;
    private FragmentManager manager;
    private NavigateFragment navFragment;

    private Bundle bundle;

    private String areaCode="",unitCode="",officeCode="11",pid="";
    private int goalType=0;//来这个界面的目的类型0帮助患者找专家2填完病历重选专家   1医生主动创建会诊帮患者找专家
    private String consultId;
    private String officeName="";

    //下面是临时的
    ImageView icon;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.select_expert_main_ui);
        initView();
        if (getIntent().hasExtra("OFFICECODE")){
            officeCode=getIntent().getStringExtra("OFFICECODE");
        }
        if (getIntent().hasExtra("OFFICENAME")){
            officeName=getIntent().getStringExtra("OFFICENAME");
        }
        consultId=getIntent().getStringExtra("consultId");
        goalType=getIntent().getIntExtra("goalType", 0);
//		 AnimationUtils.startGuiPager(this, getClass().getAccounts());
        manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction = manager.beginTransaction();
        navFragment=new NavigateFragment();
        navFragment.setSelectorListener(this);
        transaction.add(R.id.navigationbar_layout, navFragment);
        transaction.commit();
    }

    private void initView() {
        initializeTitle();
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setBackgroundResource(R.drawable.ig_seach);
        titleTextV.setText("会诊专家");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);

//
//        icon= (ImageView) findViewById(R.id.select_expert_list_item_headicon);
//        findViewById(R.id.select_expert_list_item_select).setOnClickListener(this);
//        icon.setOnClickListener(this);
        if(getIntent().hasExtra("PID")){
            pid=getIntent().getStringExtra("PID");
        }

        mPullRefreshListView=(PullToRefreshListView) findViewById(R.id.select_expert_list);
        mListView=mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setOnRefreshListener(this);
        mAdapter=new SelectExpertListAdapter(SelectExpertMainUI.this,pid);
        mAdapter.setSelectListener(this);
////		mListView.addFooterView(getLayoutInflater().inflate(R.layout.list_line_layout, null),null,false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();

                break;
            case R.id.select_expert_list_item_headicon:
//                intent=new Intent(this, DoctorClinicMainActivity.class);
                intent=new Intent(this, AtyDoctorMassage.class);
                intent.putExtra("id","20");
                startActivity(intent);

                break;
            case R.id.title_right2://搜索
                intent=new Intent(SelectExpertMainUI.this,SearchExpertActivity.class);
                intent.putExtra("OFFICECODE", officeCode);
                intent.putExtra("OFFICENAME", officeName);
                intent.putExtra("goalType", goalType);
                intent.putExtra("PID",pid);
                intent.putExtra("consultId",consultId);
                startActivity(intent);
                break;
            case R.id.select_expert_list_item_select://搜索
                intent=new Intent(SelectExpertMainUI.this,FlowMassageActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void goNotifyLoadData(String areaCode, String unitCode) {
        pageSize=1;
        this.areaCode=areaCode;
        this.unitCode=unitCode;
        loadData(areaCode,unitCode);

    }


    private int pageSize=1;
    /**
     *加载数据
     * @param unitCode 医院
     * @param areaCode 地区编码
     */
    private void loadData(String areaCode,String unitCode){

        List<BasicNameValuePair> valuePairs=new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE","findExpertByOfficeAndUnit"));
        valuePairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        valuePairs.add(new BasicNameValuePair("PAGENUM","20"));
        valuePairs.add(new BasicNameValuePair("ISConsultation","1"));
        valuePairs.add(new BasicNameValuePair("PAGESIZE",""+pageSize));
        valuePairs.add(new BasicNameValuePair("UPPER_OFFICE_ID",officeCode));
        valuePairs.add(new BasicNameValuePair("UNITCODE",unitCode));
        valuePairs.add(new BasicNameValuePair("AREACODE",areaCode));


        //192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?
        // //TYPE=findExpertByOfficeAndUnit&UPPER_OFFICE_ID=&UNITCODE=&UNITCODE=&CONSULTATION_CENTER_ID=&PAGESIZE=&PAGENUM=
//        RequestParams params=new RequestParams();
//        params.put("TYPE","findExpertByOfficeAndUnit");
//        params.put("UPPER_OFFICE_ID","11");
//        params.put("UNITCODE","");
//        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
//        params.put("PAGESIZE","1");
//        params.put("PAGENUM","20");
        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb= com.alibaba.fastjson.JSONObject.parseObject(response,BaseBean.class);
                if("1".equals(bb.code)) {
                    List<DoctorSimpleBean> list = JSON.parseArray(bb.result, DoctorSimpleBean.class);
//                ArrayList<DoctorSimpleBean> list= dld.result;
                    if (pageSize == 1) {//第一次加载
                        if (list.size() == 0) {
                            findViewById(R.id.load_data_is_null).setVisibility(View.VISIBLE);
                            mPullRefreshListView.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.load_data_is_null).setVisibility(View.GONE);
                            mPullRefreshListView.setVisibility(View.VISIBLE);
                            mAdapter.removeAll();
                            mAdapter.addAll(list);
                        }
                    } else {
                        if (list.size() != 0) {//加载出了数据
                            mAdapter.addAll(list);
                        } else {
                            ToastUtil.showShort("没有更多了");
                        }
                    }
                    pageSize++;
                }else{
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
        },this);

       /* ApiService.doGetConsultationInfoSet(params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                DoctorListData dld= com.alibaba.fastjson.JSONObject.parseObject(response,DoctorListData.class);
                ArrayList<DoctorSimpleBean> list= dld.result;
                if (list != null) {
                    if (page == 1)//第一次加载
                        mAdapter.removeAll();
                    if (list.size() != 0) {//加载出了数据
                        if (list.size() == 1) {//只有导医护士一个人
                            ToastUtil.showShort(getResources().getString(R.string.no_search_result));
                        }
                        if (isCon)
                            conPageSize++;//如果是按条件查询,将按条件查询的页数加1
                        mAdapter.addAll(list);
                    }
                } else if (response != null && response instanceof String) {
                    ToastUtil.showShort(dld.message);
                }
            }

            @Override
            public void onStart() {
                mPullRefreshListView.setRefreshing();
                super.onStart();
            }

            @Override
            public void onFinish() {
                mPullRefreshListView.onRefreshComplete();
                super.onFinish();
            }
        });*/
    }

    //下拉刷新
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageSize=1;
        loadData(areaCode,unitCode);
    }

    //上拉加载
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadData(areaCode,unitCode);

    }


    @Override
    public void onClickSelect(final DoctorSimpleBean dsb) {
        if (goalType==0||goalType==2){
            DoubleBtnFragmentDialog.show(getSupportFragmentManager(), "提示", "您确定帮患者选择专家" + dsb.DOCTOR_REAL_NAME + "吗",
                    "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {

                            selectHim(dsb);
                        }
                    });
        }else if(goalType==1){

            Intent intent = new Intent(SelectExpertMainUI.this, ConsultMessageActivity.class);
            intent.putExtra("data", dsb);
            intent.putExtra("OFFICECODE", officeCode);
            intent.putExtra("OFFICENAME", officeName);
            intent.putExtra("PID",pid);
            startActivity(intent);
        }

    }


    private void selectHim(DoctorSimpleBean dsb){
        ///DuoMeiHealth/ConsultationInfoSet?TYPE=reSelectedExpert&CUSTOMERID=&CONSULTATIONID=
        List<BasicNameValuePair> valuePairs=new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE","reSelectedExpert"));
        valuePairs.add(new BasicNameValuePair("CUSTOMERID", dsb.CUSTOMER_ID+""));
        valuePairs.add(new BasicNameValuePair("CONSULTATIONID",consultId));
        valuePairs.add(new BasicNameValuePair("SERVICE_PRICE",""+dsb.SERVICE_PRICE));
        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb=JSONObject.parseObject(response,BaseBean.class);
                if ("1".equals(bb.code)){
                    EventBus.getDefault().post(new MyEvent("refresh",2));
                    if (goalType==0){
                        SingleBtnFragmentDialog.showSinglebtn(SelectExpertMainUI.this, "您已为患者选择专家,现在请为患者填写病历吧。", "填写病历", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                            @Override
                            public void onClickSureHander() {
                                Intent intent = new Intent(SelectExpertMainUI.this, DoctorWriteCaseActivity.class);
                                intent.putExtra("consultId", consultId);
                                startActivity(intent);
                            }
                        }).show();
                    }else {
                        SelectExpertMainUI.this.finish();
                    }
                }else
                    ToastUtil.showShort(SelectExpertMainUI.this,bb.message);
            }
        }, this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SelectExpertMainUI.this, AtyDoctorMassage.class);
        intent.putExtra("id", mAdapter.datas.get(position-1).CUSTOMER_ID+"");
        intent.putExtra("type", 0);
        intent.putExtra("consultId", consultId);
        intent.putExtra("OFFICECODE", officeCode);
        intent.putExtra("OFFICENAME", officeName);
        intent.putExtra("goalType", goalType);
        if (goalType==1)
            intent.putExtra("PID",pid);
        startActivity(intent);
    }



}
