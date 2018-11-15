package com.yksj.consultation.station;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.MessageDialog;
import com.library.base.utils.EventManager;
import com.yksj.consultation.adapter.StationMemberAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationMemberBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.constant.ObjectType;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.doctor.DoctorHomeActivity;
import com.yksj.consultation.event.EStationChange;
import com.yksj.consultation.event.EStationQuit;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.InviteMemActivity;
import com.yksj.consultation.sonDoc.consultation.main.QueryDoctorActivity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 医生集团成员列表
 */
public class StationMemberActivity extends BaseTitleActivity implements StationMemberAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.load_faile_layout)
    View mEmptyView;

    @BindView(R.id.join_chat_layout)
    View mJoinChatView;

    private String mStationId;
    private int mType;
    private StationMemberAdapter mAdapter;
    private String mChatId;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_member;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initIntent();
        initRecycler();
        requestData();
    }

    private void initIntent() {
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mType = getIntent().getIntExtra(Constant.Station.STATION_HOME_TYPE, -1);
        mChatId = getIntent().getStringExtra(Constant.Station.CHAT_ID);

        switch (mType) {
            case StationType.STATION_HOME_CREATE:
            case StationType.STATION_HOME_JOIN:
                ViewUtils.setGone(mJoinChatView, false);
                break;
        }
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setTitle("工作站成员");
        if (mType == StationType.STATION_HOME_CREATE) {
            setRight("成员审核", this::onInviteMemClick);
        } else if (mType == StationType.STATION_HOME_JOIN) {
            setRight("退出工作站", this::onQuitStation);
        }
    }

    private void initRecycler() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new StationMemberAdapter(mType == StationType.STATION_HOME_CREATE);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 成员审核
     * @param v
     */
    private void onInviteMemClick(View v){
        Intent intent = new Intent(StationMemberActivity.this, InviteMemActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        startActivity(intent);
    }

    /**
     * 退出工作站
     * @param v
     */
    private void onQuitStation(View v){
        DialogManager.getMessageDialog("是否要退出工作站？")
                .addListener(new MessageDialog.SimpleMessageDialogListener(){
                    @Override
                    public void onPositiveClick(MessageDialog dialog, View v) {
                        requestQuit();
                    }
                })
                .show(getSupportFragmentManager());
    }

    /**
     * 进入聊天室
     *
     * @param v
     */
    @OnClick(R.id.tv_join_chat)
    public void onJoinChat(View v) {
        Intent intent = new Intent(this, ChatActivity.class);
        GroupInfoEntity infoEntity = new GroupInfoEntity();
        infoEntity.id = mChatId;
        infoEntity.setObjectType(ObjectType.STATION_CHAT);
        intent.putExtra(Constant.Chat.KEY_PARAME, infoEntity);
        startActivity(intent);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        ApiService.OKHttpStationMembers(mStationId, new ApiCallbackWrapper<ResponseBean<List<StationMemberBean>>>(this) {
            @Override
            public void onResponse(ResponseBean<List<StationMemberBean>> response) {
                if (response.isSuccess()) {
                    List<StationMemberBean> result = response.result;
                    if (result != null && !result.isEmpty()) {
                        mAdapter.setNewData(result);
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, this);
    }

    /**
     * 请求退出工作站
     */
    private void requestQuit() {
        ApiService.OKHttpStationQuitStation(mStationId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                ToastUtils.showShort(response.message);
                EventManager.post(new EStationQuit());
                finish();
            }
        }, this);
    }

    @Override
    public void onAddClick(View view, int position) {
        Intent intent = new Intent(this, QueryDoctorActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        StationMemberBean data = mAdapter.getItem(position);
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.putExtra(Constant.Station.DOCTOR_HOME_TYPE, DoctorHomeType.DOCTOR_HOME_NORMAL);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mType);
        intent.putExtra(Constant.Station.USER_ID, data.CUSTOMER_ID);
        startActivity(intent);
    }

    /**
     * 刷新工作站成员列表
     *
     * @param event
     */
    @Subscribe
    public void onRefreshEvent(EStationChange event) {
        requestData();
    }

}
