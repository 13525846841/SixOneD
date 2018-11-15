package com.yksj.consultation.sonDoc.chatting.sixoneclass;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.SixOneAddAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 六一班
 */
public class SixOneAddClassActivity extends BaseActivity {

    private RelativeLayout add_friend;
    private RelativeLayout rl_chat;
    private ListView mLv;
    private SixOneAddAdapter adapter;
    private int num = 9;
    private TextView gc_number;//群聊数字

    private EditText mEditText;
    private View mEmpty;

    private String addId = "";//添加好友id
    private boolean canClick = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six_one_add_class);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("搜索手机号");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("添加");


        mLv = (ListView) findViewById(R.id.chat_lv);
        mEmpty = findViewById(R.id.empty_view);
        mEditText = (EditText) findViewById(R.id.include_search).findViewById(R.id.edit_search_top);
        mEditText.setHint("输入手机号");
        mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
//        if (num > 0) {
//            gc_number.setVisibility(View.VISIBLE);
//            if (num > 0 && num < 10) {
//                gc_number.setText(num + "");
//                gc_number.setSelected(false);
//            } else if (num > 99) {
//                gc_number.setText(99 + "+");
//                gc_number.setSelected(true);
//            } else if (num > 10 && num < 100) {
//                gc_number.setText(num + "");
//                gc_number.setSelected(true);
//            }
//        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditText.getText().toString().length() == 0) {
                    adapter.removeAll();
                }
            }
        });
        mEditText.setHint("请输入...");
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String content = mEditText.getText().toString();
                    if (content.length() > 0) {
                        if (ValidatorUtil.checkMobile(content)) {
                            getFriends(content);
                        } else {
                            mEditText.setError("手机号码格式不正确");
                        }

                    }

                }
                return false;
            }
        });
        adapter = new SixOneAddAdapter(this);
        mLv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                if (canClick) {
                    addFriends();
                }
                break;
        }
    }

    /**
     * 搜索系统内医生
     *
     * @param searchContent 查询key
     */
    private void getFriends(String searchContent) {

        Map<String, String> map = new HashMap<>();
        map.put("op", "queryCustomerByPhone");
        map.put("customer_id", DoctorHelper.getId());
        map.put("phone", searchContent);
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                canClick = false;
            }

            @Override
            public void onAfter() {
                super.onAfter();
                canClick = true;
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {

                            if (!HStringUtil.isEmpty(object.optString("result"))) {
                                List<JSONObject> mList = new ArrayList<JSONObject>();
                                mList.add(object.getJSONObject("result"));
                                mEmpty.setVisibility(View.GONE);
                                adapter.onBoundData(mList);
                            } else {
                                adapter.removeAll();
                                mEmpty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    adapter.removeAll();
                    mEmpty.setVisibility(View.VISIBLE);
                }
            }
        }, this);
    }


    /**
     * 添加好友
     */
    private void addFriends() {
        if (adapter.mData.size() > 0 && adapter.mData.get(0).optInt("status") != 0) {
            ToastUtil.showShort("未选择");
            return;
        }
        if (adapter.mData.size() > 0 && adapter.mData.get(0).optBoolean("isChecked")) {
            addId = adapter.mData.get(0).optString("CUSTOMER_ID");
        } else {
            ToastUtil.showShort("未选择");
            return;
        }
        if (HStringUtil.isEmpty(addId)) {
            ToastUtil.showShort("请选择");
            return;
        }
        if (addId.equals(DoctorHelper.getId())) {
            ToastUtil.showShort("您不能添加您自己");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());
        map.put("relation_customer_id", addId);
        map.put("op", "addFriends");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                canClick = false;
            }

            @Override
            public void onAfter() {
                super.onAfter();
                canClick = true;
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            setResult(RESULT_OK);
                            finish();
                        }
                        ToastUtil.showShort(object.optString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

}
