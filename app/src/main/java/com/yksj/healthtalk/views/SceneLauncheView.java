package com.yksj.healthtalk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;

/**
 *  主页面滑动控制
 * @author origin
 *
 */
public class SceneLauncheView extends ViewGroup {
	private static final String TAG  = SceneLauncheView.class.getSimpleName();
	
	boolean isFirstLayout = true;
	public SceneLauncheView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SceneLauncheView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		// TODO Auto-generated constructor stub
	}

	public SceneLauncheView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	private void initView(){
		
	}
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View childView  = getChildAt(i);
			if(childView.getVisibility() != View.GONE){
				final int heigth = childView.getMeasuredHeight();
				final int width = childView.getMeasuredWidth();
				childView.layout(getPaddingLeft(),getPaddingTop(),width,heigth);
			}
		}
		
		if(isFirstLayout){
//			scrollTo(getWidth()/2,0);
//			scrollTo(getWidth()/2,0);
			isFirstLayout = false;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//final int width = MeasureSpec.getSize(widthMeasureSpec);
		//final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
//			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			
			final View childView = getChildAt(i);
			
//			childView.measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
//			childView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			childView.measure(widthMeasureSpec, heightMeasureSpec);
			
//			MeasureSpec.makeMeasureSpec(0,MeasureSpec.AT_MOST);
//			childView.measure(widthMeasureSpec, heightMeasureSpec);
			//childView.measure(MeasureSpec.makeMeasureSpec(getWidth(),MeasureSpec.AT_MOST), heightMeasureSpec);
//			ScrollView;
//			HorizontalScrollView
		}
//		int scorllX = getScrollX();
//		int scorllY = getScrollY();
		
		
		
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/*setMeasuredDimension(resolveSize(0,widthMeasureSpec),resolveSize(0,heightMeasureSpec));
		
		final int measureMode = MeasureSpec.getMode(widthMeasureSpec);
		if(measureMode == MeasureSpec.UNSPECIFIED){
			return;
		}
		if(getChildCount() > 0){
			final View childView = getChildAt(0);
			int width = getMeasuredWidth();
			if(childView.getMeasuredWidth() < width){
				LayoutParams layoutParams = childView.getLayoutParams();
				int childHeigthMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop()+getPaddingBottom(),layoutParams.height);
				width -= getPaddingLeft();
				width -= getPaddingRight();
				int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
				childView.measure(childWidthMeasureSpec, childHeigthMeasureSpec);
			}
		}*/
		
		
	}
	
/*	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
//		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		return super.onTouchEvent(event);
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		}
		return false;
	}*/
	
	
	private int measureWidth(int measureSpec){
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);
		if(specMode == MeasureSpec.AT_MOST){
			LogUtils.d(TAG, "--AT_MOST---");//fill_patent
		}else if(specMode == MeasureSpec.EXACTLY){
			LogUtils.d(TAG, "--EXACTLY---");//wrap_content
		}
		
		
		MeasureSpec.makeMeasureSpec(size,MeasureSpec.AT_MOST);
		
//		MeasureSpec.EXACTLY//fill_parent
//		MeasureSpec.AT_MOST//wrap_content
		result = size;
		return result;
	}
	
	private int measureHeight(int measureSpec){
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int size  = MeasureSpec.getSize(measureSpec);
		result = size;
		return result;
	}

}
