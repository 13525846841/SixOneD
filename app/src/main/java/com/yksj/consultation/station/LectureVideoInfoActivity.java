package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.library.base.pay.PayResultListner;
import com.library.base.pay.PaySdkManager;
import com.library.base.pay.PaySdkModel;
import com.library.base.utils.EventManager;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.PayType;
import com.yksj.consultation.event.ELecturePaySucees;
import com.yksj.consultation.station.view.LectureVideoInfoView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

/**
 * 健康讲堂 视频详情
 */
public class LectureVideoInfoActivity extends BaseTitleActivity implements LectureVideoInfoView.IPersent {
    private static final String LECTURE_ID_EXTRA = "lecture_id_extra";

    private LectureVideoInfoView mView;

    private String mInfoId;
    private LectureBean mLectureBean;
    private PayResultListner mPayResultListener = new PayResultListner() {
        @Override
        public void onSuccess() {
            // 发送购买成功事件
            EventManager.post(new ELecturePaySucees());
            finish();
        }

        @Override
        public void onDealing() {

        }

        @Override
        public void onFail() {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onNetWork() {

        }

        @Override
        public void onOther() {

        }
    };

    public static Intent getCallingIntent(Context context, String id) {
        Intent intent = new Intent(context, LectureVideoInfoActivity.class);
        intent.putExtra(LECTURE_ID_EXTRA, id);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new LectureVideoInfoView(this, this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mInfoId = getIntent().getStringExtra(LECTURE_ID_EXTRA);
        setTitle("课程详情");
        requestInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.getPlayer().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.getPlayer().pause();
    }

    @Override
    protected void onDestroy() {
        mView.getPlayer().release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mView.getPlayer().backPresses()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mView.getPlayer().screenChange(newConfig);
    }

    /**
     * 获取课件详情
     */
    private void requestInfo() {
        ApiService.lectureInfo(mInfoId, DoctorHelper.getId(), new ApiCallbackWrapper<ResponseBean<LectureBean>>(true) {
            @Override
            public void onResponse(ResponseBean<LectureBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mLectureBean = response.result;
                    mView.bindData(mLectureBean);
                }
            }
        });
    }

    /**
     * 请求打赏
     * @param payType 支付的类型
     * @param price 支付的金额
     */
    @Override
    public void requestPay(int payType, float price) {
        ApiService.lectureReward(payType
                , mLectureBean.SITE_ID
                , DoctorHelper.getId()
                , price
                , mLectureBean.COURSE_ID
                , new ApiCallbackWrapper<PaySdkModel>(true) {
                    @Override
                    public void onResponse(PaySdkModel response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            switch (payType) {
                                case PayType.ALI:
                                    PaySdkManager.payAlipay(LectureVideoInfoActivity.this, response.getMalipay(), mPayResultListener);
                                    break;
                                case PayType.WECHAT:
                                    PaySdkManager.payWxPay(LectureVideoInfoActivity.this, response.getWxapay(), mPayResultListener);
                                    break;
                                case PayType.UNION:
                                    PaySdkManager.payUpApp(LectureVideoInfoActivity.this, response.getBfupwapModel(), mPayResultListener);
                                    break;
                            }
                        }
                    }
                });
    }
}