package com.yksj.consultation.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.blankj.utilcode.util.AppUtils;
import com.library.base.base.BaseActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.widget.SuperButton;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yksj.consultation.service.CoreService;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.SharePreHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 功能导航页
 */
public class NavigationActivity extends BaseActivity {

    private ViewPager mViewPager;
    private SuperButton mJoinBtn;
    private AnimationDrawable drawable;
    private int currIndex;
    private int tabLineLength;
    private ImageView[] tips;// 点
    private List<View> lists;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_navigation;
    }

    @Override
    public int getStatusBarColor() {
        return R.color.transparent;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        requestPermission();
        CoreService.actionStart(this);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        tabLineLength = metrics.widthPixels;

        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mJoinBtn = findViewById(R.id.btn_join);
        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreHelper.setFirstUse(false);
                startActivity(new Intent(NavigationActivity.this, UserLoginActivity.class));
                finish();
            }
        });

        lists = new ArrayList<View>();

        ImageView imageView1 = new ImageView(this);
        imageView1.setBackgroundResource(R.drawable.guide3);

        ImageView imageView2 = new ImageView(this);
        imageView2.setBackgroundResource(R.drawable.guide2);

        lists.add(imageView1);
        lists.add(imageView2);

        ViewPagerAdapter adapter = new ViewPagerAdapter(lists);
        mViewPager.setAdapter(adapter);

        tips = new ImageView[lists.size()];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(5, 5));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.icon_gray_dot);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_white_dot);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                // 如果是最后一个引导界面的话，就出现按钮
                // 如果不是最后一个的话，就不出现
                currIndex = arg0;
                setImageBackground(arg0 % lists.size());
                mJoinBtn.setVisibility(arg0 == lists.size() - 1 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.icon_white_dot);
            } else {
                tips[i].setBackgroundResource(R.drawable.icon_gray_dot);
            }
        }
    }

    /**
     * 请求需要的权限
     */
    @SuppressLint("CheckResult")
    private void requestPermission() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (!permission.granted) {
                            showConfrimDialog();
                        }
                    }
                });
    }

    /**
     * 显示权限被拒绝提示
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

    public class ViewPagerAdapter extends PagerAdapter {
        private List<View> pages;

        public ViewPagerAdapter(List<View> lists) {
            this.pages = lists;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(pages.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(pages.get(position));
            return pages.get(position);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
