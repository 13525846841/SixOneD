package com.yksj.consultation.sonDoc.dossier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.DiscussCaseAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseDiscussDetailsActivity;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.CaseBean;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 病历讨论搜索结果
 * Created by zheng on 2015/7/21.
 */
public class AtyDossierSearchResult extends BaseActivity implements View.OnClickListener ,PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private int pageSize=1;
    private DiscussCaseAdapter mAdapter;
    private View nullView;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_dossier_search_result);
        initView();
    }
    private void initView() {
        initializeTitle();
        titleTextV.setText("搜索结果");
        titleLeftBtn.setOnClickListener(this);
        mPullToRefreshListView= (PullToRefreshListView) findViewById(R.id.search_result);
        nullView=findViewById(R.id.dossier_null_view);
        mListView=mPullToRefreshListView.getRefreshableView();
        mAdapter=new DiscussCaseAdapter(AtyDossierSearchResult.this);
        mListView.setAdapter(mAdapter);
        String searchText=getIntent().getStringExtra("SEARCHTEXT");
        initSearchData(searchText);
        mPullToRefreshListView.setOnRefreshListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AtyDossierSearchResult.this, CaseDiscussDetailsActivity.class);
                intent.putExtra("recordId", mAdapter.datas.get(position - 1).MEDICAL_RECORD_ID + "");
                startActivityForResult(intent,201);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }
    private void initSearchData(String searchStr){
//DuoMeiHealth/ConsultationInfoSet?TYPE=medicalCaseDiscussionByName&PAGESIZE=&PAGENUM=&CONSULTATION_CENTER_ID=&NAME=
        List<BasicNameValuePair> pairs=new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE","medicalCaseDiscussionByName"));
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        pairs.add(new BasicNameValuePair("PAGESIZE", pageSize+""));
        pairs.add(new BasicNameValuePair("PAGENUM", "20"));
        pairs.add(new BasicNameValuePair("NAME", searchStr));
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {

                    List<CaseBean> list = JSON.parseArray(bb.result, CaseBean.class);
                    if (list != null && list.size() > 0) {
                        mAdapter.addAll(list);
                        pageSize++;
                    } else {
                        ToastUtil.showShort("未加载到数据");
                    }


                } else if (response != null && response instanceof String) {
                    ToastUtil.showShort(bb.message);
                }
            }

            @Override
            public void onBefore(Request request) {

                mPullToRefreshListView.setRefreshing();
                super.onBefore(request);
            }

            @Override
            public void onAfter() {
                mPullToRefreshListView.onRefreshComplete();
                super.onAfter();
            }
        }, this);

    }
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageSize=1;
        mAdapter.removeAll();
        initSearchData(getIntent().getStringExtra("SEARCHTEXT"));
    }
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initSearchData(getIntent().getStringExtra("SEARCHTEXT"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==201){
                pageSize=1;
                mAdapter.removeAll();
                initSearchData(getIntent().getStringExtra("SEARCHTEXT"));
            }
        }
    }
}
