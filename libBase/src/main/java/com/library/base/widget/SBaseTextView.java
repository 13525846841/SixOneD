package com.library.base.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SBaseTextView extends LinearLayout {

    private Context mContext;

    private TextView topTextView, centerTextView, bottomTextView;

    private LinearLayout.LayoutParams topTVParams, centerTVParams, bottomTVParams;

    public SBaseTextView(Context context) {
        this(context, null);
    }

    public SBaseTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOrientation(VERTICAL);

        mContext = context;

        initView();
    }

    private void initView() {
        initTopView();
        initCenterView();
        initBottomView();
    }

    private void initTopView() {
        if (topTVParams == null) {
            topTVParams = getParams(topTVParams);
        }
        if (topTextView == null) {
            topTextView = initTextView(topTVParams, topTextView);
        }
    }

    private void initCenterView() {
        if (centerTVParams == null) {
            centerTVParams = getParams(centerTVParams);
        }
        if (centerTextView == null) {
            centerTextView = initTextView(centerTVParams, centerTextView);
        }
    }

    private void initBottomView() {
        if (bottomTVParams == null) {
            bottomTVParams = getParams(bottomTVParams);
        }
        if (bottomTextView == null) {
            bottomTextView = initTextView(bottomTVParams, bottomTextView);
        }
    }


    private TextView initTextView(LinearLayout.LayoutParams params, TextView textView) {
        textView = getTextView(textView, params);
        addView(textView);
        return textView;
    }


    /**
     * 初始化textView
     * @param textView     对象
     * @param layoutParams 对象
     * @return 返回
     */
    public TextView getTextView(TextView textView, LinearLayout.LayoutParams layoutParams) {
        if (textView == null) {
            textView = new TextView(mContext);
            textView.setLayoutParams(layoutParams);
            textView.setVisibility(GONE);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        return textView;
    }

    /**
     * 初始化Params
     * @param params 对象
     * @return 返回
     */
    public LayoutParams getParams(LayoutParams params) {
        if (params == null) {
            // TODO: 2017/7/21 问题记录 ：之前设置 MATCH_PARENT导致每次重新设置string的时候，textView的宽度都已第一次为准，在列表中使用的时候服用出现混乱，特此记录一下，以后处理好布局之间套用时候设置WRAP_CONTENT和MATCH_PARENT出现问题
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        return params;
    }


    private void setTextString(TextView textView, CharSequence textString) {
        textView.setText(textString);
        if (!TextUtils.isEmpty(textString)) {
            textView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    public void setTopTextString(CharSequence s) {
        setTextString(topTextView, s);
    }

    public void setCenterTextString(CharSequence s) {
        setTextString(centerTextView, s);
    }

    public void setBottomTextString(CharSequence s) {
        setTextString(bottomTextView, s);
    }

    public TextView getTopTextView() {
        return topTextView;
    }

    public TextView getCenterTextView() {
        return centerTextView;
    }

    public TextView getBottomTextView() {
        return bottomTextView;
    }

    public void setMaxEms(int topMaxEms, int centerMaxEms, int bottomMaxEms) {

        if (topMaxEms != 0) {
            topTextView.setEllipsize(TextUtils.TruncateAt.END);
            topTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(topMaxEms)});
        }
        if (centerMaxEms != 0) {
            centerTextView.setEllipsize(TextUtils.TruncateAt.END);
            centerTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(centerMaxEms)});
        }
        if (bottomMaxEms != 0) {
            bottomTextView.setEllipsize(TextUtils.TruncateAt.END);
            bottomTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(bottomMaxEms)});
        }

    }

    public void setGravity(int gravity){
        topTextView.setGravity(gravity);
        centerTextView.setGravity(gravity);
        bottomTextView.setGravity(gravity);
    }

    public void setCenterSpaceHeight(int centerSpaceHeight) {
        topTVParams.setMargins(0, 0, 0, centerSpaceHeight);
        centerTVParams.setMargins(0, 0, 0, 0);
        bottomTVParams.setMargins(0, centerSpaceHeight, 0, 0);
    }
}
