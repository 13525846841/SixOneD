package com.library.base.base;


import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.R;

/**
 * 带有标题的Activity基础类
 */
public class BaseTitleActivity extends BaseActivity {

    /**
     * 标题左边菜单按钮
     */
    public ImageView titleLeftBtn;

    /**
     * 标题右边菜单按钮
     */
    public TextView titleRightBtn;

    /**
     * 标题右边图片菜单按钮
     */
    public ImageView titleRightImage;

    /**
     * 历史遗留代码，不清楚用途
     */
    public Button titleRightBtn2;

    /**
     * 标题
     */
    public TextView titleTextV;

    /**
     * 历史遗留代码，不清楚用途
     */
    public ImageView mImageViewD;

    /**
     * 标题右边图片菜单按钮
     */
    public ImageView mImageViewP;

    /**
     * 返回按钮
     */
    public View mBackView;

    /**
     * 标题根View
     */
    protected View mTitleView;

    /**
     * 内容根view
     */
    protected View mContentView;

    @Override
    public void setContentView(View view) {
        mContentView = view;
        View titleView = generateTitleContent(view);
        super.setContentView(titleView);
    }

    /**
     * 生成titlebar
     * @param view
     * @return
     */
    private View generateTitleContent(View view) {
        int titleLayoutRes = createTitleLayoutRes();
        if (titleLayoutRes != 0) {
            mTitleView = LayoutInflater.from(this).inflate(titleLayoutRes, (ViewGroup) findViewById(android.R.id.content), false);
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.addView(mTitleView);
            contentLayout.addView(view);
            initializeTitle(mTitleView);
            return contentLayout;
        }
        return view;
    }

    /**
     * 获取标题根View
     * @return
     */
    public View getTitleView(){
        return mTitleView;
    }

    /**
     * 之类可复写该方法实现不同的标题
     * @return
     */
    @Override
    public int createTitleLayoutRes() {
        return R.layout.base_title_layout;
    }

    /**
     * 初始化titl相关参数
     * @param titleView
     */
    public void initializeTitle(View titleView) {
        bindTitleView(titleView);
        titleView.setBackgroundColor(getResources().getColor(getTitleBackgroundColor()));
        setLeft(R.drawable.icon_back, v -> onBackPressed());
    }

    private void bindTitleView(View titleView) {
        titleLeftBtn = titleView.findViewById(R.id.iv_back);
        titleRightImage = titleView.findViewById(R.id.right_image);
        titleRightBtn = titleView.findViewById(R.id.title_right);
        titleRightBtn2 = titleView.findViewById(R.id.title_right2);
        titleTextV = titleView.findViewById(R.id.title_lable);
//        mImageView = titleView.findViewById(R.id.title_rigth_pic);
        mImageViewD = titleView.findViewById(R.id.main_listmenuD);
        mImageViewP = titleView.findViewById(R.id.main_listmenuP);
        mBackView = titleView.findViewById(R.id.title_back);
    }

    /**
     * 设置标题
     * @param txt
     */
    public void setTitle(String txt) {
        if (titleTextV == null) return;
        titleTextV.setText(txt);
    }

    /**
     * 设置标题
     * @param titleRes
     */
    public void setTitle(@StringRes int titleRes) {
        if (titleTextV == null) return;
        titleTextV.setText(titleRes);
    }

    /**
     * 设置左边菜单
     */
    public void setLeft(@DrawableRes int resId){
        if (titleLeftBtn == null) return;
        titleLeftBtn.setImageResource(resId);
        titleLeftBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置左边菜单
     * @param resId
     * @param listener
     */
    public void setLeft(@DrawableRes int resId, OnClickListener listener) {
        if (titleLeftBtn == null) return;
        titleLeftBtn.setImageResource(resId);
        if (mBackView == null) return;
        if (listener != null) mBackView.setOnClickListener(listener);
        mBackView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边菜单
     * @param str
     */
    public void setRight(String str){
        if (titleRightBtn == null) return;
        titleRightBtn.setText(str);
        titleRightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边菜单
     * @param res
     */
    public void setRight(@StringRes int res){
        if (titleRightBtn == null) return;
        titleRightBtn.setText(res);
        titleRightBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右边菜单
     * @param txt
     * @param listener
     */
    public void setRight(String txt, OnClickListener listener) {
        setRight(txt, true, listener);
    }

    /**
     * 设置右边菜单
     * @param enable
     */
    public void setRightEnable(boolean enable){
        if (titleRightBtn == null) return;
        titleRightBtn.setEnabled(enable);
    }

    /**
     * 设置右边菜单
     * @param txt
     * @param enable
     * @param listener
     */
    public void setRight(String txt, boolean enable, OnClickListener listener) {
        if (titleRightBtn == null) return;
        setRight(txt);
        titleRightBtn.setEnabled(enable);
        if (listener != null) titleRightBtn.setOnClickListener(listener);
    }

    /**
     * 设置右边菜单
     * @param res
     * @param listener
     */
    public void setRight(@DrawableRes int res, OnClickListener listener) {
        if (mImageViewP == null) return;
        mImageViewP.setVisibility(View.VISIBLE);
        mImageViewP.setImageResource(res);
        mImageViewP.setOnClickListener(listener);
    }

    /**
     * 设置右边菜单
     * @param res
     * @param listener
     */
    public void setRightImg(@DrawableRes int res, OnClickListener listener){
        if (titleRightImage == null)return;
        titleRightImage.setVisibility(View.VISIBLE);
        titleRightImage.setImageResource(res);
        titleRightImage.setOnClickListener(listener);
    }

    /**
     * 设置标题栏背景颜色，之类可复写该方法实现不通标题栏背景颜色
     * @return
     */
    public int getTitleBackgroundColor() {
        return R.color.title_color;
    }
}
