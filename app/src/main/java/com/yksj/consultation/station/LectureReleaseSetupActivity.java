package com.yksj.consultation.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.EventManager;
import com.library.base.utils.StorageUtils;
import com.yksj.consultation.bean.LectureUploadBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.StationListBean;
import com.yksj.consultation.event.ELectureReleaseSucess;
import com.yksj.consultation.station.videoUpload.OnVideoUploadCallback;
import com.yksj.consultation.station.videoUpload.VideoUploadDelegate;
import com.yksj.consultation.station.view.LectureReleaseSetupView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import okhttp3.Request;

/**
 * 健康讲堂发布设置
 */
public class LectureReleaseSetupActivity extends BaseTitleActivity {
    private static final String UPLOAD_EXTRA = "upload_extra";

    LectureReleaseSetupView mView;

    private LectureUploadBean mUploadBean;

    public static Intent getCallingIntent(Context context, LectureUploadBean uploadBean) {
        Intent intent = new Intent(context, LectureReleaseSetupActivity.class);
        intent.putExtra(UPLOAD_EXTRA, uploadBean);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new  LectureReleaseSetupView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mUploadBean = getIntent().getParcelableExtra(UPLOAD_EXTRA);
        setTitle("发布设置");
        setRight("发布", this::onReleaseClick);
        requestStationList();
    }

    /**
     * 发布
     * @param view
     */
    private void onReleaseClick(View view) {
        LectureUploadBean uploadBean = mView.wrapBean(mUploadBean);
        if (!TextUtils.isEmpty(uploadBean.videoPath)) {
            uploadVideo(uploadBean);
        } else {
            requestReleaseArticle(uploadBean);
        }
    }

    /**
     * 上传视频
     * @param uploadBean
     */
    private void uploadVideo(LectureUploadBean uploadBean) {
        VideoUploadDelegate.getInstance().upload(this, uploadBean.videoPath, new OnVideoUploadCallback() {
            @Override
            public void onStart() {
                mView.showWait(getSupportFragmentManager());
            }

            @Override
            public void onProgress(long current, long total) {
                long percent = (current * 100) / total;
                mView.updateProgress(String.format("已上传%s%%", percent));
            }

            @Override
            public void onComplete(String bucket, String name, String videoPath) {
                mView.updateProgress("上传中...");
                uploadBean.videoName = name;// 设置视频ID
                uploadBean.videoPath = videoPath;
                requestReleaseVideo(uploadBean);
            }

            @Override
            public void onCancel() {
                mView.hideWait();
            }

            @Override
            public void onError(String msg) {
                mView.hideWait();
            }
        });
    }

    /**
     * 上传视频文章到服务器
     * @param uploadBean
     */
    private void requestReleaseVideo(LectureUploadBean uploadBean) {
        ApiService.lectureUploadVideo(uploadBean.content
                , uploadBean.isIn
                , uploadBean.price
                , uploadBean.title
                , uploadBean.isOut
                , uploadBean.price
                , uploadBean.stationId
                , DoctorHelper.getId()
                , uploadBean.videoName
                , uploadBean.videoPath
                , uploadBean.avatarPath
                , new ApiCallbackWrapper<ResponseBean>() {
                    @Override
                    public void onResponse(ResponseBean response) {
                        super.onResponse(response);
                        if (response != null) {
                            ToastUtils.showShort(response.message);
                            // 发送发布成功事件
                            EventManager.post(new ELectureReleaseSucess());
                            ActivityUtils.finishToActivity(LectureHomeActivity.class, false, true);
                        }
                        mView.hideWait();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        mView.hideWait();
                    }
                }, this);
    }

    /**
     * 上传文章到服务器
     * @param uploadBean
     */
    private void requestReleaseArticle(LectureUploadBean uploadBean) {
        ApiService.lectureUploadArticle(String.valueOf(uploadBean.lectureType)
                , uploadBean.content
                , uploadBean.isIn
                , uploadBean.price
                , String.valueOf(uploadBean.lectureType)
                , uploadBean.title
                , uploadBean.isOut
                , uploadBean.price
                , uploadBean.stationId
                , DoctorHelper.getId()
                , uploadBean.picturePath
                , new ApiCallbackWrapper<ResponseBean>(true) {
                    @Override
                    public void onResponse(ResponseBean response) {
                        super.onResponse(response);
                        if (response != null) {
                            ToastUtils.showShort(response.message);
                            // 发送发布成功事件
                            EventManager.post(new ELectureReleaseSucess());
                            ActivityUtils.finishToActivity(LectureHomeActivity.class, false, true);
                        }
                        mView.hideWait();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                        mView.hideWait();
                    }
                }, this);
    }

    /**
     * 获取工作站列表
     */
    private void requestStationList() {
        ApiService.OKHttpStationList(DoctorHelper.getId(), new ApiCallbackWrapper<ResponseBean<StationListBean>>() {
            @Override
            public void onResponse(ResponseBean<StationListBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    StationListBean result = response.result;
                    mView.bindStationList(result);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mUploadBean != null) {
            VideoUploadDelegate.getInstance().cancel(mUploadBean.videoPath);
            String avatarPath = mUploadBean.avatarPath;
            if (!TextUtils.isEmpty(avatarPath)){// 删除创建的视频封面图片
                StorageUtils.deleteFile(avatarPath);
            }
        }
        super.onDestroy();
    }
}
