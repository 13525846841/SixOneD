package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.EventManager;
import com.library.base.utils.ResourceHelper;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.adapter.StationMemberAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationCommentBean;
import com.yksj.consultation.bean.StationDetailBean;
import com.yksj.consultation.bean.StationHeadBean;
import com.yksj.consultation.bean.StationMemberBean;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.doctor.DoctorHomeActivity;
import com.yksj.consultation.event.EStationChange;
import com.yksj.consultation.event.EStationQuit;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.BarCodeActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.widget.StationExpandableDescView;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.functions.Consumer;


/**
 * 工作站详情
 */
public class StationHomeActivity extends BaseTitleActivity implements StationMemberAdapter.OnItemClickListener {

    private int mHomeType;//详情类型
    private String mStationId;//详情ID
    private StationDetailBean mStationData;

    @BindView(R.id.tv_station_head_name)
    TextView mStationHeadNameView;

    @BindView(R.id.tv_station_name)
    TextView mStationNameView;

    @BindView(R.id.footer_layout)
    View mFooterLayout;

    @BindView(R.id.tv_comment_name)
    TextView mCommentNameTv;

    @BindView(R.id.tv_comment_content)
    TextView mCommentContentTv;

    @BindView(R.id.rcv_menber)
    RecyclerView mMemberRcv;

    @BindView(R.id.comment_layout)
    View mCommentLayout;

    @BindView(R.id.hosptial_layout)
    View mHosptialLayout;

    @BindView(R.id.member_layout)
    View mMemberLayout;

    @BindView(R.id.tv_comment_more)
    TextView mCommentMoreView;

    @BindView(R.id.iv_cover)
    ImageView mCoverView;

    @BindView(R.id.sdv_hosptial)
    StationExpandableDescView mHosptialDescView;

    @BindView(R.id.sdv_station)
    StationExpandableDescView mStationDescView;

    @BindView(R.id.sdv_founder)
    StationExpandableDescView mFounderDescView;

    private StationMemberAdapter mMemberAdapter;
    private String mCoverPath;

    public static Intent getCallingIntent(Context context, int type, String id) {
        Intent intent = new Intent(context, StationHomeActivity.class);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, type);
        intent.putExtra(Constant.Station.STATION_ID, id);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_home;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mHomeType = getIntent().getIntExtra(Constant.Station.STATION_HOME_TYPE, StationType.STATION_HOME_NORMAL);
        visibalByType();
        initializeView();
        requestData();
    }

    private void initializeView() {
        setTitle("工作站主页");
        mHosptialDescView.setOnEditClickListener(this::onHosptialDescEditClick);
        mStationDescView.setOnEditClickListener(this::onStationDescEditClick);
        mFounderDescView.setOnEditClickListener(this::onFounderDescEditClick);

        mMemberRcv.setLayoutManager(new GridLayoutManager(this, 3));
        mMemberAdapter = new StationMemberAdapter(false);
        mMemberAdapter.setOnItemClickListener(this);
        mMemberRcv.setAdapter(mMemberAdapter);
    }

    /**
     * 根据工作站类型显示或隐藏界面
     */
    private void visibalByType() {
        switch (mHomeType) {
            case StationType.STATION_HOME_CREATE:
            case StationType.STATION_HOME_JOIN:
                setRight("订单管理", this::onOrderHandle);
                break;
            case StationType.STATION_HOME_RECOMMEND:
                ViewUtils.setGone(mFooterLayout, false);
                setRight("", null);
                break;
            case StationType.STATION_HOME_NORMAL:
                break;
        }
    }

    /**
     * 编辑医院介绍
     * @param v
     */
    private void onHosptialDescEditClick(View v) {
        AddTextActivity.from(this)
                       .setTitle("医院介绍")
                       .setContent(mHosptialDescView.getContent())
                       .setListener((v1, content, activity) -> {
                           requestUpdataStation(null, null, content, null);
                           activity.finish();
                       }).startActivity();
    }

    /**
     * 编辑工作站介绍
     * @param v
     */
    private void onStationDescEditClick(View v) {
        AddTextActivity.from(this)
                       .setTitle("工作站介绍")
                       .setContent(mStationDescView.getContent())
                       .setListener((v1, content, activity) -> {
                           requestUpdataStation(content, null, null, null);
                           activity.finish();
                       }).startActivity();
    }

    /**
     * 编辑站长简介
     * @param v
     */
    private void onFounderDescEditClick(View v) {
        AddTextActivity.from(this)
                       .setTitle("站长介绍")
                       .setContent(mFounderDescView.getContent())
                       .setListener((v1, content, activity) -> {
                           requestUpdataStation(null, null, null, content);
                           activity.finish();
                       }).startActivity();
    }

    /**
     * 加载数据
     */
    private void requestData() {
        ApiService.OKHttpStationDetail(mStationId, new ApiCallbackWrapper<ResponseBean<StationDetailBean>>(this) {
            @Override
            public void onResponse(ResponseBean<StationDetailBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mStationData = response.result;
                    fillContent();
                }
            }
        }, this);
    }

    /**
     * 填充数据
     */
    private void fillContent() {
        StationHeadBean stationInfo = mStationData.siteInfo;
        if (stationInfo != null) {
            mStationNameView.setText(stationInfo.SITE_NAME);
            mStationHeadNameView.setText(String.format("%s%s", stationInfo.DOCTOR_NAME, stationInfo.OFFICE_NAME));
            mStationHeadNameView.setText(new SpanUtils().append(stationInfo.DOCTOR_NAME).setForegroundColor(ResourceHelper.getColor(R.color.color_blue)).append(stationInfo.OFFICE_NAME).create());

            mCoverPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + stationInfo.SITE_BIG_PIC;
            ImageLoader.load(mCoverPath).into(mCoverView);

            //不是创建人不能编辑
            if (DoctorHelper.isSelf(stationInfo.SITE_CREATEOR)) {
                mHosptialDescView.visibalEdit(true);
                mStationDescView.visibalEdit(true);
                mFounderDescView.visibalEdit(true);
            }

            //医院介绍
            String hospitalDesc = stationInfo.HOSPITAL_DESC;
            mHosptialDescView.setTitle("医院介绍");
            mHosptialDescView.setContent(hospitalDesc);

            //工作站介绍
            String siteDesc = stationInfo.SITE_DESC;
            mStationDescView.setTitle("工作站介绍");
            mStationDescView.setContent(siteDesc);

            //站长介绍
            String siteCreateorDesc = stationInfo.SITE_CREATEOR_DESC;
            mFounderDescView.setTitle("站长介绍");
            mFounderDescView.setContent(siteCreateorDesc);
        }

        //评论
        StationCommentBean stationCommentBean = mStationData.siteeValuate;
        if (stationCommentBean != null) {
            mCommentNameTv.setText(String.format("%s:", stationCommentBean.CUSTOMER_NAME));
            mCommentContentTv.setText(stationCommentBean.EVALUATE_CONTENT);
        } else {
            ViewUtils.setGone(mCommentLayout, true);
        }

        if (!mStationData.siteMember.isEmpty()) {
            mMemberAdapter.setNewData(mStationData.siteMember);
        } else {
            ViewUtils.setGone(mMemberLayout, true);
        }
    }

    /**
     * 封面点击
     * @param v
     */
    @OnClick(R.id.iv_cover)
    public void onCoverClick(View v) {
        ImageBrowserActivity.BrowserSpace
                .from(this)
                .setImagePath(mCoverPath)
                .startActivity();
    }

    /**
     * 封面长按编辑
     * @param v
     * @return
     */
    @OnLongClick(R.id.iv_cover)
    public boolean onCoverLongClick(View v) {
        if (mHomeType != StationType.STATION_HOME_CREATE) {
            return false;
        }
        SelectorDialog
                .newInstance(new String[]{"本地照片", "拍摄"})
                .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                    @Override
                    public void onItemClick(SelectorDialog dialog, int position) {
                        switch (position) {
                            case 1:
                                RxChooseHelper.captureImage(StationHomeActivity.this, 3, 2)
                                              .subscribe(new Consumer<String>() {
                                                  @Override
                                                  public void accept(String capturePath) throws Exception {
                                                      requestUpdataBg(capturePath);
                                                  }
                                              });
                                break;
                            case 0:
                                RxChooseHelper.chooseImage(StationHomeActivity.this, 3, 2)
                                              .subscribe(new Consumer<String>() {
                                                  @Override
                                                  public void accept(String s) throws Exception {
                                                      requestUpdataBg(s);
                                                  }
                                              });
                                break;
                        }
                    }
                })
                .show(getSupportFragmentManager());
        return true;
    }

    /**
     * 查看工作站二维码
     * @param v
     */
    @OnClick(R.id.look_station_barcode)
    public void onStationBarcode(View v) {
        BarCodeActivity.from(this)
                       .setId(mStationData.siteInfo.SITE_CREATEOR)
                       .setQrPath(mStationData.qrCodeUrl)
                       .setName(mStationData.siteInfo.SITE_NAME)
                       .setAvatarPath(mCoverPath)
                       .setTitle(mStationData.siteInfo.SITE_NAME)
                       .toStart();
    }

    /**
     * 更多用户评论
     * @param v
     */
    @OnClick(R.id.tv_comment_more)
    public void onCommentMore(View v) {
        Intent intent = new Intent(this, StationCommentListActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        startActivity(intent);
    }

    /**
     * 加入工作站
     * @param v
     */
    @OnClick(R.id.tv_join_station)
    public void onJoinStation(View v) {
        AddTextActivity.from(this)
                       .setTitle("申请加入")
                       .setListener(new AddTextActivity.OnAddTextClickListener() {
                           @Override
                           public void onConfrimClick(View v, String content, AddTextActivity activity) {
                               activity.finish();
                               requestApplyJoin(content);
                           }
                       })
                       .startActivity();
    }

    /**
     * 成员列表
     * @param view
     */
    @OnClick({R.id.menber_more, R.id.station_member})
    public void onGotoMumber(View view) {
        Intent intent = new Intent(this, StationMemberActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mHomeType);
        intent.putExtra(Constant.Station.CHAT_ID, mStationData.siteInfo.GROUP_ID);
        startActivity(intent);
    }

    /**
     * 订单处理
     */
    public void onOrderHandle(View v) {
        Intent intent = new Intent(this, StationOrderPagerActivity.class);
        intent.putExtra(Constant.Station.SERVICE_TYPE_ID, "6");
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mHomeType);
        startActivity(intent);
    }

    /**
     * 健康讲堂
     * @param v
     */
    @OnClick({R.id.station_lecture_room})
    public void onLectureRoom(View v) {
        Intent intent = new Intent(this, LectureHomeActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mHomeType);
        startActivity(intent);
    }

    /**
     * 咨询设置
     * @param v
     */
    @OnClick({R.id.ll_query_setting})
    public void onQuerySetting(View v) {
        Intent intent = new Intent(this, StationServiceActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mHomeType);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        StationMemberBean data = mMemberAdapter.getItem(position);
        Intent intent = new Intent(this, DoctorHomeActivity.class);
        intent.putExtra(Constant.Station.DOCTOR_HOME_TYPE, DoctorHomeType.DOCTOR_HOME_NORMAL);
        intent.putExtra(Constant.Station.STATION_ID, mStationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, mHomeType);
        intent.putExtra(Constant.Station.USER_ID, data.CUSTOMER_ID);
        startActivity(intent);
    }

    @Override
    public void onAddClick(View view, int position) {
    }

    /**
     * 刷新工作站数据
     * @param e
     */
    @Subscribe
    public void onRefreshEvent(EStationChange e) {
        mCoverView.postDelayed(() -> requestData(), 200);
    }

    /**
     * 退出工作站事件
     * @param event
     */
    @Subscribe
    public void stationQuitEvent(EStationQuit event) {
        mHomeType = StationType.STATION_HOME_RECOMMEND;
        visibalByType();
        mCoverView.postDelayed(() -> requestData(), 200);
    }

    /**
     * 更新工作站背景图片
     */
    private void requestUpdataBg(String coverPath) {
        ApiService.OkHttpStationCoverSetting(mStationId, new File(coverPath), new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                ToastUtils.showShort(response.message);
                if (response.isSuccess()) {
                    mCoverPath = coverPath;
                    EventManager.post(new EStationChange());
                }
            }
        });
    }

    /**
     * 更新工作站信息
     * @param stationDesc
     * @param stationName
     * @param hospitalDesc
     * @param founderDesc
     */
    private void requestUpdataStation(String stationDesc, String stationName, String hospitalDesc, String founderDesc) {
        ApiService.OkHttpStationUpdata(mStationId, stationDesc, stationName, hospitalDesc, founderDesc, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    requestData();
                }
                ToastUtils.showShort(response.message);
            }
        });
    }

    /**
     * 医生集团申请理由
     */
    private void requestApplyJoin(String reason) {
        if (HStringUtil.isEmpty(reason)) {
            ToastUtil.showShort("内容不能为空");
            return;
        }
        ApiService.OKHttpStationApplyJoinStation(mStationId, reason, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                ToastUtils.showShort(response.message);
            }
        }, this);
    }
}
