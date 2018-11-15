package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.LectureCommentBean;
import com.yksj.healthtalk.net.http.ApiService;

/**
 * 健康讲堂评论
 */
public class LectureCommentListActivity extends BaseListActivity {
    private static final String COURSE_ID = "course_id";
    private String mCourseId;// 课件ID

    public static Intent getCallingIntent(Context context, String id){
        Intent intent = new Intent(context, LectureCommentListActivity.class);
        intent.putExtra(COURSE_ID, id);
        return intent;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mCourseId = getIntent().getStringExtra(COURSE_ID);
        setTitle("评论列表");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new LectureCommentAdapter();
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.lectureCommentList(mCourseId, pageIndex, createSimpleCallback(LectureCommentBean.class));
    }
}
