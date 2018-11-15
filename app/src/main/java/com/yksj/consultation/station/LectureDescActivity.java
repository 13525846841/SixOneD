package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.bean.LectureBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.ELecturePaySucees;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.view.LectureDescView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;

/**
 * 健康讲堂 课程介绍
 */
public class LectureDescActivity extends BaseTitleActivity implements LectureDescView.OnActiveListener {
    private static final String LECTURE_ID_EXTRA = "lecture_id_extra";

    @BindView(R.id.pay_info)
    LectureDescView mView;

    private String mInfoId;

    public static Intent getCallingIntent(Context context, String id) {
        Intent intent = new Intent(context, LectureDescActivity.class);
        intent.putExtra(LECTURE_ID_EXTRA, id);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_lecture_pay_info;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("课程介绍");
        mInfoId = getIntent().getStringExtra(LECTURE_ID_EXTRA);
        mView.setActiveListener(this);
        requestPayInfo();
    }

    /**
     * 获取课件付费详情
     */
    public void requestPayInfo() {
        ApiService.lecturePayInfo(mInfoId, DoctorHelper.getId()
                , new ApiCallbackWrapper<ResponseBean<LectureBean>>(true) {
                    @Override
                    public void onResponse(ResponseBean<LectureBean> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            LectureBean result = response.result;
                            result.COURSE_ID = mInfoId;// 因为接口没有课件ID所以在这里赋值
                            mView.bindData(result);
                        }
                    }
                });
    }

    /**
     * 接收支付成功事件
     * 需要刷新课件状态，重新请求数据
     * @param e
     */
    @Subscribe
    public void onPaySucees(ELecturePaySucees e){
        requestPayInfo();
    }

    /**
     * 跳转到课件评论界面
     * @param v
     */
    @Override
    public void onCommentClick(@NotNull View v, String id) {
        startActivity(LectureCommentListActivity.getCallingIntent(this, id));
    }

    /**
     * 跳转到支付界面
     * @param v
     * @param data
     */
    @Override
    public void onPayClick(@NotNull View v, LectureBean data) {
        Intent intent = LecturePayActivity.getCallingIntent(this, data);
        startActivity(intent);
    }

    /**
     * 跳转到详情界面
     * @param v
     * @param data
     */
    @Override
    public void onLookClick(@NotNull View v, LectureBean data) {
        if (data.isVideo()) {// 视频
            Intent intent = LectureVideoInfoActivity.getCallingIntent(this, data.COURSE_ID);
            startActivity(intent);
        } else {// 图文
            Intent intent = LectureArticleInfoActivity.getCallingIntent(this, data.COURSE_ID);
            startActivity(intent);
        }
    }
}