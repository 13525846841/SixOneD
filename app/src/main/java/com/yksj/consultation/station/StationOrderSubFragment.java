package com.yksj.consultation.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseFragment;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.StationOrderAdapter;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.ServiceOrderBean;
import com.yksj.consultation.event.EStationOrderRefresh;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.library.base.utils.ResourceHelper;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Request;

/**
 * Created by ${chen} on 2017/4/6.
 */
public class StationOrderSubFragment extends BaseFragment implements StationOrderAdapter.OnOrderClickListener {

    private StationOrderAdapter mAdapter;
    private String mType;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    private String site_id;

    public static StationOrderSubFragment newInstance(String serviceTypeId, String type, String stationId) {

        Bundle args = new Bundle();
        args.putString(Constant.Station.SERVICE_TYPE_ID, serviceTypeId);
        args.putString(Constant.Station.ORDER_TYPE, type);
        args.putString(Constant.Station.STATION_ID, stationId);

        StationOrderSubFragment fragment = new StationOrderSubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.order_list_fragment;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        mType = getArguments().getString(Constant.Station.ORDER_TYPE);
        site_id = getArguments().getString(Constant.Station.STATION_ID);
        initView(view);
    }

    private void initView(View view) {
        mRefreshLayout
                .setOnRefreshListener(refreshLayout -> requestData())
                .setEnableLoadMore(false)
                .autoRefresh();

        mRecyclerView.setAdapter(mAdapter = new StationOrderAdapter(mType));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, ResourceHelper.getDimens(R.dimen.dp_6)));
        mAdapter.setOrderClickListener(this);
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(BaseQuickAdapter adapter, View view, int i) {
//        if ("6".equals(service_type_id)) {
//
//        } else if (service_type_id.equals(ServiceOrderType.SP) && "2".equals(mType)) {
//            try {
//                sendVideo(mAdapter.list.get(position - 1).getJSONObject("info").optString("CUSTOMER_ACCOUNTS"), mAdapter.list.get(position - 1).optString("ORDER_ID"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else if (service_type_id.equals(ServiceOrderType.SP) && "1".equals(mType)) {
//
//        } else {
//            String name = "";
//            String chatId = "";
//            String orderId = "";
//            try {
//                name = mAdapter.list.get(position - 1).getJSONObject("info").optString("CUSTOMER_NICKNAME");
//                chatId = mAdapter.list.get(position - 1).optString("ENJOY_CUSTOMER_ID");
//                orderId = mAdapter.list.get(position - 1).optString("ORDER_ID");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            FriendHttpUtil.chatFromPerson(getActivity(), chatId, name, orderId, ObjectType.SPECIAL_SERVER);
//        }
    }

    @Subscribe
    public void onEventRefresh(EStationOrderRefresh e) {
        mRefreshLayout.postDelayed(() -> requestData(), 20);
    }

    @Override
    public void onDestroy() {
        mAdapter.release();
        super.onDestroy();
    }

    private void requestData() {
        ApiService.OkHttpStationOrder(DoctorHelper.getId(), site_id, mType, new ApiCallbackWrapper<ResponseBean<List<ServiceOrderBean>>>() {

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onResponse(ResponseBean<List<ServiceOrderBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<ServiceOrderBean> result = response.result;
                    mAdapter.setNewData(result);
                    mRefreshLayout.finishRefresh();
                    if (result.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.showShort(response.message);
                    mRefreshLayout.finishRefresh();
                }
            }
        });
    }

    /**
     * 站长抢单
     * @param doctorId
     * @param orderId
     * @param status
     */
    private void requestGrab(String doctorId, String orderId, String status) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "distributionOrder");
        map.put("doctor_id", doctorId);
        map.put("order_id", orderId);
        map.put("status", status);//1-抢单阶段, 2 - 分配给某个医生
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<JSONObject>(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    ToastUtil.showShort(response.optString("message"));
                    if ("1".equals(response.optString("code"))) {
                        //更新
                        EventBus.getDefault().post(new MyEvent("", 0));
                    }
                }
            }
        }, this);
    }

    /**
     * 验证是否可以视频
     */
    public void sendVideo(final String account, final String order_Id) {
//        Map<String, String> map = new HashMap<>();
//        map.put("order_id", order_Id);
//        map.put("op", "verificationOrder");
//        ApiService.OKHttpConInvited(map, new OkHttpClientManager.ApiCallback<String>() {
//            @Override
//            public void onError(Request request, Exception e) {
//            }
//
//            @Override
//            public void onResponse(String content) {
//                try {
//                    JSONObject obj = new JSONObject(content);
//                    if ("1".equals(obj.optString("code"))) {
//                        DoubleBtnFragmentDialog.showDefault(getActivity().getSupportFragmentManager(), "确定要发送视频吗？", "取消", "确定",
//                                new DoubleBtnFragmentDialog.OnDilaogClickListener() {
//                                    @Override
//                                    public void onDismiss(DialogFragment fragment) {
//                                    }
//
//                                    @Override
//                                    public void onClick(DialogFragment fragment, View v) {
//                                        loginAvChat(account, order_Id);
//                                    }
//                                });
//                    } else {
//                        ToastUtil.showShort(obj.optString("message"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, this);
    }

    /**
     * 刷新
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyEvent event) {
        if (Constant.StationOrderStatus.ZZFP.equals(mType)) {
            requestData();
        }
    }

    /**
     * 分配
     * @param item
     */
    @Override
    public void onDispatchClick(ServiceOrderBean item) {
        Intent intent = StationMemberChoiceAty.getCallingIntent(getContext(), Constant.ChoiceType.FP, site_id, String.valueOf(item.ORDER_ID), item.GROUP_ID);
        startActivity(intent);
    }

    /**
     * 医生集团接单
     * @param item
     */
    @Override
    public void onGrabClick(ServiceOrderBean item) {
        requestGrab(DoctorHelper.getId(), String.valueOf(item.ORDER_ID), Constant.StationOrderStatus.QDSUCESS);
    }

    /**
     * 聊天
     * @param item
     */
    @Override
    public void onChatClick(ServiceOrderBean item) {
        GroupInfoEntity entity = new GroupInfoEntity();
        entity.setId(item.GROUP_ID);
        entity.setIsBL("2");
        entity.setName(item.RECORD_NAME);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constant.Chat.KEY_PARAME, entity);
        intent.putExtra(Constant.Chat.ORDER_ID, String.valueOf(item.ORDER_ID));
        startActivity(intent);
    }

    /**
     * 邀请医生
     * @param item
     */
    @Override
    public void onInviteClick(ServiceOrderBean item) {
        Intent intent = StationMemberChoiceAty.getCallingIntent(getContext(), Constant.ChoiceType.YQ, site_id, String.valueOf(item.ORDER_ID), item.GROUP_ID);
        startActivity(intent);
    }
}
