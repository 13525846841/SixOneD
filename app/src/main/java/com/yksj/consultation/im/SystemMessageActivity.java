package com.yksj.consultation.im;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.EventManager;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.SystemMessageAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.event.EChatClearHide;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.MessageEntity;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.DataParseUtil;

import java.util.List;

import butterknife.BindView;

/**
 * 系统消息
 */
public class SystemMessageActivity extends BaseTitleActivity {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mChatId;
    private String mConsultationId;
    private String mObjectType;
    private SystemMessageAdapter mAdapter;
    private String mOrderId;
    private String mNextPagerId = String.valueOf(Integer.MAX_VALUE);

    @Override
    public int createLayoutRes() {
        return R.layout.activity_system_message;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mChatId = getIntent().getStringExtra(Constant.Chat.SINGLE_ID);
        mConsultationId = getIntent().getStringExtra(Constant.Chat.CONSULTATION_ID);
        mObjectType = getIntent().getStringExtra(Constant.Chat.OBJECT_TYPE);
        mOrderId = getIntent().getStringExtra(Constant.Chat.ORDER_ID);

        mRefreshLayout.setRefreshHeader(new SimpleRefreshHeader(this));
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestMessage(true));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.autoRefresh();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SystemMessageAdapter(this, mOrderId);
        mRecyclerView.setAdapter(mAdapter);

        requestUnreadWarn();
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setTitle("系统消息");
    }

    /**
     * 删除未读消息提示
     */
    private void requestUnreadWarn(){
        ApiService.deleteUnreadMessageWarn(DoctorHelper.getId(), mChatId, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean resp) {
                super.onResponse(resp);
                if (resp.isSuccess()){//未读消息删除成功
                    EChatClearHide event = new EChatClearHide();
                    event.id = mConsultationId;
                    EventManager.post(event);
                }else{
                    LogUtils.e("未读消息删除失败");
                }
            }
        });
    }

    /**
     * 请求消息数据列表
     * @param scrollBottom
     */
    public void requestMessage(boolean scrollBottom) {
        ApiService.OkHttpSystemHistoryMessage(DoctorHelper.getId(), mNextPagerId, mChatId, mObjectType, mConsultationId, new ApiCallbackWrapper<String>() {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                mRefreshLayout.finishRefresh();
                List<MessageEntity> list = DataParseUtil.parseGroupMessage(response, DoctorHelper.getId());
                mAdapter.addData(0, list);
                seteupNextId();
                if (scrollBottom) scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(mAdapter.getBottomPosition(), 0);
    }

    public void seteupNextId() {
        mNextPagerId = mAdapter.getItemCount() > 0 ? mAdapter.getItem(mAdapter.getData().size() - 1).getId() : String.valueOf(Integer.MAX_VALUE);
    }
}
