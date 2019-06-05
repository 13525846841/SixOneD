package com.yksj.consultation.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.BeanCacheHelper;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yksj.consultation.bean.LoginBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.main.MainActivity;
import com.yksj.consultation.service.CoreService;
import com.yksj.consultation.sonDoc.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 闪屏页
 */
public class SplashActivity extends BaseActivity {
    private String mAccount;
    private String mPassword;
    private LoginBusiness.SimpleLoginCallback mLoginCallback = new LoginBusiness.SimpleLoginCallback() {
        @Override
        public void onLoginSucees() {
            ToastUtils.showShort("登陆成功");
            Intent intent = MainActivity.getCallingIntent(SplashActivity.this);
//            Intent intent = RegisterDoctorActivity.getCallingIntent(SplashActivity.this, "13946058038", "123456", "0");
//            Intent intent = LectureListActivity.getCallingIntent(SplashActivity.this, "366", StationType.STATION_HOME_CREATE);
//            Intent intent = AgencyHomeActivity.getCallingIntent(SplashActivity.this);
            startActivity(intent);
            finish();
        }

        @Override
        public void onLoginError(Exception e) {
            ToastUtils.showShort(e.getMessage());
            start2Login();
        }
    };

    @Override
    public int getStatusBarColor() {
        return R.color.transparent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.logo_layout;
    }

    @Override
    @SuppressLint("CheckResult")
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        CoreService.actionStart(this);
        requestPermission()
                .subscribe(permission -> {
                    if (!permission.granted) {
                        showConfrimDialog();
                    } else {
                        startJump();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void startJump() {
        //延迟跳转
        Observable.timer(4000, TimeUnit.MILLISECONDS)
                .map(aLong -> BeanCacheHelper.load(SplashActivity.this, LoginBean.class) != null)
                .subscribe(b -> {
                    if (b) {
                        start2Main();
                    } else {
                        start2Login();
                    }
                });
    }

    /**
     * 到登陆界面
     */
    private void start2Login() {
        startActivity(UserLoginActivity.getCallingIntent(this));
        finish();
    }

    /**
     * 进入主页
     */
    private void start2Main() {
        LoginBean loginBean = BeanCacheHelper.load(this, LoginBean.class);
        mAccount = loginBean.getAccount();
        mPassword = loginBean.getPassword();
        LoginBusiness.getInstance().login(mAccount, mPassword, "0", mLoginCallback);
    }

    /**
     * 请求权限
     */
    private Observable<Permission> requestPermission() {
        return new RxPermissions(this)
                .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 显示权限未同意dialog
     */
    private void showConfrimDialog() {
        ConfirmDialog.newInstance("", "相应权限未开启，请到设置界面开启相应权限？")
                .addListener(new ConfirmDialog.SimpleConfirmDialogListener(){
                    @Override public void onPositiveClick(ConfirmDialog dialog, View v) {
                        super.onPositiveClick(dialog, v);
                        AppUtils.launchAppDetailsSettings();
                        finish();
                    }

                    @Override public void onNegativeClick(ConfirmDialog dialog, View v) {
                        super.onNegativeClick(dialog, v);
                        finish();
                    }
                }).show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        mLoginCallback = null;
        super.onDestroy();
    }
}
