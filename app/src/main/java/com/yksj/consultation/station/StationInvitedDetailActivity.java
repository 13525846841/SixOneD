package com.yksj.consultation.station;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationDetailBean;
import com.yksj.consultation.bean.StationHeadBean;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.widget.StationExpandableDescView;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 工作站邀请详情
 */
public class StationInvitedDetailActivity extends BaseTitleActivity {

    private String mStationId;//详情ID
    private StationDetailBean mStationData;

    @BindView(R.id.iv_cover)
    ImageView mCoverView;

    @BindView(R.id.tv_station_head_name)
    TextView mStationHeadNameView;

    @BindView(R.id.tv_station_name)
    TextView mStationNameView;

    @BindView(R.id.sdv_hosptial)
    StationExpandableDescView mHosptialDescView;

    @BindView(R.id.sdv_station)
    StationExpandableDescView mStationDescView;

    @BindView(R.id.sdv_founder)
    StationExpandableDescView mFounderDescView;
    private String mCoverPath;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_invited_detail;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("工作站详情");
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        requestData();
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
                    dealSuccess();
                }
            }
        }, this);
    }

    private void dealSuccess() {
        StationHeadBean stationInfo = mStationData.siteInfo;
        mCoverPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + stationInfo.SITE_BIG_PIC;
        ImageLoader.load(mCoverPath).into(mCoverView);
        mStationNameView.setText(stationInfo.SITE_NAME);
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

        mStationHeadNameView.setText(String.format("%s%s", stationInfo.DOCTOR_NAME, stationInfo.OFFICE_NAME));
        mStationHeadNameView.setText(new SpanUtils()
                .append(stationInfo.DOCTOR_NAME)
                .setForegroundColor(Utils.getApp().getResources().getColor(R.color.color_blue))
                .append(stationInfo.OFFICE_NAME)
                .create());
    }

    /**
     * 工作站主页
     * @param v
     */
    @OnClick(R.id.station_info_layout)
    public void onStationInfoClick(View v) {
        startActivity(StationHomeActivity.getCallingIntent(this
                , StationType.STATION_HOME_NORMAL
                , mStationId));
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
     * 同意工作站的邀请
     * @param v
     */
    @OnClick(R.id.tv_agree)
    public void onAgreeInvite(View v) {
        requestStationInviteDoctor(201);
    }

    /**
     * 拒绝工作站的邀请
     * @param v
     */
    @OnClick(R.id.tv_refuse)
    public void onRefuseInvite(View v) {
        requestStationInviteDoctor(202);
    }

    /**
     * 请求工作站邀请医生状态
     * @param status
     */
    private void requestStationInviteDoctor(int status) {
        ApiService.OKHttpStationInviteDoctor(mStationId, status, new ApiCallbackWrapper<ResponseBean>() {
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
}
