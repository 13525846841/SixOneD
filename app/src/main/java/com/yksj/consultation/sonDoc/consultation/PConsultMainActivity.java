package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.member.FlowMassageActivity;
import com.yksj.consultation.sonDoc.doctor.SelectExpertMainUI;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新六一健康科室列表
 *
 * @author zheng
 */
public class PConsultMainActivity extends BaseActivity implements OnClickListener, OnItemClickListener, DoubleBtnFragmentDialog.OnFristClickListener, DoubleBtnFragmentDialog.OnSecondClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    private PullToRefreshListView mPullRefreshListView;
    private PConsultMainAdapter mAdapter;
    private Map<String, String> mConsultMap;
    private List<Map<String, String>> mConsultList;
    private ListView mListView;
    private EditText editSearch;
    private String officeName = "";
    private String officeNameNum;
    private String selectedOffice, officeCode;
    private String pid = "";
    private View mNullView;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.consultation_center_home_activity_layout);
        LogUtil.d("TAG", LoginBusiness.getInstance().getLoginEntity().getId() + "用户id");
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("会诊科室");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn.setVisibility(View.VISIBLE);
//        editSearch = (EditText) findViewById(R.id.edit_search_top);
//        editSearch.setHint("请输入科室名称");
//        editSearch.addTextChangedListener(new TextWatcher() {//搜索框字数变化监听
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                officeName = editSearch.getText().toString().trim();
//                initseatch(editSearch);
//            }
//        });
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.consultation_pulltorefresh_listview);
        mNullView = findViewById(R.id.nullview);
        mListView = mPullRefreshListView.getRefreshableView();
        mAdapter = new PConsultMainAdapter(this);
        mListView.setEmptyView(mNullView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mPullRefreshListView.setOnRefreshListener(this);
        if (getIntent().hasExtra("PID")) {
            pid = getIntent().getStringExtra("PID");
        }
        initData();
    }

//    private void initseatch(final EditText Search) {
//        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    officeName = Search.getText().toString().trim();
//                    if (!"".equals(officeName)) {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);//关闭软键盘
//                        initData();
//                    } else {
//                        initData();
//                        ToastUtil.showShort("输入科室名称");
//                    }
//                    handled = true;
//                }
//                return handled;
//            }
//        });
//
//    }


    private void initData() {
        ApiService.doHttpFindOfficeDoctor(officeName, new AsyncHttpResponseHandler(PConsultMainActivity.this) {

            @Override
            public void onSuccess(String content) {
                try {
                    JSONObject onject = new JSONObject(content);
                    JSONArray ss = onject.getJSONArray("result");
                    mConsultList = new ArrayList<>();
                    for (int i = 0; i < ss.length(); i++) {
                        mConsultMap = new HashMap<>();
                        JSONObject dd = ss.getJSONObject(i);
                        mConsultMap.put("OFFICE_CODE", dd.optString("OFFICE_CODE"));
                        mConsultMap.put("OFFICE_NAME", dd.optString("OFFICE_NAME"));
                        mConsultList.add(mConsultMap);
                    }
                    mAdapter.removeAll();
                    mAdapter.addAll(mConsultList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mPullRefreshListView.onRefreshComplete();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        selectedOffice = mAdapter.datas.get(position - 1).get("OFFICE_NAME").toString();
        officeCode = mAdapter.datas.get(position - 1).get("OFFICE_CODE").toString();
        initNum();
    }

    private void initNum() {
        ApiService.doHttpfindDocNum(officeCode, new ObjectHttpResponseHandler() {

            @Override
            public Object onParseResponse(String content) {
                try {
                    if (content != null) {
                        JSONObject object = new JSONObject(content);
                        if ("1".equals(object.optString("code"))) {
                            officeNameNum = object.optString("result");
                        } else if ("0".equals(object.optString("code"))) {
                            ToastUtil.showToastPanl(object.optString("message"));
                        }
                        return officeNameNum;
                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (response != null) {
                    String numText = "共有" + response.toString() + "位专家等待为您服务";
//                    showD(numText);
                    if ("0".equals(response.toString())) {
                        showS(numText);
                    } else if (!"0".equals(response.toString())) {
                        Intent intent = new Intent(PConsultMainActivity.this, SelectExpertMainUI.class);
                        intent.putExtra("OFFICECODE", officeCode);
                        intent.putExtra("OFFICENAME", selectedOffice);
                        intent.putExtra("PID", pid);
                        intent.putExtra("goalType", 1);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void showD(String numDoc) {
        DoubleBtnFragmentDialog.showDoubleBtn(PConsultMainActivity.this, selectedOffice, numDoc, "自己找专家", "帮我找专家", this, this).show();
    }

    private void showS(String hint) {
        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), hint);
    }

    @Override
    public void onBtn1() {
        Intent intent = new Intent(PConsultMainActivity.this, SelectExpertMainUI.class);
        intent.putExtra("OFFICECODE", officeCode);
        intent.putExtra("OFFICENAME", selectedOffice);
        intent.putExtra("PID", pid);
        intent.putExtra("goalType", 1);
        startActivity(intent);
    }

    @Override
    public void onBtn2() {
        startActivity(new Intent(PConsultMainActivity.this, FlowMassageActivity.class));
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        initData();
    }
}
