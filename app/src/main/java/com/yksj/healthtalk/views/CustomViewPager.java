package com.yksj.healthtalk.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {


	private boolean scrollble=true;

	  public CustomViewPager(Context context){ 
	    super(context);
	  }

	  public CustomViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	  }
	
	 @Override
	  public boolean onTouchEvent(MotionEvent ev) {
	    if (!scrollble) {
	      return true;
	    }
	    return super.onTouchEvent(ev);
	  }
	 


	 @Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(scrollble){
		return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}

	  public boolean isScrollble() {
	    return scrollble;
	  }

	  public void setScrollble(boolean scrollble) {
	    this.scrollble = scrollble;
	  }
}

