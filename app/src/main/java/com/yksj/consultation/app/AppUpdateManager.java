package com.yksj.consultation.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.library.base.base.BaseDialog;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.dialog.MessageDialog;
import com.yksj.consultation.bean.AppUpdataInfoBean;
import com.yksj.consultation.service.AppUpdataService;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.SharePreHelper;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Request;

/**
 * 版本更新管理类
 */
public class AppUpdateManager {
    private WeakReference<FragmentActivity> mWeakActivity;

    private static AppUpdateManager INSTANCE = null;

    private void AppUpdateManager() {}

    public static AppUpdateManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AppUpdateManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppUpdateManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * App是否有更新
     * @return
     */
    public Observable<Boolean> isAppUpdate() {
        return requestCheckUpdate(false)
                .map(new Function<AppUpdataInfoBean, Boolean>() {
                    @Override public Boolean apply(AppUpdataInfoBean info) throws Exception {
                        return info != null && !TextUtils.isEmpty(info.version);
                    }
                });
    }

    /**
     * 检查更新
     */
    @SuppressLint("CheckResult")
    public void checkeUpdate(FragmentActivity activity, boolean isShowDialog) {
        this.mWeakActivity = new WeakReference<>(activity);
        requestCheckUpdate(isShowDialog)
                .subscribe(new Consumer<AppUpdataInfoBean>() {
                    @Override public void accept(AppUpdataInfoBean info) throws Exception {
                        if (mWeakActivity.get() != null && mWeakActivity.get().isFinishing()) {
                            mWeakActivity = null;
                            return;
                        }
                        if (info != null && !TextUtils.isEmpty(info.version)) {//有更新
                            final AppUpdataInfoBean finalInfo = info;
                            if (!isShowDialog && isDirectDonwload()) {
                                info.isNowInstall = false;//不需要安装
                                startDonwload(finalInfo);
                            } else {
                                showUpdataDialog(info);
                            }
                        } else if (isShowDialog) {//没有更新
                            showNotUpdata(info);
                        }
                    }
                });
    }

    /**
     * 获取更新数据
     * @param isShowDialog
     * @return
     */
    private Observable<AppUpdataInfoBean> requestCheckUpdate(boolean isShowDialog) {
        PublishSubject<AppUpdataInfoBean> subject = PublishSubject.create();

        String currentVersion = AppUtils.getAppVersionName();
        ApiService.doHttpCheckAppVersion(currentVersion, new ApiCallbackWrapper<AppUpdataInfoBean>(isShowDialog) {
            @Override public void onResponse(AppUpdataInfoBean response) {
                super.onResponse(response);
                subject.onNext(response);
            }

            @Override public void onError(Request request, Exception e) {
                super.onError(request, e);
                subject.onError(e);
            }
        });
        return subject;
    }

    /**
     * 显示不更新提示
     * @param info
     */
    private void showNotUpdata(AppUpdataInfoBean info) {
        MessageDialog.newInstance("", "当前为最新版本")
                .show(mWeakActivity.get().getSupportFragmentManager());
    }

    /**
     * 是否直接下载
     * @return
     */
    private boolean isDirectDonwload() {
        boolean wifiUpdata = SharePreHelper.isWifiUpdate();
        boolean isWifi = NetworkUtils.isWifiConnected();
        return isWifi && wifiUpdata;
    }

    /**
     * 显示版本更新提示
     * @param info
     */
    private void showUpdataDialog(AppUpdataInfoBean info) {
        ConfirmDialog.newInstance(String.format("%s版本更新", info.version), info.message)
                .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                    @Override public void onPositiveClick(ConfirmDialog dialog, View v) {
                        super.onPositiveClick(dialog, v);
                        // TODO 下载APK
                        info.isNowInstall = true;//需要安装
                        prepareDownload(info);
                    }
                })
                .setPositive("立即更新", R.color.main_color)
                .setNegative("下次再说", 0)
                .show(mWeakActivity.get().getSupportFragmentManager());
    }

    /**
     * 准备下载
     * @param info
     */
    private void prepareDownload(AppUpdataInfoBean info) {
        if (NetworkUtils.isMobileData()) {//移动数据链接
            showModileDataDialog(info);
        } else {
            startDonwload(info);
        }
    }

    /**
     * 显示数据流量下载提示
     * @param info
     * @return
     */
    private BaseDialog showModileDataDialog(AppUpdataInfoBean info) {
        return ConfirmDialog.newInstance("", "是否使用移动数据流量下载安装包？")
                .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                    @Override public void onPositiveClick(ConfirmDialog dialog, View v) {
                        super.onPositiveClick(dialog, v);
                        startDonwload(info);
                    }
                })
                .show(mWeakActivity.get().getSupportFragmentManager());
    }

    /**
     * 开始下载
     * @param info
     */
    private void startDonwload(AppUpdataInfoBean info) {
        Intent intent = AppUpdataService.getCallingIntent(mWeakActivity.get(), info);
        mWeakActivity.get().startService(intent);
    }
}
