package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.library.base.pay.PayResultListner;
import com.library.base.pay.PaySdkManager;
import com.library.base.pay.PaySdkModel;
import com.library.base.utils.EventManager;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.bean.OverageBean;
import com.yksj.consultation.constant.PayType;
import com.yksj.consultation.event.ELecturePaySucees;
import com.yksj.consultation.sonDoc.consultation.PConsultCouponActivity;
import com.yksj.consultation.station.view.LecturePayView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.jetbrains.annotations.NotNull;

/**
 * 订单支付
 */
public class LecturePayActivity extends BaseTitleActivity implements LecturePayView.OnPayClickListener {
    private static final String LECTURE_EXTRA = "lecture_extra";

    LecturePayView mView;

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

    public static Intent getCallingIntent(Context context, LectureBean lectureBean) {
        Intent intent = new Intent(context, LecturePayActivity.class);
        intent.putExtra(LECTURE_EXTRA, lectureBean);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new LecturePayView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("订单支付");
        mLectureBean = getIntent().getParcelableExtra(LECTURE_EXTRA);
        mView.bindData(mLectureBean);
        mView.setPayClickListener(this);
        requestOverage();
    }

    /**
     * 余额支付
     */
    private void overagePay() {
        startInfoActivity();
        EventManager.post(new ELecturePaySucees());
    }

    /**
     * 跳转详情页
     */
    private void startInfoActivity() {
        if (mLectureBean.isVideo()) {
            Intent intent = LectureVideoInfoActivity.getCallingIntent(LecturePayActivity.this, mLectureBean.COURSE_ID);
            startActivity(intent);
        } else {
            Intent intent = LectureArticleInfoActivity.getCallingIntent(LecturePayActivity.this, mLectureBean.COURSE_ID);
            startActivity(intent);
        }
        finish();
    }

    /**
     * 请求支付
     * @param payType
     */
    private void requestSdkPay(int payType) {
        ApiService.lectureOrderPay(payType
                , mLectureBean.SITE_ID
                , DoctorHelper.getId()
                , mLectureBean.getPrice()
                , mLectureBean.COURSE_ID
                , new ApiCallbackWrapper<PaySdkModel>(true) {
                    @Override
                    public void onResponse(PaySdkModel response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            switch (payType) {
                                case PayType.ALI:
                                    PaySdkManager.payAlipay(LecturePayActivity.this, response.getMalipay(), mPayResultListener);
                                    break;
                                case PayType.WECHAT:
                                    PaySdkManager.payWxPay(LecturePayActivity.this, response.getWxapay(), mPayResultListener);
                                    break;
                                case PayType.UNION:
                                    PaySdkManager.payUpApp(LecturePayActivity.this, response.getBfupwapModel(), mPayResultListener);
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 查询余额
     */
    public void requestOverage() {
        ApiService.inquireOverage(DoctorHelper.getId(), new ApiCallbackWrapper<OverageBean>() {
            @Override
            public void onResponse(OverageBean response) {
                super.onResponse(response);
                if (response.isSucees()) {
                    mView.setOverage(response.balance);
                }
            }
        });
    }

    /**
     * 支付点击事件
     * @param v
     * @param payModel
     */
    @Override
    public void onPayClick(@NotNull View v, int payModel) {
        if (payModel == PayType.OVERAGE) {
            overagePay();
        } else {
            requestSdkPay(payModel);
        }
    }

    /**
     * 优惠卷点击事件
     * @param v
     */
    @Override
    public void onCouponClick(@NotNull View v) {
        Intent intent = new  Intent(LecturePayActivity.this, PConsultCouponActivity.class);
        startActivityForResult(intent, 201);
    }
}
