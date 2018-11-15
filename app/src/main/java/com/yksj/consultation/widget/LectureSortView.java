package com.yksj.consultation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.library.base.widget.BaseView;
import com.yksj.consultation.station.view.LectureLabelView;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.view.LectureLabelView.LableData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LectureSortView extends BaseView implements LectureLabelView.OnLectureLabelListener {

    @BindView(R.id.one_level_label)
    LectureLabelView mOnelevelLabelView;

    @BindView(R.id.two_level_label)
    LectureLabelView mTwolevelLabelView;

    @BindView(R.id.three_level_label)
    LectureLabelView mThreelevelLabelView;

    private OnLectureSortListener mListener;

    public LectureSortView(Context context) {
        this(context, null);
    }

    public LectureSortView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LectureSortView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int createContentRes() {
        return R.layout.view_lecture_sort;
    }

    @Override
    protected void init() {
        super.init();

        mOnelevelLabelView.setLabels(generaOneType())
                .setTextColor(R.color.white, R.color.black)
                .setDrawable(R.drawable.lecture_label_select, R.drawable.lecture_label_unselect)
                .setLabelWidth(60)
                .setListener(this);

        mTwolevelLabelView.setLabels(generaTwoType())
                .setTextColor(R.color.white, R.color.black)
                .setDrawable(R.drawable.lecture_label_select, R.drawable.lecture_label_unselect)
                .setLabelWidth(60)
                .setListener(this);

        mThreelevelLabelView.setLabels(generaThreeType())
                .setTextColor(R.color.white, R.color.black)
                .setDrawable(R.drawable.lecture_label_select, R.drawable.lecture_label_unselect)
                .setLabelWidth(60)
                .setListener(this);
    }

    /**
     * 一级分类
     * @return
     */
    private List<LableData> generaOneType() {
        List<LableData> lables = new ArrayList<>();
        lables.add(new LableData("全部", 0));
        lables.add(new LableData("最新", 1));
        lables.add(new LableData("最热", 2));
        lables.add(new LableData("科普类", 3));
        lables.add(new LableData("学术类", 4));
        lables.add(new LableData("人文类", 5));
        return lables;
    }

    /**
     * 二级分类
     * @return
     */
    private List<LableData> generaTwoType() {
        List<LableData> lables = new ArrayList<>();
        lables.add(new LableData("全部", 0));
        lables.add(new LableData("站内", 1));
        lables.add(new LableData("站外", 2));
        return lables;
    }

    /**
     * 三级分类
     * @return
     */
    private List<LableData> generaThreeType() {
        List<LableData> lables = new ArrayList<>();
        lables.add(new LableData("全部", 0));
        lables.add(new LableData("付费", 1));
        lables.add(new LableData("免费", 2));
        return lables;
    }

    public LectureSortView setOneLevelLabelVisible(boolean isVisible) {
        mOnelevelLabelView.setVisibility(isVisible ? VISIBLE : GONE);
        return this;
    }

    public LectureSortView setTwoLevelLabelVisible(boolean isVisible) {
        mTwolevelLabelView.setVisibility(isVisible ? VISIBLE : GONE);
        return this;
    }

    public LectureSortView setThreeLevelLabelVisible(boolean isVisible) {
        mThreelevelLabelView.setVisibility(isVisible ? VISIBLE : GONE);
        return this;
    }

    public void setListener(OnLectureSortListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onSelect(int position) {
        if (mListener != null) {
            mListener.onSortChange(mOnelevelLabelView.getSelectedLable().tag
                    , mTwolevelLabelView.getSelectedLable().tag
                    , mThreelevelLabelView.getSelectedLable().tag);
        }
    }

    @Override
    public void onReselect(int position) {

    }

    public interface OnLectureSortListener {
        void onSortChange(int oneLevelPosition, int twoLevelPosition, int threeLevelPosition);
    }
}
