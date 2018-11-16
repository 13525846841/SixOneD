package com.yksj.consultation.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewHelper {

    /**
     * 设置文字（如果文字未空隐藏view）
     * @param view
     * @param text
     */
    public static void setTextForView(View view, String text) {
        if (view instanceof TextView) {
            view.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            ((TextView) view).setText(text);
        } else if (view instanceof Button) {
            view.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            ((Button) view).setText(text);
        }
    }
}
