package com.yksj.consultation.station.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.library.base.R;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class LectureLabelView extends HorizontalScrollView {

    private LinearLayout mLabelContainer;
    private List<LableData> mLabels;
    private int mLabelCount;
    private int mSelectedPosition = -1;
    private int mSelectDrawable;
    private int mUnselectDrawable;
    private int mSelectTextColor;
    private int mUnselectTextColor;
    private int mTextSize = 14;
    private int mLabelWidth;
    private boolean mSpaceEqual;
    private OnLectureLabelListener mListener;

    public LectureLabelView(Context context) {
        this(context, null);
    }

    public LectureLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LectureLabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalScrollBarEnabled(false);
        mLabelContainer = new LinearLayout(getContext());
        mLabelContainer.setOrientation(LinearLayout.HORIZONTAL);
        int padding = SizeUtils.dp2px(14);
        mLabelContainer.setPadding(padding, 0, padding, 0);
        mLabelContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(mLabelContainer);
    }

    public LectureLabelView setListener(OnLectureLabelListener listener) {
        this.mListener = listener;
        return this;
    }

    public LectureLabelView setLabels(List<LableData> labels) {
        this.mLabels = labels;
        notifyDataSetChange();
        return this;
    }

    public LectureLabelView setTextSize(int size) {
        this.mTextSize = size;
        updataLabelStyle();
        return this;
    }

    public LectureLabelView setLabelWidth(int width) {
        this.mLabelWidth = width;
        notifyDataSetChange();
        return this;
    }

    public LectureLabelView setDrawable(@DrawableRes int selectDrawable, @DrawableRes int unselectDrawable) {
        this.mSelectDrawable = selectDrawable;
        this.mUnselectDrawable = unselectDrawable;
        updataLabelStyle();
        return this;
    }

    public LectureLabelView setTextColor(@ColorRes int selectColor, @ColorRes int unselectColor) {
        this.mSelectTextColor = getContext().getResources().getColor(selectColor);
        this.mUnselectTextColor = getContext().getResources().getColor(unselectColor);
        updataLabelStyle();
        return this;
    }

    public LectureLabelView setSelected(int position) {
        if (mSelectedPosition != position) {
            updataLabelSelecte(position);
        }
        return this;
    }

    public LableData getSelectedLable() {
        return mLabels.get(mSelectedPosition);
    }

    public void notifyDataSetChange() {
        mLabelContainer.removeAllViews();
        mLabelCount = mLabels.size();
        for (int i = 0; i < mLabelCount; i++) {
            View labelView = LayoutInflater.from(getContext()).inflate(R.layout.view_lecture_label, mLabelContainer, false);
            labelView.setTag(i);
            addLabel(i, labelView);
        }
        updataLabelStyle();
        setSelected(0);
    }

    private void addLabel(int position, final View labelView) {
        TextView labelContent = labelView.findViewById(R.id.tv_label);
        LableData lableData = mLabels.get(position);
        String labelStr = lableData.text;
        labelContent.setText(labelStr);
        labelView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (mSelectedPosition != position) {
                    setSelected(position);
                    if (mListener != null) {
                        mListener.onSelect(mSelectedPosition);
                    }
                } else if (mListener != null) {
                    mListener.onReselect(mSelectedPosition);
                }
            }
        });
        LinearLayout.LayoutParams layoutParams = mSpaceEqual ? new LinearLayout.LayoutParams(0, MATCH_PARENT, 1.0f)
                : new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        if (mLabelWidth > 0) {
            layoutParams.width = SizeUtils.dp2px(mLabelWidth);
        }
        if (position != 0) {
            layoutParams.leftMargin = SizeUtils.dp2px(8);
        }
        mLabelContainer.addView(labelView, position, layoutParams);
    }

    private void updataLabelSelecte(int position) {
        if (mSelectedPosition != -1) {
            updataLabel(mSelectedPosition, false);
        }
        updataLabel(position, true);
        mSelectedPosition = position;
    }

    private void updataLabelStyle() {
        final int count = mLabelContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            updataLabel(i, i == mSelectedPosition);
        }
    }

    private void updataLabel(int position, boolean isSelect) {
        View labelView = mLabelContainer.getChildAt(position);
        TextView labelContent = (TextView) labelView.findViewById(R.id.tv_label);
        labelView.setBackgroundResource(isSelect ? mSelectDrawable : mUnselectDrawable);
        labelContent.setTextColor(isSelect ? mSelectTextColor : mUnselectTextColor);
        labelContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
    }

    public interface OnLectureLabelListener {
        void onSelect(int position);

        void onReselect(int position);
    }

    public static class LableData {
        public String text;
        public int tag;

        public LableData(String text, int tag) {
            this.text = text;
            this.tag = tag;
        }
    }
}
