package com.yksj.consultation.sonDoc.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseFragment;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.MessageHistoryAdapter;
import com.yksj.consultation.bean.MessageHistoryBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.event.EChatClearHide;
import com.yksj.consultation.event.EMainMessage;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.im.SystemMessageActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 消息列表
 * @author jack_tang
 */
public class MessageHistoryFragment extends BaseFragment implements OnBuyTicketHandlerListener, BaseQuickAdapter.OnItemClickListener, MessageHistoryAdapter.OnDoctorMessageAdapterListener {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    private MessageHistoryAdapter mAdapter;

    public static MessageHistoryFragment newInstance() {

        Bundle args = new Bundle();

        MessageHistoryFragment fragment = new MessageHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.fgt_message_history_layout;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        initView();
    }

    @Subscribe
    public void onEventMessage(EMainMessage e) {
        requestData();
    }

    private void initView() {
        SimpleRefreshHeader refreshHeader = new SimpleRefreshHeader(getContext());
        mRefreshLayout.setRefreshHeader(refreshHeader, ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(80));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(refresh -> requestData());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL));
        mAdapter = new MessageHistoryAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnDoctorMessageAdapterListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.autoRefresh();
    }

    /**
     * 获取聊天消息历史数据
     */
    private void requestData() {
        String doctorId = DoctorHelper.getId();
        ApiService.OkHttpDoctorMessage(doctorId, new ApiCallbackWrapper<ResponseBean<List<MessageHistoryBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<MessageHistoryBean>> response) {
                super.onResponse(response);
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccess()) {
                    List<MessageHistoryBean> result = response.result;
                    if (result == null || result.isEmpty()) {
                        mRecyclerView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                        mAdapter.setNewData(result);
                    }
                }
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MessageHistoryBean item = mAdapter.getItem(position);
        if (item.isGroup()) {//群聊
            Intent intent = new Intent();
            intent.putExtra(Constant.Chat.CONSULTATION_ID, item.OBJECT_ID);
            intent.putExtra(Constant.Chat.GROUP_ID, item.GROUP_ID);
            intent.putExtra(Constant.Chat.CONSULTATION_NAME, item.TARGET_NAME);
            intent.putExtra(Constant.Chat.OBJECT_TYPE, item.OBJECT_TYPE);
            intent.setClass(getActivity(), ChatActivity.class);
            startActivity(intent);
        } else if (item.isSystemMsg()) {//系统消息
            Intent intent = new Intent(getActivity(), SystemMessageActivity.class);
            intent.putExtra(Constant.Chat.CONSULTATION_ID, item.OBJECT_ID);
            intent.putExtra(Constant.Chat.SINGLE_ID, item.SEND_ID);
            intent.putExtra(Constant.Chat.SINGLE_NAME, item.TARGET_NAME);
            intent.putExtra(Constant.Chat.OBJECT_TYPE, item.OBJECT_TYPE);
            intent.putExtra(Constant.Chat.ORDER_ID, item.OBJECT_ID);
            startActivity(intent);
        } else {//人
            Intent intent = new Intent();
            intent.putExtra(Constant.Chat.CONSULTATION_ID, item.OBJECT_ID);
            String targetId = DoctorHelper.isSelf(item.SEND_ID) ? item.TARGET_ID : item.SEND_ID;
            intent.putExtra(Constant.Chat.SINGLE_ID, targetId);
            intent.putExtra(Constant.Chat.SINGLE_NAME, item.TARGET_NAME);
            intent.putExtra(Constant.Chat.OBJECT_TYPE, item.OBJECT_TYPE);
            intent.putExtra(Constant.Chat.ORDER_ID, item.OBJECT_ID);
            intent.setClass(getActivity(), ChatActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 清除已读消息提示
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventClearMessageHide(EChatClearHide e) {
        mAdapter.clearMessageHide(e.id);
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
        if ("0".equals(state)) {
        } else if ("-1".equals(state)) {
            ToastUtil.showBasicErrorShortToast(getActivity());
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
            intent1.setClass(getActivity(), ChatActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    public void onDeleteClick(View v, final int positio, MessageHistoryBean item) {
        ApiService.OkHttpDoctorDelMessage(DoctorHelper.getId(), item.OFFLINE_ID, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mAdapter.remove(positio);
                }
                ToastUtils.showShort(response.message);
            }
        });
    }
}
