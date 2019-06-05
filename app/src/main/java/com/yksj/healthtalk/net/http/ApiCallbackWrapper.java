package com.yksj.healthtalk.net.http;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.dialog.WaitDialog;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Request;

/**
 * Created by HEKL on 15/11/4.
 */
public abstract class ApiCallbackWrapper<T> extends ApiCallback<T> {
    private WeakReference<FragmentActivity> mActivityReference;
    private WaitDialog mLoadDialog;
    private boolean mShowWait;//是否显示等待dialog

    public ApiCallbackWrapper() {
    }

    /**
     * @see #ApiCallbackWrapper(boolean)
     * @param activity 不为空显示加载框
     */
    @Deprecated
    public ApiCallbackWrapper(FragmentActivity activity) {
        if (activity != null) {
            mShowWait = true;
            mActivityReference = new WeakReference<>(activity);
        }
    }

    /**
     * @param showWait 是否显示加载提示框
     */
    public ApiCallbackWrapper(boolean showWait) {
        mShowWait = showWait;
        if (mShowWait) {
            FragmentActivity activity = null;
            if (ActivityUtils.getTopActivity() instanceof FragmentActivity) {
                activity = ((FragmentActivity) ActivityUtils.getTopActivity());
            }
            if (activity != null) {
                mActivityReference = new WeakReference<>(activity);
            }
        }
    }

    /**
     * 开始请求数据
     * @param request
     */
    @Override
    public void onBefore(Request request) {
        super.onBefore(request);
        if (mShowWait && mActivityReference != null) {
            FragmentActivity activity = mActivityReference.get();
            if (!activity.isFinishing()) {
                mLoadDialog = DialogManager.getWaitDialog("请稍等...");
                mLoadDialog.setBackenable(false);
                mLoadDialog.show(activity.getSupportFragmentManager());
            }
        }
    }

    /**
     * 请求数据结束
     */
    @Override
    public void onAfter() {
        super.onAfter();
        if (mLoadDialog != null && !mLoadDialog.isDetached()) {
            mLoadDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onResponse(T response) {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity == null || activity.isFinishing()){
            return;//Activity已销毁不用处理
        }
    }

    @Override
    public void onError(Request request, Exception e) {
        LogUtils.e(e.toString());

        if (e == null || e.getCause() instanceof ConnectException) {
            ToastUtils.showShort("网络不给力或连接中断，请在设置中退出登录并重新登录。");
        } else if (e instanceof IllegalStateException) {
            ToastUtils.showShort("貌似数据出问题了！");
        } else if (e instanceof SocketTimeoutException) {
            ToastUtils.showShort("网络不给力或连接中断，请稍后再试。");
        } else {
            ToastUtils.showShort("貌似出问题了!");
        }
    }
}
