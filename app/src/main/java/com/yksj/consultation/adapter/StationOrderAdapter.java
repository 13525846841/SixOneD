package com.yksj.consultation.adapter;

import android.annotation.SuppressLint;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.blankj.utilcode.constant.TimeConstants;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.ServiceOrderBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ${chen} on 2017/4/6.
 */
public class StationOrderAdapter extends BaseQuickAdapter<ServiceOrderBean, BaseViewHolder> {

    private static final int DEFAULT_SERVICE_END = 1000 * 60 * 60 * 24;

    public String type;
    private OnOrderClickListener mListener;
    private SparseArrayCompat mViewHelpers;
    private Disposable mSubscribe;

    public StationOrderAdapter(String type) {
        super(R.layout.item_service_order);
        this.type = type;
        if (Constant.StationOrderStatus.QD.equals(type) ||
                Constant.StationOrderStatus.ZZFP.equals(type) ||
                Constant.StationOrderStatus.FWZ.equals(type)) {
            mViewHelpers = new SparseArrayCompat();
            startLoopRefresh();
        }
    }

    /**
     * 开启循环任务刷新结束时间
     */
    @SuppressLint("CheckResult")
    private void startLoopRefresh() {
        mSubscribe = Flowable.interval(1000, TimeUnit.MILLISECONDS)
                             .flatMap((Function<Long, Publisher<ServiceOrderBean>>) aLong -> Flowable.fromIterable(getData()))
                             .filter(orderBean -> mViewHelpers.size() > 0)
                             .map(orderBean -> computeTime(orderBean))
                             .filter(orderBean -> !TextUtils.isEmpty(orderBean.differenceTime))
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe(orderBean -> {
                                 if (!TextUtils.isEmpty(orderBean.differenceTime)) {
                                     ((TextView) mViewHelpers.get(orderBean.adapterInPosition)).setText(String.format("距离结束：%s", orderBean.differenceTime));
                                 } else {
                                     ((TextView) mViewHelpers.get(orderBean.adapterInPosition)).setText("已取消");
                                     mViewHelpers.remove(orderBean.adapterInPosition);
                                 }
                             });
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceOrderBean item) {
        if (Constant.StationOrderStatus.QD.equals(type)) {
            dealQD(helper, item);
        } else if (Constant.StationOrderStatus.FWZ.equals(type)) {
            dealFWZ(helper, item);
        } else if (Constant.StationOrderStatus.ZZFP.equals(type)) {
            dealZZFP(helper, item);
        } else if (Constant.StationOrderStatus.QDSUCESS.equals(type)) {
            dealQDSUCESS(helper, item);
        } else {
            helper.setGone(R.id.title, false);
            helper.setGone(R.id.btn1, false);
            helper.setGone(R.id.btn2, false);
        }
        helper.setGone(R.id.tv_order_number, !TextUtils.isEmpty(item.PAY_ID));
        helper.setText(R.id.tv_order_number, String.format("订单号: %s", item.PAY_ID));

        helper.setGone(R.id.tv_order_time, !TextUtils.isEmpty(item.SERVICE_START) || !TextUtils.isEmpty(item.ORDER_CREATE_TIME));
        helper.setText(R.id.tv_order_time, String.format("开始时间：%s", !HStringUtil.isEmpty(item.SERVICE_START) ?
                TimeUtil.getFormatDate(item.SERVICE_START) :
                TimeUtil.getFormatDate(item.ORDER_CREATE_TIME)));

        helper.setGone(R.id.end_time, !TextUtils.isEmpty(item.SERVICE_END));
        helper.setText(R.id.end_time, TimeUtil.getFormatDate(item.SERVICE_END));

        helper.setGone(R.id.tv_circle, item.SERVICE_CYCLE != 0);
        helper.setText(R.id.tv_circle, String.format("服务周期：%s小时", item.SERVICE_CYCLE));

        helper.setText(R.id.tv_order_money, String.format("服务费用：%s元", TextUtils.isEmpty(item.SERVICE_GOLD) ? "0" : item.SERVICE_GOLD));

        helper.setGone(R.id.tv_dark_fail_reason, !TextUtils.isEmpty(item.SERVICE_SOURCE));
        helper.setText(R.id.tv_dark_fail_reason, String.format("服务来源：%s", item.SERVICE_SOURCE));

        helper.setGone(R.id.darkbacktext, !TextUtils.isEmpty(item.getStatus()));
        helper.setText(R.id.darkbacktext, item.getStatus());

        helper.setGone(R.id.tv_sex, false);
//        helper.setText(R.id.tv_sex, item.CUSTOMER_SEX.equals("W") ? "女" : "男");
        helper.setText(R.id.tv_sex,  "女" );

        helper.setGone(R.id.tv_year, item.AGE != 0);
        helper.setText(R.id.tv_year, String.format("%s岁", item.AGE));

        helper.setText(R.id.name_tv, HStringUtil.isEmpty(item.CUSTOMER_NICKNAME) ? "匿名" : item.CUSTOMER_NICKNAME);

        String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.BIG_ICON_BACKGROUND;
        ImageLoader.loadAvatar(url).into(helper.getView(R.id.det_img_head));
    }

    /**
     * 待抢单
     */
    private void dealQDSUCESS(BaseViewHolder helper, ServiceOrderBean item) {
        helper.setGone(R.id.btn1, false);
        helper.setGone(R.id.btn2, false);
        helper.setGone(R.id.title, true);
        item = computeTime(item);
        if (isServiceCancel(item)) {
            helper.setText(R.id.title, "已取消");
            helper.setBackgroundRes(R.id.title, R.color.refresh_bottom_color);
            helper.getView(R.id.btn1).setEnabled(false);
            helper.getView(R.id.btn2).setEnabled(false);
        } else {
            item.adapterInPosition = helper.getAdapterPosition();
            mViewHelpers.put(item.adapterInPosition, helper.getView(R.id.title));
            helper.setText(R.id.title, String.format("距离结束：%s", item.differenceTime));
        }
    }

    /**
     * 站长分配
     * @param helper
     * @param item
     */
    private void dealZZFP(BaseViewHolder helper, ServiceOrderBean item) {
        helper.setGone(R.id.btn1, true);
        helper.setGone(R.id.btn2, true);
        helper.setGone(R.id.title, true);
        ServiceOrderBean finalItem = item;
        helper.setText(R.id.btn1, "分配");
        helper.setOnClickListener(R.id.btn1, v -> {
            if (mListener != null) mListener.onDispatchClick(finalItem);//分配
        });
        helper.setText(R.id.btn2, "接单");
        helper.setOnClickListener(R.id.btn2, v -> {
            if (mListener != null) mListener.onGrabClick(finalItem);//接单
        });
        item = computeTime(item);
        if (isServiceCancel(item)) {
            helper.setText(R.id.title, "已取消");
            helper.setBackgroundRes(R.id.title, R.color.refresh_bottom_color);
            helper.getView(R.id.btn1).setEnabled(false);
            helper.getView(R.id.btn2).setEnabled(false);
        } else {
            item.adapterInPosition = helper.getAdapterPosition();
            mViewHelpers.put(item.adapterInPosition, helper.getView(R.id.title));
            helper.setText(R.id.title, String.format("距离结束：%s", item.differenceTime));
        }
    }

    /**
     * 服务中订单
     * @param helper
     * @param item
     */
    private void dealFWZ(BaseViewHolder helper, ServiceOrderBean item) {
        helper.setGone(R.id.btn1, true);
        helper.setGone(R.id.btn2, true);
        helper.setGone(R.id.title, true);
        helper.setText(R.id.btn1, "对话");
        ServiceOrderBean finalItem = item;
        helper.setOnClickListener(R.id.btn1, v -> {
            if (mListener != null) mListener.onChatClick(finalItem);//对话
        });
        helper.setText(R.id.btn2, "邀请");
        helper.setOnClickListener(R.id.btn2, v -> {
            if (mListener != null) mListener.onInviteClick(finalItem);
        });
        item = computeTime(item);
        if (isServiceCancel(item)) {
            helper.setText(R.id.title, "已取消");
            helper.setBackgroundRes(R.id.title, R.color.refresh_bottom_color);
        } else {
            item.adapterInPosition = helper.getAdapterPosition();
            mViewHelpers.put(item.adapterInPosition, helper.getView(R.id.title));
            helper.setText(R.id.title, String.format("距离结束：%s", item.differenceTime));
        }
    }

    /**
     * 抢单
     * @param helper
     * @param item
     */
    private void dealQD(BaseViewHolder helper, ServiceOrderBean item) {
        helper.setGone(R.id.btn1, false);
        helper.setGone(R.id.btn2, false);
        helper.setGone(R.id.title, true);
        item = computeTime(item);
        if (isServiceCancel(item)) {
            helper.setText(R.id.title, "已取消");
            helper.setBackgroundRes(R.id.title, R.color.refresh_bottom_color);
        } else {
            item.adapterInPosition = helper.getAdapterPosition();
            mViewHelpers.put(item.adapterInPosition, helper.getView(R.id.title));
            helper.setText(R.id.title, String.format("距离结束：%s", item.differenceTime));
        }
    }

    /**
     * 计算结束时间
     * @param orderBean 订单
     * @return
     */
    private ServiceOrderBean computeTime(ServiceOrderBean orderBean) {
        long startServiceTime = TimeUtil.formatMillion(TextUtils.isEmpty(orderBean.SERVICE_START) ? orderBean.ORDER_CREATE_TIME : orderBean.SERVICE_START);
//        if (orderBean.SERVICE_CYCLE == 0) {
//            return orderBean;
//        }
        long endServiceTime = startServiceTime + DEFAULT_SERVICE_END;
        orderBean.differenceTime = computeTime(endServiceTime);
        return orderBean;
    }

    /**
     * 计算工作站订单结束时间
     * @param endServiceTime
     * @return
     */
    @SuppressLint("DefaultLocale")
    private String computeTime(Long endServiceTime) {
        long nowTime = System.currentTimeMillis();
        long diff = endServiceTime - nowTime;
        int day = (int) (diff / TimeConstants.DAY);//天
        int hours = (int) ((diff - day * TimeConstants.DAY) / TimeConstants.HOUR);//时
        int minutes = (int) ((diff - day * TimeConstants.DAY - hours * TimeConstants.HOUR) / TimeConstants.MIN);//分
        int second = (int) ((diff - day * TimeConstants.DAY - hours * TimeConstants.HOUR - minutes * TimeConstants.MIN) / 1000);//秒
        if (day > 0) {
            return String.format("%d天%d时%d分%d秒", day, hours, minutes, second);
        } else if (hours > 0) {
            return String.format("%d时%d分%d秒", hours, minutes, second);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, second);
        } else if (second > 0) {
            return String.format("%d秒", second);
        } else {
            return "";
        }
    }

    /**
     * 活动是否已取消
     * @param item
     * @return
     */
    private boolean isServiceCancel(ServiceOrderBean item) {
        return TextUtils.isEmpty(item.differenceTime)/* || item.REMAININGTIME.startsWith("-")*/;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
            mSubscribe = null;
        }
        if (mViewHelpers != null && mViewHelpers.size() > 0) {
            mViewHelpers.clear();
            mViewHelpers = null;
        }
    }

    public void setOrderClickListener(OnOrderClickListener listener) {
        this.mListener = listener;
    }

    public interface OnOrderClickListener {
        void onDispatchClick(ServiceOrderBean item);

        void onGrabClick(ServiceOrderBean item);

        void onChatClick(ServiceOrderBean item);

        void onInviteClick(ServiceOrderBean item);
    }
}
