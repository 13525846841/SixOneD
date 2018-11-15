package com.yksj.consultation.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.EventManager;
import com.library.base.utils.ResourceHelper;
import com.library.base.widget.SimpleRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppUpdateManager;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.doctor.PersonCenterActivity;
import com.yksj.consultation.event.EMainRefresh;
import com.yksj.consultation.im.NIMManager;
import com.yksj.consultation.login.UserLoginActivity;
import com.yksj.consultation.setting.SettingPhoneBound;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.ExpertUploadCaseActivity;
import com.yksj.consultation.sonDoc.friend.MyCustomerActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import butterknife.BindView;


/**
 * Created by HEKL on 2015/9/14.
 * Used for 新六一健康医生主页_
 */
public class MainActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.refresh_layout) SmartRefreshLayout mRefreshLayout;

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_mainlayout;
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setLeft(R.drawable.main_pc, v -> {
            Intent intent = new Intent(MainActivity.this, PersonCenterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
        // 登陆网易IM
        NIMManager.doLogin(this, DoctorHelper.getAccount(), DoctorHelper.getNimToken());
        // 检查版本更新
        AppUpdateManager.getInstance().checkeUpdate(this, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DoctorHelper.hasLoagin()) {
            String phone = LoginBusiness.getInstance().getLoginEntity().getPoneNumber().trim();
            if (TextUtils.isEmpty(phone)) {
                showBindPhone();
            }
        } else {
            ToastUtil.showShort(ResourceHelper.getString(R.string.error_login_info_none));
            Intent intent = new Intent(this, UserLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    /**
     * 显示绑定手机提示
     */
    private void showBindPhone() {
        ConfirmDialog.newInstance("", "绑定手机号码,有助于您找回密码,现在就去绑定吗？")
                     .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                         @Override
                         public void onNegativeClick(ConfirmDialog dialog, View v) {
                             super.onNegativeClick(dialog, v);
                             Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                             startActivity(intent);
                             MainActivity.this.finish();
                         }

                         @Override
                         public void onPositiveClick(ConfirmDialog dialog, View v) {
                             super.onPositiveClick(dialog, v);
                             Intent intent = new Intent(MainActivity.this, SettingPhoneBound.class);
                             startActivity(intent);
                         }
                     })
                     .show(getSupportFragmentManager());
        DoubleBtnFragmentDialog.show(getSupportFragmentManager(), "六一健康", "绑定手机号码,有助于您找回密码,现在就去绑定吗?", "去绑定", "退出", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
            @Override
            public void onDismiss(DialogFragment fragment) {

            }

            @Override
            public void onClick(DialogFragment fragment, View v) {

            }
        });
    }

    /**
     * View的初始化f
     */
    private void initView() {
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            setTitle(LoginBusiness.getInstance().getLoginEntity().getCenterName());
        }
        findViewById(R.id.rl_consult).setVisibility(DoctorHelper.isExpert() ? View.GONE : View.VISIBLE);
        findViewById(R.id.rl_case).setVisibility(!DoctorHelper.isExpert() ? View.GONE : View.VISIBLE);
        mRefreshLayout.setRefreshHeader(new SimpleRefreshHeader(this));
        mRefreshLayout.setOnRefreshListener(refreshLayout -> onRefresh());
        mRefreshLayout.setEnableLoadMore(false);
        findViewById(R.id.btn_consult).setOnClickListener(this);
        findViewById(R.id.btn_case).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_consult://医生发起会诊
                intent = MyCustomerActivity.getCallingIntent(this);
                startActivity(intent);
                break;
            case R.id.btn_case://专家分享病历
                intent = ExpertUploadCaseActivity.getCallingIntent(this);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DialogManager.getConfrimDialog("您确定要退出吗?")
                         .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                             @Override
                             public void onPositiveClick(ConfirmDialog dialog, View v) {
                                 AppContext.exitApp();
                             }
                         })
                         .show(getSupportFragmentManager());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 刷新首页数据
     */
    public void onRefresh() {
        EventManager.post(new EMainRefresh());
        mRefreshLayout.finishRefresh(2000);
    }
}
