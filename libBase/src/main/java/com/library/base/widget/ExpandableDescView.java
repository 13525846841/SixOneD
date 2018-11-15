package com.library.base.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.R;

public class ExpandableDescView extends LinearLayout {

    private TextView mContentView;
    private TextView mReadMoreView;
    private int mMaxLine = 3;
    private boolean mCollapsed;
    private String mContent;

    public ExpandableDescView(Context context) {
        this(context, null);
    }

    public ExpandableDescView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableDescView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
        mContentView = new TextView(context);
        mContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mContentView.setTextColor(Color.parseColor("#656565"));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mContentView, layoutParams);

        mReadMoreView = new TextView(context);
        mReadMoreView.setTextColor(context.getResources().getColor(R.color.text_black_two));
        mReadMoreView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mReadMoreView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onReadMoreClick();
            }
        });
        mReadMoreView.setText("阅读更多");
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        addView(mReadMoreView, layoutParams);
    }

    /**
     * 设置内容
     * @param content
     */
    public void setContent(String content) {
        mContent = content;
        mContentView.setText(content);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int lineCount = mContentView.getLineCount();
                if (lineCount <= mMaxLine) {
                    mReadMoreView.setVisibility(GONE);
                } else {
                    mReadMoreView.setVisibility(VISIBLE);
                    mContentView.setMaxLines(mMaxLine);
                    mContentView.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
    }

    /**
     * 设置最大多少行显示more
     * @param maxLine
     */
    public void setMaxLine(int maxLine){
        this.mMaxLine = maxLine;
        invalidate();
    }

    private void onReadMoreClick() {
        int start = computeHeight(mMaxLine);
        int end = computeHeight(Integer.MAX_VALUE);
        startExpand(start, end);
    }

    private void startExpand(int start, int end) {
        mCollapsed = !mCollapsed;
        mReadMoreView.setText(mCollapsed ? "收起" : "阅读更多");
        ValueAnimator animator = ValueAnimator.ofInt(mCollapsed ? start : end, mCollapsed ? end : start);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int percent = (int) animation.getAnimatedValue();
                mContentView.getLayoutParams().height = percent;
                mContentView.requestLayout();
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private int computeHeight(int lines) {
        mContentView.setMaxLines(lines);
        mContentView.setText(mContent);
        return mContentView.getLayout().getLineTop(mContentView.getLineCount());
    }
}
