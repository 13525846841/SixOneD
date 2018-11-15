package com.library.base.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseView extends ConstraintLayout {

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        int contentRes = createContentRes();
        if (contentRes != 0){
            View contentView = LayoutInflater.from(getContext()).inflate(contentRes, this, false);
            addView(contentView);
            ButterKnife.bind(this, this);
        }
    }

    public abstract int createContentRes();

}
