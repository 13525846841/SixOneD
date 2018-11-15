package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yksj.consultation.adapter.LectureAdapter;
import com.yksj.consultation.basic.BaseListActivity;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.LectureType;
import com.yksj.consultation.station.LectureArticleInfoActivity;
import com.yksj.consultation.station.LectureDescActivity;
import com.yksj.consultation.station.LectureVideoInfoActivity;
import com.yksj.healthtalk.net.http.ApiService;

/**
 * 个人讲堂
 */
public class DoctorLectureListActivity extends BaseListActivity {

    private String mDoctorId;

    public static Intent getCallingIntent(Context context, String doctorId) {
        Intent intent = new Intent(context, DoctorLectureListActivity.class);
        intent.putExtra(Constant.Station.USER_ID, doctorId);
        return intent;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mDoctorId = getIntent().getStringExtra(Constant.Station.USER_ID);
        setTitle("个人讲堂");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        LectureBean item = (LectureBean) adapter.getItem(position);
        if (item.COURSE_IN_PRICE == 0 | item.COURSE_OUT_PRICE == 0) {// 免费文章
            if (item.COURSE_CLASS == LectureType.VIDEO_TYPE) {
                Intent intent = LectureVideoInfoActivity.getCallingIntent(this, item.COURSE_ID);
                startActivity(intent);
            } else {
                Intent intent = LectureArticleInfoActivity.getCallingIntent(this, item.COURSE_ID);
                startActivity(intent);
            }
        } else {// 付费文章
            Intent intent = LectureDescActivity.getCallingIntent(this, item.COURSE_ID);
            startActivity(intent);
        }
    }

    @Override
    protected BaseQuickAdapter createAdapter() {
        return new LectureAdapter();
    }

    @Override
    protected void requestData(boolean isMore, int pageIndex) {
        ApiService.lectureListByDoctor(mDoctorId, pageIndex, createSimpleCallback(LectureBean.class));
    }
}
