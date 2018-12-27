package com.library.base.base;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.library.base.R;
import com.library.base.event.EExitApp;
import com.library.base.event.EOffsiteLogin;
import com.library.base.utils.EventManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * activity基类，新建activity应该继承该类
 * 如果需要代表标题的activity，请使用{@link com.library.base.base.BaseTitleActivity}
 */
public class BaseActivity extends AppCompatActivity implements OnClickListener {

    @Deprecated public ImageView titleLeftBtn;
    @Deprecated public ImageView titleRightBtn;
    @Deprecated public TextView titleRightBtn2;
    @Deprecated public TextView titleTextV;
    @Deprecated public RelativeLayout title;
    @Deprecated public RadioButton leftRadio, rightRadio;
    @Deprecated public RadioGroup radioGroup;
    @Deprecated public EditText editSearch;
    @Deprecated public ImageView mImageView;
    @Deprecated public ImageView mImageViewD;
    @Deprecated public ImageView mImageViewP;
    private Unbinder mButterBind;
    private String mStartActivityTimeTarget;
    private long mLastStartActivityTime;

    /**
     * 如果需要代表标题的activity，请使用{@link com.library.base.base.BaseTitleActivity}
     */
    @Deprecated
    public final void initializeTitle() {
        titleLeftBtn = bindView(R.id.title_back);
        titleRightBtn = bindView(R.id.title_right);
        titleTextV = bindView(R.id.title_lable);
        titleRightBtn2 = bindView(R.id.title_right2);
//        mImageView = bindView(R.id.title_rigth_pic);
        mImageViewP = bindView(R.id.main_listmenuP);
        mImageViewD = bindView(R.id.main_listmenuD);
        title = bindView(R.id.title_root);
    }

    /**
     * 如果需要代表标题的activity，请使用{@link com.library.base.base.BaseTitleActivity}
     * 设置标题
     * @param txt
     */
    public void setTitle(String txt) {
        titleTextV.setText(txt);
    }

    /**
     * 根据ID获取View实体
     * @param id  view Id
     * @param <T>
     * @return
     */
    public <T extends View> T bindView(@IdRes int id) {
        return findViewById(id);
    }

    /**
     * 如果需要代表标题的activity，请使用{@link com.library.base.base.BaseTitleActivity}
     * @param txt
     * @param listener
     */
    public void setRight(String txt, OnClickListener listener) {
        if (titleRightBtn2 == null) return;
        titleRightBtn2.setText(txt);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(listener);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        int layoutRes = createLayoutRes();
        View contentView = createLayout();
        if (layoutRes != 0) {
            setContentView(layoutRes);
        } else if (contentView != null) {
            setContentView(contentView);
        }
        EventManager.register(this);
        initialize(arg0);
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) findViewById(android.R.id.content), false);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        BarUtils.setStatusBarColor(this, getResources().getColor(getStatusBarColor()), 0);
        view.setFitsSystemWindows(true);
        mButterBind = ButterKnife.bind(this, view);
    }

    /**
     * 获取布局文件ID
     * @return
     */
    public int createLayoutRes() {
        return 0;
    }

    /**
     * 获取布局
     * @return
     */
    public View createLayout() {
        return null;
    }

    /**
     * 创建title布局
     * @return
     */
    public int createTitleLayoutRes() {
        return 0;
    }

    /**
     * 初始化相关操作
     * @param bundle
     */
    public void initialize(Bundle bundle) {
    }

    /**
     * 状态栏颜色
     * @return colorRes
     */
    public int getStatusBarColor() {
        return R.color.color_blue;
    }

    @Override
    protected void onDestroy() {
        if (mButterBind != null) {
            mButterBind.unbind();
        }
        EventManager.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 应用退出 子类可实现相关操作
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitAppEvent(EExitApp e) {
        finish();
    }

    /**
     * 异地登陆 子类可实现相关操作
     * @param e
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoctorOffsite(EOffsiteLogin e) {
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (startActivitySafeCheck(intent)) {
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    /**
     * 检查当前 Activity 是否重复跳转了，不需要检查则重写此方法并返回 true 即可
     * @param intent
     * @return
     */
    private boolean startActivitySafeCheck(Intent intent) {
        boolean result = true;
        String tempTarget;
        if (intent.getComponent() != null) {
            tempTarget = intent.getComponent().getClassName();
        } else if (intent.getAction() != null) {
            tempTarget = intent.getAction();
        } else {
            return result;
        }
        if (tempTarget.equals(mStartActivityTimeTarget) && mLastStartActivityTime >= SystemClock.uptimeMillis() - 500){
            result = false;
        }
        mStartActivityTimeTarget = tempTarget;
        mLastStartActivityTime = SystemClock.uptimeMillis();
        return result;
    }

    /**
     * 当前Activity是否被销毁
     * @return true=销毁
     */
    @Override
    public boolean isDestroyed() {
        return !ActivityUtils.isActivityExistsInStack(this);
    }
}
