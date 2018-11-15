package com.yksj.consultation.sonDoc.friend;

import android.content.Context;
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

import okhttp3.Request;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.MyCustomerListAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by zheng on 15/9/30.
 */
public class SeatchCustomerAty extends BaseActivity implements View.OnClickListener {
    private EditText editSearch;
    private String customerName="";
    private MyCustomerListAdapter mAdapter;
    private List<Map<String ,String>> mData;
    private Map<String ,String > mCustomer;
    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private int pagesize=1;
    private String isEx="0";
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.seacth_customer_aty);
        initView();
    }

    private void initView() {
        editSearch = ((EditText) findViewById(R.id.seach_text));
        ((TextView) findViewById(R.id.cancel_onclick)).setOnClickListener(this);
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_customer_list));
        mListView = mPullRefreshListView.getRefreshableView();
//        if(!"0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition())){
//            isEx="1";
//        }
        if(getIntent().hasExtra("MAIN")){
            isEx="1";
        }
        mAdapter = new MyCustomerListAdapter(this);
        mListView.setAdapter(mAdapter);
        editSearch.setHint("请输入患者姓名");
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                customerName = editSearch.getText().toString().trim();
                if (customerName != null) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
//                    initData();
                    initseatch(editSearch);
                } else {
                    ToastUtil.showShort("输入患者姓名");
                }
//                initseatch(editSearch);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel_onclick:
                onBackPressed();
                break;
        }
    }

        private void initseatch(final EditText Search) {
        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    customerName = Search.getText().toString().trim();
//                    if (officeName != null) {
                    if(!"".equals(customerName)){
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Search.getWindowToken(), 0);//关闭软键盘
                        initData();
                    } else {
                        ToastUtil.showShort("输入科室名称");
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }
    private void initData(){
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", SmartFoxClient.getLoginUserId());
//        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", "3762");
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE","findMyPatient");
        BasicNameValuePair param2 = new BasicNameValuePair("NAME",customerName);
        BasicNameValuePair param3 = new BasicNameValuePair("PAGESIZE",pagesize+"");
        BasicNameValuePair param4 = new BasicNameValuePair("PAGENUM","15");
        params.add(param);params.add(param1);params.add(param2);params.add(param3);params.add(param4);
        ApiService.OKHttpFindMyPatient(params, new MyApiCallback<JSONObject>(this) {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    mData = new ArrayList<Map<String, String>>();
                    try {
                        if ("1".equals(response.optString("code"))) {
                            JSONArray array = response.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                mCustomer = new HashMap<String, String>();
                                JSONObject object = array.optJSONObject(i);
                                mCustomer.put("ISEX",isEx);//是否是专家
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
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }
}
