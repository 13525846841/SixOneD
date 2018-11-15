package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.LectureAdapter;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.StationType;
import com.yksj.consultation.event.ELectureReleaseSucess;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.widget.LectureSortView;
import com.yksj.consultation.widget.SearchBarLayout;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 健康讲堂列表
 */
public class LectureHomeActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener, LectureSortView.OnLectureSortListener {

    @BindView(R.id.upload_layout)
    View mUploadLay;

    @BindView(R.id.lecture_upload)
    TextView mLectureUploadView;

    @BindView(R.id.empty_layout)
    View mEmptyView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.search_layout)
    SearchBarLayout mSearchBarLay;

    private LectureSortView mLectureSortView;
    private String mStationId;//工作站id
    private int mStationType;
    private LectureAdapter mAdapter;
    private int mPageIndex = 1;
    private int mType = 0;
    private int mCategory = 0;
    private int mCast = 0;

    public static Intent getCallingIntent(Context context, String stationId, int type) {
        Intent intent = new Intent(context, LectureHomeActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        intent.putExtra(Constant.Station.STATION_HOME_TYPE, type);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_lecture_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        mStationType = getIntent().getIntExtra(Constant.Station.STATION_HOME_TYPE, -1);
        initView();
        requestData(mStationId, mType, mCategory, mCast, false);
    }

    @Override
    public void initializeTitle(View mTitleView) {
        super.initializeTitle(mTitleView);
        setTitle(R.string.title_lecture_room);
    }

    private void initView() {
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL));
        mAdapter = new LectureAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnLoadMoreListener(() -> requestData(mStationId, mType, mCategory, mCast, true), mRecyclerView);

        // 添加头部筛选栏
        mLectureSortView = new LectureSortView(this);
        mLectureSortView.setListener(this);
        mAdapter.addHeaderView(mLectureSortView);

        // 设置空布局topMargin避免空布局遮住头部筛选
        mLectureSortView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLectureSortView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ((ViewGroup.MarginLayoutParams) mEmptyView.getLayoutParams()).topMargin = mLectureSortView.getMeasuredHeight();
                mEmptyView.requestLayout();
            }
        });

        //只有站长和成员才能发布
        if (mStationType == StationType.STATION_HOME_JOIN || mStationType == StationType.STATION_HOME_CREATE) {
            ViewUtils.setGone(mUploadLay, false);
        } else {
            mLectureSortView.setTwoLevelLabelVisible(false);
        }

        mSearchBarLay.setSearchHint("搜索健康讲堂");
    }

    /**
     * 我要发布点击
     * @param v
     */
    @OnClick(R.id.lecture_upload)
    public void onUploadClick(View v) {
        SelectorDialog.newInstance(new String[]{"发布视频", "发布图文"})
                .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                    @Override
                    public void onItemClick(SelectorDialog dialog, int position) {
                        Intent intent = null;
                        switch (position) {
                            case 0:
                                intent = LectureReleaseVideoActivity.getCallingIntent(LectureHomeActivity.this, mStationId);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = LectureReleaseArticleActivity.getCallingIntent(LectureHomeActivity.this, mStationId);
                                startActivity(intent);
                                break;
                        }
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 获取数据
     * @param stationId
     * @param type
     * @param category
     * @param cast
     * @param isMore
     */
    private void requestData(String stationId, int type, int category, int cast, boolean isMore) {
        mPageIndex = isMore ? ++mPageIndex : 1;
        ApiService.lectureList(stationId, type, category, cast, mPageIndex, new ApiCallbackWrapper<ResponseBean<List<LectureBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<LectureBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<LectureBean> result = response.result;
                    if (isMore) {
                        if (result != null && !result.isEmpty()) {
                            mAdapter.addData(result);
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.loadMoreEnd();
                        }
                    } else {
                        if (result != null && !result.isEmpty()) {
                            mAdapter.setNewData(result);
                            mEmptyView.setVisibility(View.GONE);
                        } else {
                            mAdapter.setNewData(result);
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
        LectureBean item = mAdapter.getItem(position);
        String id = item.COURSE_ID;
        Intent intent = LectureDescActivity.getCallingIntent(this, id);
        startActivity(intent);
    }

    /**
     * 筛选栏发生改变
     * @param oneLevelPosition
     * @param twoLevelPosition
     * @param threeLevelPosition
     */
    @Override
    public void onSortChange(int oneLevelPosition, int twoLevelPosition, int threeLevelPosition) {
        mType = oneLevelPosition;
        mCategory = twoLevelPosition;
        mCast = threeLevelPosition;
        requestData(mStationId, mType, mCategory, mCast, false);
    }

    /**
     * 接收健康讲堂发布成功事件刷新列表数据
     * @param e
     */
    @Subscribe
    public void onReleaseSucess(ELectureReleaseSucess e){
        requestData(mStationId, mType, mCategory, mCast, false);
    }
}
