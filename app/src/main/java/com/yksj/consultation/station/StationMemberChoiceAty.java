package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.blankj.utilcode.util.ToastUtils;
import com.yksj.consultation.adapter.StationMemberChoiceAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.EStationOrderRefresh;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
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
 * 医生集团成员选择
 */
public class StationMemberChoiceAty extends BaseTitleActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private StationMemberChoiceAdapter adapter;
    private PullToRefreshListView mPullRefreshListView;

    private List<JSONObject> list = null;
    private String mStationId;
    private String mType;
    private View mEmptyView;
    private String mOrderId;
    private String mGroupId;

    public static Intent getCallingIntent(Context context, String orderType, String stationId, String orderId, String groupId) {
        Intent intent = new Intent(context, StationMemberChoiceAty.class);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        intent.putExtra(Constant.Station.ORDERID_EXTRA, orderId);
        intent.putExtra(Constant.Station.GROUPID_EXTRA, groupId);
        intent.putExtra(Constant.Station.ORDER_TYPE, orderType);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_member_choice_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mOrderId = getIntent().getStringExtra(Constant.Station.ORDERID_EXTRA);
        mGroupId = getIntent().getStringExtra(Constant.Station.GROUPID_EXTRA);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mType = getIntent().getStringExtra(Constant.Station.ORDER_TYPE);
        initView();
        initData();
    }

    private void initView() {
        setTitle("医生集团成员");
        mEmptyView = findViewById(R.id.empty_view_famous1);
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_station_member__pulllist));
        ListView mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setOnRefreshListener(this);
        adapter = new StationMemberChoiceAdapter(this);
        mListView.setAdapter(adapter);
        findViewById(R.id.btn2).setOnClickListener(this::makeChoice);
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "querySitePerson");
        map.put("site_id", mStationId);//1
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    list = new ArrayList<>();
                    if (HttpResult.SUCCESS.equals(obj.optString("code"))) {

                        if (!HStringUtil.isEmpty(obj.optString("result"))) {
                            JSONArray array = obj.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                jsonobject.put("isChecked", false);
                                list.add(jsonobject);
                            }
                            adapter.onBoundData(list);

                            if (list.size() == 0) {
                                mEmptyView.setVisibility(View.VISIBLE);
                                mPullRefreshListView.setVisibility(View.GONE);
                            } else {
                                mEmptyView.setVisibility(View.GONE);
                                mPullRefreshListView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullRefreshListView.onRefreshComplete();
                super.onAfter();
            }
        }, this);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.onRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.onRefreshComplete();
    }

    /**
     * 分配
     */
    public void makeChoice(View v) {
        String choice_id = "";
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (adapter.datas.get(i).optBoolean("isChecked")) {
                choice_id = adapter.datas.get(i).optString("CUSTOMER_ID");
            }
        }
        if (!HStringUtil.isEmpty(choice_id)) {
            if (DoctorHelper.isSelf(choice_id)) {
                ToastUtil.showShort("不能选择自己");
            } else {
                Intent i = new Intent();
                i.putExtra("id", choice_id);
                setResult(RESULT_OK, i);
                if (mType.equals(Constant.ChoiceType.FP)) {
                    requestDispatch(choice_id, mOrderId);
                } else if (mType.equals(Constant.ChoiceType.YQ)) {
                    requestInvited(choice_id, mOrderId, mGroupId);
                }
            }
        } else {
            ToastUtils.showShort("还未选择");
        }
    }

    /**
     * 分配医生
     * @param doctorId
     * @param orderId
     */
    private void requestDispatch(String doctorId, String orderId) {
        ApiService.OkHttpStationOrderDispatch(doctorId, orderId, Constant.StationOrderStatus.ZZFP, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    finish();
                    EventBus.getDefault().post(new EStationOrderRefresh());
                }
                ToastUtils.showShort(response.message);
            }
        });
    }

    /**
     * 邀请医生
     * @param doctorId
     */
    private void requestInvited(String doctorId, String mOrderId, String mGroupId) {
        ApiService.OkHttpStationOrderInvited(DoctorHelper.getId(), doctorId, mOrderId, mGroupId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    EventBus.getDefault().post(new EStationOrderRefresh());
                    finish();
                }
                ToastUtils.showShort(response.message);
            }
        });
    }
}
