package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.station.view.LectureArticleInfoView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.jetbrains.annotations.NotNull;

/**
 * 健康讲堂 图文详情
 */
public class LectureArticleInfoActivity extends BaseTitleActivity implements LectureArticleInfoView.IPresenter {
    private static final String LECTURE_ID_EXTRA = "lecture_id_extra";

    LectureArticleInfoView mView;

    private String mInfoId;

    public static Intent getCallingIntent(Context context, String id) {
        Intent intent = new Intent(context, LectureArticleInfoActivity.class);
        intent.putExtra(LECTURE_ID_EXTRA, id);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new LectureArticleInfoView(this, this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mInfoId = getIntent().getStringExtra(LECTURE_ID_EXTRA);
        setTitle("课程详情");
        requestInfo();
    }

    /**
     * 获取课件详情
     */
    private void requestInfo() {
        ApiService.lectureInfo(mInfoId, DoctorHelper.getId()
                , new ApiCallbackWrapper<ResponseBean<LectureBean>>(true) {
                    @Override
                    public void onResponse(ResponseBean<LectureBean> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            LectureBean result = response.result;
                            mView.bindData(result);
                        }
                    }
                });
    }

    @Override
    public void onAvatarClick(@NotNull String path) {
        ImageBrowserActivity.from(this)
                .setImagePath(path)
                .startActivity();
    }
}
