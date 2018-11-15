package com.yksj.consultation.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ViewHelper {

    public static void setTextForView(View view, String text, boolean isGone) {
        if (view instanceof TextView) {
            if (isGone) view.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            ((TextView) view).setText(text);
        } else if (view instanceof Button) {
            if (isGone) view.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            ((Button) view).setText(text);
        }
    }

    public static void setVisibalForDatas(View contentView, View emptyView, List result) {
        contentView.setVisibility(result == null || result.isEmpty() ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(result == null || result.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
