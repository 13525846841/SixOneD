package com.yksj.consultation.sonDoc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 *
 */
public class MyListView2 extends ListView {

	public MyListView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	public MyListView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView2(Context context) {
		super(context);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
