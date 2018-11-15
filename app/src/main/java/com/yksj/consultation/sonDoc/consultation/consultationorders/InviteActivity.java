package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.squareup.picasso.Picasso;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.ExpertStatus;
import com.yksj.consultation.bean.IntentConstant;
import com.yksj.consultation.comm.CommonAdapter;
import com.yksj.consultation.comm.CommonViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.home.InviteAlertDialog;
import com.yksj.consultation.sonDoc.listener.DialogOnClickListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 邀请成员列表
 */
public class InviteActivity extends BaseActivity {

    private ListView mListView;
    private InviteAlertDialog dialog;
    private List<JSONObject> mList;//专家好友
    private CommonAdapter<JSONObject> mAdapter;
    private String conId = "";//会诊id
    private String expId = "";//主诊专家id
    private String inviteInfo = "";//被邀请专家信息
    private String conName = "";//会诊名称
    private static final int LOADMORE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initView();
        loadMyFriends();
    }

    private void initView() {
        initializeTitle();
        if (getIntent().hasExtra(IntentConstant.ConsultationId)) {
            conId = getIntent().getStringExtra(IntentConstant.ConsultationId);
        } if (getIntent().hasExtra(IntentConstant.ConsultationName)) {
            conName = getIntent().getStringExtra(IntentConstant.ConsultationName);
        }
        if (getIntent().hasExtra(IntentConstant.ConsultationMainExpert)) {
            expId = getIntent().getStringExtra(IntentConstant.ConsultationMainExpert);
        }
        if (getIntent().hasExtra(IntentConstant.InvitedExpert)) {
            inviteInfo = getIntent().getStringExtra(IntentConstant.InvitedExpert);
        }
        titleTextV.setText("邀请");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("确定");
        titleRightBtn2.setOnClickListener(this);
        mListView = ((ListView) findViewById(R.id.my_invite_list));
        mAdapter = new CommonAdapter<JSONObject>(this) {
            @Override
            public void onBoundView(CommonViewHolder helper, final JSONObject obj) {
                //备注名称
                String mRemarkName = obj.optString("REMARKS_NAME");
                //昵称
                String mCustomerNickname = obj.optString("CUSTOMER_NICKNAME");
                //科室
                String mPro = obj.optString("OFFICE_NAME");
                //头像
                String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + obj.optString("BIG_ICON_BACKGROUND");

                if (HStringUtil.isEmpty(mRemarkName)) {
                    helper.setText(R.id.doc_name, mCustomerNickname);
                } else {
                    helper.setText(R.id.doc_name, mRemarkName);
                }
                helper.setText(R.id.doc_room, mPro);


                CheckBox mCheckBox = helper.getView(R.id.iv_choice);
                TextView mCheckText = helper.getView(R.id.choice_tip);


                mCheckText.setVisibility(View.GONE);
                mCheckBox.setVisibility(View.GONE);
                String state = obj.optString(Constant.EXPERT_STATE);
                if (ExpertStatus.INVITING_STATE.equals(state)) {
                    mCheckText.setVisibility(View.VISIBLE);
                    mCheckText.setText("邀请中");
                } else if (ExpertStatus.ACCEPT_STATE.equals(state)) {
                    mCheckText.setVisibility(View.VISIBLE);
                    mCheckText.setText("已邀请");
                } else {
                    mCheckBox.setVisibility(View.VISIBLE);
                }

                mCheckBox.setChecked(obj.optBoolean(Constant.IS_CHECKED));
                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            if (buttonView.isPressed()) {
                                obj.put(Constant.IS_CHECKED, isChecked);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                ImageView mView = helper.getView(R.id.image);
                Picasso.with(InviteActivity.this).load(headview).error(R.drawable.default_head_mankind).placeholder(R.drawable.default_head_mankind).into(mView);
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


            }

            @Override
            public int viewLayout() {
                return R.layout.item_invite;
            }
        };
        mListView.setAdapter(mAdapter);
        findViewById(R.id.tv_moreExpert).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.tv_moreExpert://更多专家
                intent = new Intent(InviteActivity.this, ExpertMainUI.class);
                intent.putExtra(IntentConstant.ConsultationId, conId);
                intent.putExtra(IntentConstant.ConsultationName, conName);
                intent.putExtra(IntentConstant.ConsultationMainExpert, expId);
                intent.putExtra(IntentConstant.InvitedExpert, inviteInfo);
                startActivityForResult(intent, LOADMORE);
                break;
            case R.id.title_right2:
                confirmInvite();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    /**
     * 弹框
     */
    private void AlertDialog() {
        dialog = new InviteAlertDialog.Builder(InviteActivity.this)
                .setTitleText("已经向专家发起邀请成功，请耐心等候专家接受。")
                .setHeight(0.21f)  //屏幕高度*0.21
                .setWidth(0.7f)  //屏幕宽度*0.7
                .setOnclickListener(new DialogOnClickListener() {
                    @Override
                    public void clickButton(View view) {
                        InviteActivity.this.finish();
                    }
                })
                .build();
    }

    /**
     * 我的专家好友
     */
    private void loadMyFriends() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "queryExpertFriendsList");
        map.put("customer_id", DoctorHelper.getId());
        ApiService.OKHttpAddTools(map, new MyApiCallback<JSONObject>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    mList = new ArrayList<JSONObject>();
                    JSONArray array = new JSONArray();
                    array = response.optJSONArray("result");
                    int count = array.length();
                    //存在好友
                    if (count > 0) {
                        for (int i = 0; i < count; i++) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                obj.put(Constant.IS_CHECKED, false);
                                obj.put(Constant.EXPERT_STATE, ExpertStatus.NO_STATE);
                                mList.add(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        mAdapter.onBoundData(checkExpert(mList));
                        findViewById(R.id.load_faile_layout).setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    } else {
                        mListView.setVisibility(View.GONE);
                        findViewById(R.id.load_faile_layout).setVisibility(View.VISIBLE);
                    }
                }
            }
        }, this);
    }

    /**
     * 获取被邀请专家的id
     */
    private List<String> getExpertIds() {
        List<String> expertIds = new ArrayList<>();
        int count = mList.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                JSONObject obj = mList.get(i);
                if (obj.optBoolean(Constant.IS_CHECKED)) {
                    expertIds.add(obj.optString("REL_CUSTOMER_ID"));
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
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        for (int i = 0; i < mIds.size(); i++) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("consultation_id", conId);
                obj.put("main_doctor_id", expId);
                obj.put("consultation_name", conName);
                obj.put("inv_doctor_id", mIds.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
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
                    if ("1".equals(response.optString("code"))) {
                        AlertDialog();
                        dialog.show();
                    } else {
                        ToastUtil.showShort(response.optString("message"));
                    }
                }
            }
        }, this);
    }

    /**
     * 检查专家是否被邀约
     */
    private List<JSONObject> checkExpert(List<JSONObject> mList) {
        if (!HStringUtil.isEmpty(inviteInfo)) {
            try {
                JSONArray array = new JSONArray(inviteInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    for (int j = 0; j < mList.size(); j++) {
                        if (obj.optString("INV_DOCTOR_ID").equals(mList.get(j).optString("REL_CUSTOMER_ID"))) {
                            mList.get(j).put(Constant.EXPERT_STATE, obj.optString("INV_STATUS"));
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
