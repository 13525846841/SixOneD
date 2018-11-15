package com.yksj.consultation.im;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.BaseTabPagerAdpater;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.order.AddListFragment;
import com.yksj.consultation.sonDoc.order.AddListFragmentP;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupChatActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private ViewPager mPager;
    private RadioGroup mGroup;
    private int searchType = 0;//0医生 1患者

    private EditText mEditText;
    private AddListFragment fragment1 = null;
    private AddListFragmentP fragment2 = null;
    private List<String> idList1 = null;
    private List<String> idList2 = null;

    public static final String TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat);
        initView();

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("选择群聊成员");
        titleLeftBtn.setOnClickListener(this);

        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("确定");
//        AddGroupChatFragment addgroupchat = new AddGroupChatFragment();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.add(R.id.ll_fragment,addgroupchat);
//        ft.commit();

        mGroup = (RadioGroup) findViewById(R.id.radio_group1);
        mGroup.setOnCheckedChangeListener(this);
        mPager = (ViewPager) findViewById(R.id.viewpager1);
        BaseTabPagerAdpater mAdpater = new BaseTabPagerAdpater(getSupportFragmentManager());
        mPager.setAdapter(mAdpater);
        mPager.setOnPageChangeListener(this);
        ArrayList<Fragment> mlList = new ArrayList<>();


        mEditText = (EditText) findViewById(R.id.include_search).findViewById(R.id.edit_search_top);
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
                    switch (searchType) {
                        case 0:
                            fragment1.refreshData("");
                            break;
                        case 1:
                            fragment2.refreshData("");
                            break;
                    }
                }
            }
        });
        mEditText.setHint("请输入...");
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mEditText.getText().toString().length() > 0) {
                        switch (searchType) {
                            case 0:
                                fragment1.refreshData(mEditText.getText().toString());
                                fragment2.refreshData("");
                                break;
                            case 1:
                                fragment2.refreshData(mEditText.getText().toString());
                                fragment1.refreshData("");
                                break;
                        }
                    }
                    WheelUtils.hideInput(AddGroupChatActivity.this, mEditText.getWindowToken());
                }
                return false;
            }
        });


        //0-医生
        fragment1 = new AddListFragment();
        Bundle e = new Bundle();
        e.putString("type", "0");
        e.putString("groupId", "");
        fragment1.setArguments(e);
        mlList.add(fragment1);


        //1-患者
        fragment2 = new AddListFragmentP();
        Bundle b = new Bundle();
        b.putString("type", "1");
        b.putString("groupId", "");
        fragment2.setArguments(b);
        mlList.add(fragment2);


        mAdpater.bindFragment(mlList);
        mPager.setCurrentItem(0, false);
        idList1 = new ArrayList<>();
        idList2 = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://确定
//                ToastUtil.showShort("确定");
//                Intent intent = new Intent(this, GroupDataActivity.class);//群资料界面，暂时放这，要挪
//                startActivity(intent);
                createGroup();
                break;
        }
    }

    /**
     * 创建群聊
     */
    private void createGroup() {
        getIds();

        int count1 = idList1.size();
        int count2 = idList2.size();
        if (count1 + count2 <= 0) {
            ToastUtil.showShort("至少选择一个人");
            return;
        }

        JSONArray array = new JSONArray();
        for (int i = 0; i < count1; i++) {
            JSONObject obj1 = new JSONObject();
            try {
                obj1.put("customer_id", idList1.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj1);
        }
        for (int j = 0; j < count2; j++) {
            JSONObject obj2 = new JSONObject();
            try {
                obj2.put("customer_id", idList2.get(j));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj2);
        }
        JSONObject object=new JSONObject();
        try {
            object.put("customerArr",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Map<String, String> map = new HashMap<>();
        map.put("create_id", DoctorHelper.getId());
        map.put("op", "createGroupChat");
        map.put("jsonArr", object.toString());
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        ToastUtil.showShort(obj.optString("message"));
                        if (HttpResult.SUCCESS.endsWith(obj.optString("code"))) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) {
                mPager.setCurrentItem(i, true);
                searchType = i;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton mButton = (RadioButton) mGroup.getChildAt(position);
        mButton.setChecked(true);
        searchType = position;
        if (mEditText.getText().toString().length() == 0) {
            WheelUtils.hideInput(AddGroupChatActivity.this, mEditText.getWindowToken());
        } else {
            WheelUtils.showSoftInput(AddGroupChatActivity.this, mEditText);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void getIds() {
        idList1.clear();
        idList2.clear();
        List<JSONObject> mList1 = null;
        List<JSONObject> mList2 = null;
        mList1 = fragment1.getIdsData();
        if (mList1.size() > 0) {
            for (JSONObject obj : mList1) {
                if (obj.optBoolean("isChecked")) {
                    idList1.add(obj.optString("REL_CUSTOMER_ID"));
                }
            }
        }
        mList2 = fragment2.getIdsData();
        if (mList2.size() > 0) {
            for (JSONObject obj : mList2) {
                if (obj.optBoolean("isChecked")) {
                    idList2.add(obj.optString("CUSTOMER_ID"));
                }
            }
        }

    }
}
