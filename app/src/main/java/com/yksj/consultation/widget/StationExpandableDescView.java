package com.yksj.consultation.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.widget.BaseView;
import com.library.base.widget.ExpandableDescView;
import com.yksj.consultation.sonDoc.R;

import butterknife.BindView;

public class StationExpandableDescView extends BaseView {

    private int mMaxLines = 3;

    @BindView(R.id.tv_title)
    TextView mTitleView;

    @BindView(R.id.iv_edit)
    ImageView mEditView;

    @BindView(R.id.desc_view)
    ExpandableDescView mDescView;

    public StationExpandableDescView(Context context) {
        this(context, null);
    }

    public StationExpandableDescView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StationExpandableDescView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int createContentRes() {
        return R.layout.view_station_desc;
    }

    public StationExpandableDescView setTitle(String title) {
        mTitleView.setText(title);
        return this;
    }

    public StationExpandableDescView setContent(String content) {
        if (TextUtils.isEmpty(content)){
            setVisibility(GONE);
        }
        mDescView.setContent(content);
        return this;
    }

    public StationExpandableDescView setOnEditClickListener(View.OnClickListener listener){
        mEditView.setOnClickListener(listener);
        return this;
    }

    public StationExpandableDescView visibalEdit(boolean visibal){
        mEditView.setVisibility(visibal ? VISIBLE : GONE);
        return this;
    }
}