package com.yksj.consultation.doctor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.ResourceHelper;
import com.yksj.consultation.adapter.MyRoomAdapter;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.login.UserLoginActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctor.MyInfoActivity;
import com.yksj.consultation.sonDoc.listener.onClickMsgeListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

;

/**
 * 我的工作室
 */
public class MyRoomActivity extends BaseTitleActivity implements AdapterView.OnItemClickListener, onClickMsgeListener {

    private RelativeLayout personInfo;
    private GridView gv;
    //private GridView gv_top;//上面三个固定的按钮
    //  private RoomTopAdapter mAdapter;//服务开通的adapter
    private MyRoomAdapter adapter;//工具箱的适配器
    private static final int REQUESTCODE = 1;
    private static final int SURE = 2;
    public boolean isOpen;//判断是否关闭
    public String consultation_flag = "0";//会诊是否开启标记
    private List<JSONObject> mList = null;
    private String doctor_Id = "";
    private String logo;
    private static final int ONLINE = 3;
    private TextView utils;
    private ImageView utils_bg;
    private TextView utils_name;

    public int servicePosition;//服务开通-文字
    public View serviceView;//服务开通——试图
    private ListView mLv;

    private CustomerInfoEntity mCusInfoEctity;

    private int AUDIT;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_my_room;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("我的工作室");
        initView();
    }

    private void initView() {
        personInfo = (RelativeLayout) findViewById(R.id.rl_person_info);
        personInfo.setOnClickListener(this);

        mLv = (ListView) findViewById(R.id.remen);
        adapter = new MyRoomAdapter(this);
        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(this);


        findViewById(R.id.rl_picandcul).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
        findViewById(R.id.rl_consul).setOnClickListener(this);
        findViewById(R.id.rl_video).setOnClickListener(this);
        findViewById(R.id.rl_addnum).setOnClickListener(this);
        findViewById(R.id.rl_online2).setOnClickListener(this);
        findViewById(R.id.rl_comments).setOnClickListener(this);
        findViewById(R.id.tool).setOnClickListener(this);
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            AUDIT = LoginBusiness.getInstance().getLoginEntity().getRoldid();
            if (AUDIT == 0) {
                startToLogin();
            }
        } else {
            startToLogin();
        }
    }

    /**
     * 跳转到登陆界面
     */
    private void startToLogin(){
        ToastUtil.showShort(ResourceHelper.getString(R.string.error_login_info_none));
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /***
     * 加载医生资料，如果医生在审核中，不能开通服务
     */
    private void initDoctorData() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "findCustomerInfo"));
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));

        ApiService.doGetConsultationInfoSet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        mCusInfoEctity = DataParseUtil.JsonToDocCustmerInfo(obj.getJSONObject("result"));
                        if (!"40".equals(mCusInfoEctity.getVerifyFlag())) {
                            AUDIT = mCusInfoEctity.getRoldid();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DoctorHelper.hasLoagin()) {
            doctor_Id = LoginBusiness.getInstance().getLoginEntity().getId();
            if (HStringUtil.isEmpty(doctor_Id)) {
                startToLogin();
            }
        } else {
            startToLogin();
        }
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDoctorData();
    }

    /**
     * 加载工具箱
     */
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", doctor_Id);//124951  doctor_id
        map.put("consultation_center_id", "5");

        ApiService.OKHttpLoadingTools(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            JSONArray array = jsonObject.getJSONArray("result");
                            mList = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                mList.add(jsonobject);
                            }
                            adapter.onBoundData(mList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    /**
     * 加载数据  （在线会诊）
     */
//    private void initData1(final TextView utils, final TextView utils_name, final ImageView utils_bg) {
//        Map<String, String> map = new HashMap<>();
//        map.put("doctor_id", doctor_Id);
//        map.put("op", "queryConsultationCenterInfo");
//
//        ApiService.OKHttpIsConsultation(map, new ApiCallbackWrapper<String>(this) {
//            @Override
//            public void onResponse(String response) {
//                super.onResponse(response);
//                if (!HStringUtil.isEmpty(response)) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
//                            JSONObject massage = jsonObject.optJSONObject("result");
//                            consultation_flag = massage.optString("CONSULTATION_FLAG");
//
//                            if (consultation_flag.equals("1")) {
//                                utils.setText("已开通");
//                                utils_name.setTextColor(Color.WHITE);
//                                utils.setTextColor(Color.WHITE);
//                                utils_bg.setSelected(true);
//                                //   mAdapter.notifyDataSetChanged();
//                                //mAdapter.isChange(1);
//                            } else if (consultation_flag.equals("0")) {
//                                utils_bg.setSelected(false);
//                                utils.setText("未开通");
//                                //   mAdapter.notifyDataSetChanged();
//                                // mAdapter.isChange(0);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, this);
//    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.rl_person_info:
                intent = MyInfoActivity.getCallingIntent(this, DoctorHelper.getId());
                startActivity(intent);
                break;

            case R.id.rl_picandcul://图文咨询
                if (888 != AUDIT) {
                    ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
                } else {
                    intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.TW);
                    startActivity(intent);
                }
                break;
            case R.id.rl_phone://电话咨询
                intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.DH);
                startActivity(intent);
                break;

            case R.id.rl_consul://包月咨询
                if (888 != AUDIT) {
                    ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
                } else {
                    intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.BY);
                    startActivity(intent);
                }
                break;
            case R.id.rl_video://视频咨询
                intent = DoctorServiceSettingsActivity.getCallingIntent(this, ServiceType.SP);
                startActivity(intent);
                break;

            case R.id.rl_addnum://门诊界面
                if (888 != AUDIT) {
                    ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
                } else {
                    intent = new Intent(MyRoomActivity.this, DoctorSeeServiceActivity.class);
                    intent.putExtra("type", "3");
                    intent.putExtra("titleName", "门诊预约");
                    startActivity(intent);
                }
                break;
            case R.id.rl_online2://会诊界面
                if (888 != AUDIT) {
                    ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
                } else {
                    intent = new Intent(MyRoomActivity.this, OnLineConsultActivity.class);
                    intent.putExtra("AUDIT", AUDIT);
                    startActivityForResult(intent, ONLINE);
                }
                break;
            case R.id.rl_comments://我的评价
                intent = new Intent(this, MyCommentActivity.class);
                startActivity(intent);
                break;
            case R.id.tool://工具箱
                intent = new Intent(this, DoctorAddToolsActivity.class);
                intent.putExtra(DoctorAddToolsActivity.TYPE, "add");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position == parent.getChildCount() - 1) {
//            Intent intent = new Intent(this, DoctorAddToolsActivity.class);
//            intent.putExtra(DoctorAddToolsActivity.TYPE, "add");
//            startActivity(intent);
//        } else {
//          logo = adapter.logo(position);
        Intent intent = new Intent(this, DoctorAddToolsActivity.class);
        intent.putExtra(DoctorAddToolsActivity.TYPE, "set");
//          intent.putExtra(DoctorAddToolsActivity.LOGO, logo);
        intent.putExtra("TOOL_NAME", mList.get(position).optString("TOOL_NAME"));
        intent.putExtra("TOOL_URL", mList.get(position).optString("TOOL_URL"));
        intent.putExtra("TOOL_CODE", mList.get(position).optString("TOOL_CODE"));
        startActivityForResult(intent, REQUESTCODE);
        //  }

    }

    //    @Override
    public void onClickMsg(View view, int position) {
////        servicePosition = position;
////        serviceView = view;
//
////        utils = (TextView) view.findViewById(R.id.true_false);
////        utils_bg = (ImageView) view.findViewById(R.id.image_bg);
//        utils_name = (TextView) view.findViewById(R.id.utils_name);
//        if (position == 1) {
//            initData(utils, utils_name, utils_bg);
//        } else if (position == 0) {
//            initAppoData(utils, utils_name, utils_bg);
//        }
    }

//    private void serviceData(){
//        utils = (TextView) serviceView.findViewById(R.id.true_false);
//        utils_bg = (ImageView) serviceView.findViewById(R.id.image_bg);
//        utils_name = (TextView) serviceView.findViewById(R.id.utils_name);
//        if(servicePosition==1){
//            initData(utils,utils_name,utils_bg);
//        }else if(servicePosition==0){
//            initAppoData(utils,utils_name,utils_bg);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case REQUESTCODE:
//                if (resultCode == RESULT_OK) {
//                    isOpen = data.getBooleanExtra("ISCLOSE", true);
////                    adapter.setBackground(isOpen);
////                    adapter.notifyDataSetChanged();
//                }
//                break;
//            case SURE:
//                if (resultCode == RESULT_OK) {
//                    isOpen = data.getBooleanExtra("ISCLOSE", true);
////                    adapter.setBackground(isOpen);
////                    adapter.notifyDataSetChanged();
//                }
//                break;
//            case ONLINE:
//                if (resultCode == RESULT_OK) {
//                    initData(utils, utils_name, utils_bg);
//                }
//                break;
//            default:
//                break;
        }
    }

    /**
     * 预约就诊数据
     *
     * @param utils
     * @param utils_name
     * @param utils_bg
     */
    private void initAppoData(final TextView utils, final TextView utils_name, final ImageView utils_bg) {
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", doctor_Id);
        ApiService.OKHttpIsYue(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            if (jsonObject.optString("result").equals("1")) {
                                logo = "1";
                                utils.setText("已开通");
                                utils_name.setTextColor(Color.WHITE);
                                utils.setTextColor(Color.WHITE);
                                utils_bg.setSelected(true);
                                //mAdapter.isChange(1);
                            } else if (jsonObject.optString("result").equals("0")) {
                                logo = "0";
                                utils_bg.setSelected(false);
                                utils.setText("未开通");
                                //mAdapter.isChange(0);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }
}
