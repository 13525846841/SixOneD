package com.yksj.healthtalk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MyHoritanlScrollView extends FrameLayout {

	public MyHoritanlScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		super.measureChildren(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec,
			int parentHeightMeasureSpec) {
		ViewGroup.LayoutParams lp = child.getLayoutParams();

		int childWidthMeasureSpec;
		int childHeightMeasureSpec;

		childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
				getPaddingTop() + getPaddingBottom(), lp.height);

		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	@Override
	protected void measureChildWithMargins(View child,
			int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {
		final MarginLayoutParams lp = (MarginLayoutParams) child
				.getLayoutParams();

		final int childHeightMeasureSpec = getChildMeasureSpec(
				parentHeightMeasureSpec, getPaddingTop() + getPaddingBottom()
						+ lp.topMargin + lp.bottomMargin + heightUsed,
				lp.height);
		final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
				lp.leftMargin + lp.rightMargin, MeasureSpec.UNSPECIFIED);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

}
