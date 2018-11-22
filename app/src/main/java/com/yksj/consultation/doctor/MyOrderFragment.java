package com.yksj.consultation.doctor;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseFragment;
import com.library.base.utils.ResourceHelper;
import com.library.base.widget.DividerListItemDecoration;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.ServiceOrderAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.ServiceOrderBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.ObjectType;
import com.yksj.consultation.doctor.constant.OrderType;
import com.yksj.consultation.doctor.constant.ServiceType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.AVChatActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.NetworkUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Request;

/**
 * Created by ${chen} on 2017/4/6.
 */
public class MyOrderFragment extends BaseFragment {

    public static final String TYPE_EXTRA = "type_extra";

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    private ServiceOrderAdapter mAdapter;
    private String mServiceType = "";
    @OrderType.Type private String mType;

    public static MyOrderFragment newInstance(@ServiceType.Type String serviceType, @OrderType.Type String orderType) {
        Bundle args = new Bundle();
        args.putString(Constant.Station.SERVICE_TYPE_ID, serviceType);
        args.putString(TYPE_EXTRA, orderType);

        MyOrderFragment fragment = new MyOrderFragment();
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
        mServiceType = getArguments().getString(Constant.Station.SERVICE_TYPE_ID);
        mType = getArguments().getString(TYPE_EXTRA);
        initView(view);
    }

    private void initView(View view) {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestData())
                .setEnableLoadMore(false)
                .autoRefresh();

        mRecyclerView.setAdapter(mAdapter = new ServiceOrderAdapter(mServiceType, mType));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, ResourceHelper.getDimens(R.dimen.dp_6)));
        mAdapter.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(BaseQuickAdapter adapter, View view, int i) {
//        if ("6".equals(mServiceType)) {
//
//        } else if (mServiceType.equals(ServiceType.SP) && "2".equals(mType)) {
//            try {
//                sendVideo(mAdapter.list.get(position - 1).getJSONObject("info").optString("CUSTOMER_ACCOUNTS"), mAdapter.list.get(position - 1).optString("ORDER_ID"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else if (mServiceType.equals(ServiceType.SP) && "1".equals(mType)) {
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
//            //FriendHttpUtil.chatFromPerson(getActivity(), "271780", name);
//        }
        ServiceOrderBean item = mAdapter.getItem(i);
        if (mServiceType.equals(ServiceType.TW)) {
            String chatId = item.ENJOY_CUSTOMER_ID;
            String name = item.info.CUSTOMER_NICKNAME;
            int orderId = item.ORDER_ID;
            FriendHttpUtil.chatFromPerson(getActivity(), chatId, name, String.valueOf(orderId), ObjectType.SPECIAL_SERVER);
        }
    }

    private void requestData() {
        ApiService.OKHttpFindOrderByDoctor(DoctorHelper.getId(), mServiceType, mType, new ApiCallbackWrapper<ResponseBean<List<ServiceOrderBean>>>() {

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onResponse(ResponseBean<List<ServiceOrderBean>> response) {
                super.onResponse(response);
                if (response.code == 0) {
                    List<ServiceOrderBean> orders = response.orders;
                    if (orders.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter.setNewData(orders);
                    }
                    mRefreshLayout.finishRefresh();
                }
            }
        }, this);
    }

    /**
     * 验证是否可以视频
     */
    public void sendVideo(final String account, final String order_Id) {
        Map<String, String> map = new HashMap<>();
        map.put("order_id", order_Id);
        map.put("op", "verificationOrder");
        ApiService.OKHttpConInvited(map, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("1".equals(obj.optString("code"))) {
                        DoubleBtnFragmentDialog.showDefault(getActivity().getSupportFragmentManager(), "确定要发送视频吗？", "取消", "确定",
                                new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                                    @Override
                                    public void onDismiss(DialogFragment fragment) {
                                    }

                                    @Override
                                    public void onClick(DialogFragment fragment, View v) {
                                        loginAvChat(account, order_Id);
                                    }
                                });
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    /**
     * 开启视频
     * @param account
     */
    private void checkCall(String account, String order_Id) {
        if (NetworkUtil.isNetAvailable(getActivity())) {
            startAudioVideoCall(account, order_Id);
        } else {
            ToastUtil.showShort(getResources().getString(R.string.getway_error_note));
        }
    }

    /************************ 音视频通话 ***********************/

    public void startAudioVideoCall(String account, String order_Id) {
        AVChatActivity.launch(getActivity(), account, AVChatType.VIDEO.getValue(), order_Id, AVChatActivity.FROM_INTERNAL);
        LoginBusiness.getInstance().getLoginEntity().setAvAcount(account);
//        AVChatActivity.launch(this, "15028760690", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL);
//        LoginBusiness.getInstance().getLoginEntity().setAvAcount("15028760690");
        SharePreHelper.updateAvChateState(true);
    }

    private AbortableFuture<LoginInfo> loginRequest;

    private void loginAvChat(final String tarAccount, final String order_Id) {
        checkCall(tarAccount, order_Id);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
//        String pass = "123456";
//        final String account = "15210270585";
//        final String token = MD5.getStringMD5(pass);

//        if (LoginBusiness.getInstance().getLoginEntity() != null) {
////            final String token =MD5.getStringMD5(LoginBusiness.getInstance().getLoginEntity().getAvToken()) ;
//            final String token =LoginBusiness.getInstance().getLoginEntity().getAvToken() ;
//            final String account = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount();
//            // 登录
//            loginRequest = NimUIKit.doLogin(new LoginInfo(account, token,getResources().getString(R.string.avchat_appkey)), new RequestCallback<LoginInfo>() {
//                //            loginRequest = NimUIKit.doLogin(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
//                @Override
//                public void onSuccess(LoginInfo param) {
////                LogUtil.i(TAG, "onLoginClick success");
//
//                    DemoCache.setAccount(account);
//
////                saveLoginInfo(account, token);
//
//                    // 初始化消息提醒配置
////                initNotificationConfig();
//
//                    // 进入主界面
////                startActivity(new Intent(this,LoginA));
////                MainActivity.startRecorder(LoginActivity.this, null);
////                finish();
////                if (SharePreHelper.getisAvChatState()) {
////                    checkCall();
////                }
//                }
//
//                @Override
//                public void onFailed(int code) {
//                    if (code == 302 || code == 404) {
//                        Toast.makeText(HTalkApplication.getApplication(), R.string.login_failed, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(HTalkApplication.getApplication(), "登录失败: " + code, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onException(Throwable exception) {
//                    Toast.makeText(HTalkApplication.getApplication(), R.string.login_exception, Toast.LENGTH_LONG).show();
//                }
//            });
//        }

    }
}
