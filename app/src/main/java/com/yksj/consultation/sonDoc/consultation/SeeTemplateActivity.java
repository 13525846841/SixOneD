package com.yksj.consultation.sonDoc.consultation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.adapter.SeeTemplateAdapter;
import com.yksj.consultation.bean.FollowTemplateSubBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Request;


/**
 * 查看随访计划
 */
public class SeeTemplateActivity extends BaseTitleActivity {
    public final static String TYPE = "TYPE";

    @BindView(R.id.name_stv) SuperTextView mNameStv;
    @BindView(R.id.start_time_stv) SuperTextView mStartTimeStv;
    @BindView(R.id.remind_me_stv) SuperTextView mRemindMeStv;
    @BindView(R.id.remind_patient_stv) SuperTextView mRemindPatientStv;
    @BindView(R.id.remind_time_stv) SuperTextView mRemindTimeStv;
    @BindView(R.id.patient_visiable_stv) SuperTextView mRemindVisiableStv;
    @BindView(R.id.followuplist) RecyclerView mRecyclerView;

    private SeeTemplateAdapter mAdapter;
    private String mFollowId;

    public static Intent getCallingIntent(Context context, String followId){
        Intent intent = new Intent(context, SeeTemplateActivity.class);
        intent.putExtra("follow_id", followId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_see_template;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("随访计划");
        mFollowId = getIntent().getStringExtra("follow_id");
        initView();
    }

    private void initView() {
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(8)));
        mAdapter = new SeeTemplateAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRemindVisiableStv.setSwitchEnable(false);
        initPlanData();
    }

    /**
     * 查看计划
     */
    private void initPlanData() {
        Map<String, String> map = new HashMap<>();
        map.put("follow_id", mFollowId);
        ApiService.OKHttpFindFollowSubListById(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("查询失败");
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        List<FollowTemplateSubBean> result = new ArrayList<>();
                        JSONArray array = obj.getJSONArray("sbus");
                        for (int i = 0; i < array.length(); i++) {
                            String json = array.getString(i);
                            FollowTemplateSubBean bean = GsonSerializer.deserialize(json, FollowTemplateSubBean.class);
                            result.add(bean);
                        }
                        mAdapter.setNewData(result);

                        JSONObject object = obj.getJSONObject("follow");
                        mNameStv.setRightString(object.optString("FOLLOW_UP_NAME"));
                        if (!HStringUtil.isEmpty(object.optString("CREATE_TIME"))){
                            mStartTimeStv.setRightString(TimeUtil.getFormatDate2(object.optString("CREATE_TIME")));
                        }


                        if ("1".equals(object.optString("ALERT_ME"))) {
                            mRemindMeStv.setSwitchIsChecked(true);
                        } else if ("0".equals(object.optString("ALERT_ME")) ) {
                            mRemindMeStv.setSwitchIsChecked(false);
                        }
                        mRemindMeStv.setSwitchEnable(false);

                        if ("1".equals(object.optString("ALERT_SICK"))) {
                            mRemindPatientStv.setSwitchIsChecked(true);
                        } else if ("0".equals(object.optString("ALERT_SICK"))) {
                            mRemindPatientStv.setSwitchIsChecked(false);
                        }
                        mRemindPatientStv.setSwitchEnable(false);

                        if ("10".equals(object.optString("ALERT_TIMETYPE"))) {
                            mRemindTimeStv.setRightString("提前" + object.optString("ALERT_TIMECOUNT") + "天");
                        } else if ("20".equals(object.optString("ALERT_TIMETYPE"))) {
                            mRemindTimeStv.setRightString("提前" + object.optString("ALERT_TIMECOUNT") + "周");
                        } else if ( "30".equals(object.optString("ALERT_TIMETYPE"))) {
                            mRemindTimeStv.setRightString("提前" + object.optString("ALERT_TIMECOUNT") + "月");
                        } else if ("40".equals(object.optString("ALERT_TIMETYPE"))) {
                            mRemindTimeStv.setRightString("提前" + object.optString("ALERT_TIMECOUNT") + "年");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
}
