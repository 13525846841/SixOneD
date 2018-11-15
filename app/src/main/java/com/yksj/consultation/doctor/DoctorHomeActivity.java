package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.EventManager;
import com.library.base.utils.ResourceHelper;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.StationMemDetDuctorServiceAdapter;
import com.yksj.consultation.adapter.StationMemDetStaAdapter;
import com.yksj.consultation.adapter.StationMemDetStaServiceAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.DoctorToolsBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationBean;
import com.yksj.consultation.bean.StationDoctorBean;
import com.yksj.consultation.bean.DoctorServiceBean;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.event.EStationChange;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.BarCodeActivity;
import com.yksj.consultation.sonDoc.doctor.DoctorCommentListActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.widget.DoctorAvatarView;
import com.yksj.consultation.widget.StationExpandableDescView;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

;

/**
 * 医生主页
 */
public class DoctorHomeActivity extends BaseTitleActivity {
    public int mType;//1邀请成员列表进入 2 从成员列表进入  3 申请中进入 4 邀请中

    private String mDoctorId;//医生ID
    private String mStationId;
    private int mApplyStatus;
    private String mApplyReason;

    @BindView(R.id.tv_doc_name)
    TextView mNameView;

    @BindView(R.id.tv_doc_account)
    TextView mAccountView;

    @BindView(R.id.tv_doc_address)
    TextView mAddressView;

    @BindView(R.id.tv_doc_pro)
    TextView mProView;

    @BindView(R.id.tv_doc_room)
    TextView mRoomView;

    @BindView(R.id.tv_doc_place)
    TextView mPlaceView;

    @BindView(R.id.rv_join_station)
    RecyclerView mJoinStationRv;

    @BindView(R.id.join_station_label)
    TextView mJoinStationLabelView;

    @BindView(R.id.service_label)
    TextView mStationServiceLabelView;

    @BindView(R.id.rv_station_service)
    RecyclerView mStationServiceRv;

    @BindView(R.id.rcv_ductor_service)
    RecyclerView mDoctorServiceRv;

    @BindView(R.id.tv_comment_name)
    TextView mCommentNameView;

    @BindView(R.id.tv_comment_content)
    TextView mCommentContentView;

    @BindView(R.id.comment_layout)
    View mCommentLayout;

    @BindView(R.id.iv_doc_head)
    DoctorAvatarView mAvatarView;

    @BindView(R.id.tv_invite_reason)
    TextView mApplyReasonView;

    @BindView(R.id.apply_layout)
    View mApplyLayout;

    @BindView(R.id.footer_layout)
    View mFooterLayout;

    @BindView(R.id.tv_invite_join_station)
    TextView mInviteView;

    @BindView(R.id.sev_doctor_desc)
    StationExpandableDescView mDoctorDescView;

    @BindView(R.id.ll_doc_tools)
    View mToolsLayout;

    @BindView(R.id.scroll_view)
    NestedScrollView mScrollView;

    @BindView(R.id.service_divider)
    View mServiceDivider;

    @BindView(R.id.top_bg)
    View mTopbgView;

    private StationMemDetStaAdapter mJoinStationAdapter;
    private StationMemDetStaServiceAdapter mStationServiceAdapter;
    private StationMemDetDuctorServiceAdapter mDuctorServiceAdapter;
    private StationDoctorBean mDetailBean;

    public static Intent getCallingIntent(Context context, String doctorId) {
        Intent intent = new Intent(context, DoctorHomeActivity.class);
        intent.putExtra(Constant.Station.USER_ID, doctorId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doctor_home;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initIntent();
        initView();
        requestData();
    }

    private void initView() {
        DividerListItemDecoration itemDecoration = new DividerListItemDecoration(LinearLayoutManager.VERTICAL, ConvertUtils.dp2px(1), getResources().getColor(R.color.divider));
        //加入的工作站
        mJoinStationRv.addItemDecoration(itemDecoration);
        mJoinStationRv.setLayoutManager(new LinearLayoutManager(this));
        mJoinStationAdapter = new StationMemDetStaAdapter();
        mJoinStationAdapter.bindToRecyclerView(mJoinStationRv);
        //工作站的服务
        mStationServiceRv.setLayoutManager(new LinearLayoutManager(this));
        mStationServiceAdapter = new StationMemDetStaServiceAdapter();
        mStationServiceAdapter.bindToRecyclerView(mStationServiceRv);
        //医生的服务
        mDoctorServiceRv.setLayoutManager(new LinearLayoutManager(this));
        mDuctorServiceAdapter = new StationMemDetDuctorServiceAdapter();
        mDuctorServiceAdapter.bindToRecyclerView(mDoctorServiceRv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTitleView.setElevation(0);
            mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int elevation = ResourceHelper.getDimens(R.dimen.title_elevation);
                mTitleView.setElevation(scrollY > mTopbgView.getMeasuredHeight() ? elevation : 0);
            });
        }
    }

    private void initIntent() {
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mType = getIntent().getIntExtra(Constant.Station.DOCTOR_HOME_TYPE, -1);
        mDoctorId = getIntent().getStringExtra(Constant.Station.USER_ID);
        mApplyReason = getIntent().getStringExtra(Constant.Station.STATION_APPLY_REASON);
    }

    /**
     * 头像点击
     * @param v
     */
    @OnClick(R.id.iv_doc_head)
    public void onHeadClick(View v) {
        ImageBrowserActivity.BrowserSpace
                .from(this)
                .setImagePath(AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + mDetailBean.BIG_ICON_BACKGROUND)
                .startActivity();
    }

    /**
     * 工作站点击
     * @param v
     */
    @OnClick(R.id.lay_station)
    public void onStationClick(View v) {
        Intent intent = DoctorStationListActivity.getCallingIntent(this);
        startActivity(intent);
    }

    /**
     * 二维码
     * @param v
     */
    @OnClick(R.id.ll_barcode)
    public void onLookBarcode(View v) {
        if (mDetailBean == null) return;

        BarCodeActivity.from(this)
                .setId(mDoctorId)
                .setName(mDetailBean.DOCTOR_REAL_NAME)
                .setHospital(mDetailBean.DOCTOR_HOSPITAL)
                .setSecendOffice(mDetailBean.OFFICE_NAME)
                .setTitle(mDetailBean.TITLE_NAME)
                .toStart();
    }

    /**
     * 查看评论
     * @param v
     */
    @OnClick(R.id.more_and_more_4)
    public void onMoreComment(View v) {
        if (mDetailBean == null) return;
        Intent intent = new Intent(this, DoctorCommentListActivity.class);
        intent.putExtra(Constant.USER_ID, mDetailBean.CUSTOMER_ID);
        startActivity(intent);
    }

    /**
     * 邀请加入工作站
     * @param v
     */
    @OnClick(R.id.tv_invite_join_station)
    public void onClickInviteJoinStation(View v) {
        if (mDetailBean == null) return;
        requestInviteJoinStation();
    }

    /**
     * 同意加入工作站
     * @param v
     */
    @OnClick(R.id.tv_agree)
    public void onClickAgree(View v) {
        if (mDetailBean == null) return;
        mApplyStatus = 101;
        requestChangeDoctorStatue(String.valueOf(mApplyStatus));
    }

    /**
     * 拒绝加入工作站
     * @param v
     */
    @OnClick(R.id.tv_refuse)
    public void onClickRefuse(View v) {
        if (mDetailBean == null) return;
        mApplyStatus = 102;
        requestChangeDoctorStatue(String.valueOf(mApplyStatus));
    }

    /**
     * 名医分享
     * @param v
     */
    @OnClick(R.id.share_layout)
    public void onClickShare(View v) {
        if (mDetailBean == null) return;
        Intent intent = new Intent(this, ShareListActivity.class);
        intent.putExtra(Constant.USER_ID, mDoctorId);
        startActivity(intent);
    }

    /**
     * 工具箱
     * @param v
     */
    @OnClick(R.id.ll_doc_tools)
    public void onToolsClick(View v) {
        if (mDetailBean == null) return;
        Intent intent = DoctorToolsListActivity.getCallingIntent(this, mDetailBean.CUSTOMER_ID);
        startActivity(intent);
    }

    /**
     * 个人讲堂
     * @param v
     */
    @OnClick(R.id.ll_personal_lecture)
    public void onPersonalLectureClick(View v) {
        Intent intent = DoctorLectureListActivity.getCallingIntent(this, mDoctorId);
        startActivity(intent);
    }

    /**
     * 填充数据
     */
    private void fillContent() {
        ImageLoader.loadAvatar(AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + mDetailBean.BIG_ICON_BACKGROUND)
                .into(mAvatarView);
        setTitle(String.format("%s医生主页", mDetailBean.DOCTOR_REAL_NAME));
        mNameView.setText(mDetailBean.DOCTOR_REAL_NAME);
        mAddressView.setText(mDetailBean.WORK_LOCATION_DESC);
        mProView.setText(mDetailBean.TITLE_NAME);
        mRoomView.setText(mDetailBean.OFFICE_NAME);
        mPlaceView.setText(mDetailBean.DOCTOR_HOSPITAL);
        mDoctorDescView.setTitle("医生介绍");
        mDoctorDescView.setContent(TextUtils.isEmpty(mDetailBean.INTRODUCTION) ? "这家伙很懒，什么都没有写。" : mDetailBean.INTRODUCTION);
        mApplyReasonView.setText(mApplyReason);

        //加入的工作站信息
        List<StationBean> siteDesc = mDetailBean.siteDesc;
        if (siteDesc != null && !siteDesc.isEmpty()) {
            mJoinStationAdapter.addData(mDetailBean.siteDesc);
        } else {
            mJoinStationRv.setVisibility(View.GONE);
            mJoinStationLabelView.setVisibility(View.GONE);
        }

        //工作站服务
        List<DoctorServiceBean> siteService = mDetailBean.siteService;
        if (siteService != null && !siteService.isEmpty()) {
            mStationServiceAdapter.addData(siteService);
        } else {
            mStationServiceRv.setVisibility(View.GONE);
        }

        //医生服务
        List<DoctorServiceBean> doctorService = mDetailBean.doctorService;
        if (doctorService != null && !doctorService.isEmpty()) {
            mDuctorServiceAdapter.addData(mDetailBean.doctorService);
        } else {
            mDoctorServiceRv.setVisibility(View.GONE);
        }

        mServiceDivider.setVisibility(siteService.isEmpty() || doctorService.isEmpty() ? View.GONE : View.VISIBLE);

        //医生评论
        if (mDetailBean.evaluate != null) {
            mCommentNameView.setText(mDetailBean.evaluate.CUSTOMER_NAME);
            mCommentContentView.setText(mDetailBean.evaluate.EVALUATE_CONTENT);
        } else {
            ViewUtils.setGone(mCommentLayout, true);
        }
        //工具箱
        List<DoctorToolsBean> tools = mDetailBean.tools;
        if (tools != null && !tools.isEmpty()) {
            mToolsLayout.setVisibility(View.VISIBLE);
        }

        //不能邀请自己
        if (!DoctorHelper.isSelf(mDetailBean.CUSTOMER_ID)) {
            if (mType == DoctorHomeType.DOCTOR_HOME_APPLY) {//申请的
                mFooterLayout.setVisibility(View.VISIBLE);
                mApplyLayout.setVisibility(View.VISIBLE);
            } else if (mType == DoctorHomeType.DOCTOR_HOME_INVITE) {
                mFooterLayout.setVisibility(View.VISIBLE);
                mInviteView.setVisibility(View.VISIBLE);
            } else {
                setRight("私聊", v -> FriendHttpUtil.chatFromPerson(DoctorHomeActivity.this, mDetailBean.CUSTOMER_ID, mDetailBean.DOCTOR_REAL_NAME));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ll_order_date:
//                if (888 != AUDIT) {
//                    ToastUtil.showShort("您尚未提交资质申请或审核中，不能开通此服务，若有其他问题请联系客服。");
//                } else {
//                    Intent intent3 = new Intent(this, DoctorSeeServiceActivity.class);
//                    intent3.putExtra("type", "3");
//                    intent3.putExtra("titleName", "门诊预约");
//                    startActivity(intent3);
//                }
//                break;
//            case R.id.title_right:
//                FriendHttpUtil.chatFromPerson(this, mDoctorId, name);
//                break;
        }
    }

    /**
     * 请求医生详情数据
     */
    private void requestData() {
        ApiService.OKHttpDoctorInfo(mType, mDoctorId, mStationId, new ApiCallbackWrapper<ResponseBean<StationDoctorBean>>(this) {
            @Override
            public void onResponse(ResponseBean<StationDoctorBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mDetailBean = response.result;
                    fillContent();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                finish();
            }
        }, this);
    }

    /**
     * 邀请成员
     */
    private void requestInviteJoinStation() {
        ApiService.OKHttpInviteJoinStation(mDoctorId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                ToastUtils.showShort(response.message);
                if (response.isSuccess()) {
                    finish();
                }
            }
        }, this);
    }

    /**
     * 申请状态更改（院长管理申请）
     */
    private void requestChangeDoctorStatue(String status) {
        ApiService.OKHttpStationDoctorStatue(mStationId, mDetailBean.CUSTOMER_ID, String.valueOf(status)
                , new ApiCallbackWrapper<ResponseBean>(this) {
                    @Override
                    public void onResponse(ResponseBean response) {
                        super.onResponse(response);
                        if (mApplyStatus == 101) {//同意加入工作站
                            EventManager.post(new EStationChange());
                        }
                        finish();
                        ToastUtils.showShort(response.message);
                    }
                }, this);
    }

    /**
     * 设置为助理  type 20助理  30普通成员
     */
    private void setAssis() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "settingSiteAdmin");
        map.put("site_id", mStationId);
        map.put("customer_id", mDoctorId);
//        map.put("type", "" + type);

        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    if (!HStringUtil.isEmpty(response)) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (HttpResult.SUCCESS.equals(obj.optString("code"))) {
                                ToastUtil.showShort(obj.optString("message"));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }, this);
    }
}
