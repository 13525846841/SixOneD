package com.yksj.consultation.sonDoc.dossier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.DiscussCaseAdapter;
import com.yksj.consultation.adapter.FamDocPopAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseDiscussDetailsActivity;
import com.yksj.consultation.sonDoc.casehistory.ExpertUploadCaseActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.CaseBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 病历列表页
 * Created by zheng on 2015/9/17.
 */
public class DiscussCaseListActivity extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListview;
    private DiscussCaseAdapter mAdapter;
    //private NavigationListAdapter firstAdapter;
    private FamDocPopAdapter firstAdapter;

    private int pageSize = 1;

    private EditText editSearch;
    private String searchKey = "";
    boolean searchFlag = false;
    private CheckBox classify;

    private LinearLayout popupWLayout;
    private List<JSONObject> list = null;
    private String office_id;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.discuss_case_list_aty);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        pageSize = 1;
        initData();
    }

    /**
     * 初始化加载
     */
    private void initData() {
        //DuoMeiHealth/ConsultationInfoSet?TYPE=medicalCaseDiscussion&PAGESIZE=1&PAGENUM=20&CONSULTATION_CENTER_ID=1&CUSTOMERID=225043
        List<BasicNameValuePair> pairs = new ArrayList<>();
        if (searchFlag) {
            pairs.add(new BasicNameValuePair("TYPE", "medicalCaseDiscussionByName"));
            pairs.add(new BasicNameValuePair("NAME", searchKey));
        } else {
            pairs.add(new BasicNameValuePair("TYPE", "medicalCaseDiscussion"));
        }

        pairs.add(new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        pairs.add(new BasicNameValuePair("PAGESIZE", pageSize + ""));
        pairs.add(new BasicNameValuePair("PAGENUM", "20"));
        pairs.add(new BasicNameValuePair("office_id", office_id));
        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);

        ApiService.doGetConsultationInfoSet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    if (pageSize == 1) {
                        mAdapter.removeAll();
                    }

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

    private void initView() {
        initializeTitle();
        titleTextV.setText("病例讨论");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("上传");

        mPullToRefreshListView = ((PullToRefreshListView) findViewById(R.id.case_discuss_list));
        mListview = mPullToRefreshListView.getRefreshableView();
        mAdapter = new DiscussCaseAdapter(this);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                caseClick(mAdapter.datas.get(position-1).MEDICAL_RECORD_ID + "");
                Intent intent = new Intent(DiscussCaseListActivity.this, CaseDiscussDetailsActivity.class);
                intent.putExtra("recordId", mAdapter.datas.get(position - 1).MEDICAL_RECORD_ID + "");
                startActivityForResult(intent, 201);
            }
        });
        mPullToRefreshListView.setOnRefreshListener(this);
        popupWLayout = (LinearLayout) findViewById(R.id.popwindow_layout);
        firstList = (ListView) findViewById(R.id.pop_list);
        classify = (CheckBox) findViewById(R.id.navigationbar_hospital);
        classify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    popupWLayout.setVisibility(View.GONE);
                } else {
                    popupWLayout.setVisibility(View.VISIBLE);
                    initPopData();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
            }
        });
        firstAdapter = new FamDocPopAdapter(this);
        firstList.setAdapter(firstAdapter);
        firstList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                office_id = firstAdapter.datas.get(position).optString("OFFICE_ID");
                classify.setText(firstAdapter.datas.get(position).optString("OFFICE_NAME"));
                pageSize = 1;
                initData();
                outPopup();
            }
        });
        editSearch = ((EditText) findViewById(R.id.edit_search_top));
        editSearch.setHint("请输入...");
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1) {
                    searchFlag = false;
                    searchKey = "";
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
                    pageSize = 1;
                    initData();
                } else {
                    searchFlag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchKey = editSearch.getText().toString().trim();
                initseatch(editSearch);
            }
        });

    }

    /**
     * 加载病例讨论分类数据
     */
    private void initPopData() {
        ApiService.OKHttpAddType(new AsyncHttpResponseHandler(this){
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))){
                        list = new ArrayList<JSONObject>();

                        JSONArray array = obj.optJSONArray("office");

                        JSONObject obj1 = new JSONObject();
                        obj1.put("OFFICE_NAME", "全部");
                        obj1.put("OFFICE_ID", "");
//                        array.put(0,obj1);

                        JSONObject item;
                        list.add(obj1);
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            list.add(item);
                        }

                        firstAdapter.onBoundData(list);
                    }else{
                        ToastUtil.showShort(obj.optString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }
    //关闭popupwindow
    private void outPopup() {
        if (classify.isChecked()){
            classify.setChecked(false);
        }else{
            classify.setChecked(true);
        }
        popupWLayout.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://上传病历
                if (!"0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition())) {
                    intent = new Intent(DiscussCaseListActivity.this, ExpertUploadCaseActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(DiscussCaseListActivity.this, "暂未开通");
                }
//                intent = new Intent(DiscussCaseListActivity.this,AtyDossierSearch.class);
//                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageSize = 1;
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 201) {
                pageSize = 1;
                initData();
            }
        }
    }

    /**
     * 搜索
     *
     * @param Search
     */
    private void initseatch(final EditText Search) {
        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchKey = editSearch.getText().toString().trim();
//                    if (officeName != null) {
                    if (!"".equals(searchKey)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);//关闭软键盘
                        pageSize = 1;
                        initData();
                    } else {
                        ToastUtil.showShort("请输入关键词");
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    private ListView firstList;

    /**
     * 增加点击数目
     *
     * @param id 病例id
     */
    private void caseClick(String id) {
//        http://localhost:8080/DuoMeiHealth/InfoCenterServlet?op=clickNum&medical_record_id=232299
        Map<String, String> map = new HashMap<>();
        map.put("op", "clickNum");
        map.put("medical_record_id", id);
        ApiService.OKHttpDelectData(map, new ApiCallbackWrapper<JSONObject>(this) {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, this);
    }
}
