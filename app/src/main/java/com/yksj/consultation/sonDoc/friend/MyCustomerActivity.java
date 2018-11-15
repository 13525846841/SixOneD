package com.yksj.consultation.sonDoc.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yksj.consultation.adapter.MyCustomerListAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 我的患者
 * Created by zheng on 15/9/23.
 */
public class MyCustomerActivity extends BaseTitleActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
    private MyCustomerListAdapter mAdapter;
    private List<Map<String, String>> mData;
    private Map<String, String> mCustomer;
    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private String customerName = "";
    private int pagesize = 1;
    private String isEx = "0";
    private EditText editSearch;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, MyCustomerActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.my_customer_aty_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("我的患者");
        if(!"0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition())){
            isEx="1";
        }
        if (getIntent().hasExtra("MAIN")) {
            isEx = "1";
        }
        mData = new ArrayList<Map<String, String>>();
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_customer_list));
        mListView = mPullRefreshListView.getRefreshableView();
        mAdapter = new MyCustomerListAdapter(this);
        mListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnRefreshListener(this);


        editSearch = ((EditText) findViewById(R.id.edit_search_top));
        editSearch.setHint("请输入患者姓名");
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    customerName = "";
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
                    pagesize = 1;
                    mData.clear();
                    initData();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                customerName = editSearch.getText().toString().trim();
                initseatch(editSearch);
            }
        });
        initData();
    }

    private void initData() {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", SmartFoxClient.getLoginUserId());
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "findMyPatient");
        BasicNameValuePair param2 = new BasicNameValuePair("NAME", customerName);
        BasicNameValuePair param3 = new BasicNameValuePair("PAGESIZE", pagesize + "");
        BasicNameValuePair param4 = new BasicNameValuePair("PAGENUM", "20");
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        ApiService.OKHttpFindMyPatient(params, new MyApiCallback<JSONObject>(this) {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    if (pagesize == 1) {
                        mData.clear();
                    }
                    try {
                        if ("1".equals(response.optString("code"))) {
                            JSONArray array = response.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                mCustomer = new HashMap<String, String>();
                                JSONObject object = array.optJSONObject(i);
                                mCustomer.put("ISEX", isEx);//是否是专家
                                mCustomer.put("CUSTOMER_ID", object.optString("CUSTOMER_ID"));
                                mCustomer.put("REAL_NAME", object.optString("REAL_NAME"));
                                mCustomer.put("CUSTOMER_SEX", object.optString("CUSTOMER_SEX"));
                                mCustomer.put("CUSTOMER_ACCOUNTS", object.optString("CUSTOMER_ACCOUNTS"));
                                mCustomer.put("CLIENT_ICON_BACKGROUND", object.optString("CLIENT_ICON_BACKGROUND"));
                                mCustomer.put("NUMS", object.optString("NUMS"));
                                mData.add(mCustomer);
                            }
                            mAdapter.removeAll();
                            mAdapter.addAll(mData);
                            if (mData.size()==0){
                                ToastUtil.showShort("没有您想要的内容");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullRefreshListView.setRefreshing();
            }
        }, this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pagesize = 1;
        mData.clear();
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pagesize++;
        initData();
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
                    customerName = Search.getText().toString().trim();
//                    if (officeName != null) {
                    if (!"".equals(customerName)) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);//关闭软键盘
                        pagesize = 1;
                        mData.clear();
                        initData();
                    } else {
                        ToastUtil.showShort("输入患者姓名");
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }
}
