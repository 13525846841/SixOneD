package com.yksj.consultation.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * 圆形单选框
 */
public class CirCheckBox extends AppCompatRadioButton {

    public CirCheckBox(Context context) {
        super(context);
    }

    public CirCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CirCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }
}
