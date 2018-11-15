package com.yksj.consultation.sonDoc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by hww on 18/4/10.
 * Used for
 */

public class MyListView extends ListView {
//    private ScrollView scrollView;
//    private boolean notAllowParentScroll = true;

//    public void setNotAllowParentScroll(boolean notAllowParentScroll) {
//        this.notAllowParentScroll = notAllowParentScroll;
//    }
//
//    public void setScrollView(ScrollView scrollView) {
//        this.scrollView = scrollView;
//    }

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if(notAllowParentScroll){
//            switch(event.getAction()){
//                case MotionEvent.ACTION_DOWN:
//                    scrollView.requestDisallowInterceptTouchEvent(true);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    scrollView.requestDisallowInterceptTouchEvent(false);
//                    break;
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
