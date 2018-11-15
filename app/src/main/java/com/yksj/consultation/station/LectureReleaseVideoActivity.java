package com.yksj.consultation.station;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.bean.LectureUploadBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.station.view.LectureReleaseVideoView;

import java.io.File;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * 健康讲堂 发布视频
 */
public class LectureReleaseVideoActivity extends BaseTitleActivity {
    private static final String PATH_EXTRA = "path_extra";
    private static final String TYPE_EXTRA = "type_extra";
    private static final int DEFAULT_CAPTURE_SECOND = 10;

    @BindView(R.id.video_view) LectureReleaseVideoView mVideoView;

    private String mStationId = "";//工作站ID
    private String mFileName = "";//上传文件名
    private String mUploadPath;//上传文件路径
    private String mUploadType;//上传文件类型
    private String mFileType = "";//上传文件路径
    private File mCoverPath = null;//上传文件缩略图

    public static Intent getCallingIntent(Context context, String stationId) {
        Intent intent = new Intent(context, LectureReleaseVideoActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_lecture_main;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mUploadPath = getIntent().getStringExtra(PATH_EXTRA);
        mUploadType = getIntent().getStringExtra(TYPE_EXTRA);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        initView();
    }

    private void initView() {
        setTitle(R.string.title_lecture_room);
        setRight("下一步", this::onLectureUpload);
        mVideoView.setUploadClick(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                RxChooseHelper.videoChoose(LectureReleaseVideoActivity.this)
                              .subscribe(new Consumer<String>() {
                                  @Override
                                  public void accept(String s) throws Exception {
                                      mUploadPath = s;
                                      mVideoView.setVideoPath(mUploadPath);
                                  }
                              });
            }
        });
        mVideoView.setCaptureClick(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                RxChooseHelper.videoRecord(LectureReleaseVideoActivity.this, DEFAULT_CAPTURE_SECOND)
                              .subscribe(new Consumer<String>() {
                                  @Override
                                  public void accept(String s) throws Exception {
                                      mUploadPath = s;
                                      LogUtils.e(s);
                                      mVideoView.setVideoPath(s);
                                  }
                              });
            }
        });
    }

    /**
     * 提交
     * @param v
     */
    public void onLectureUpload(View v) {
        LectureUploadBean uploadBean = mVideoView.toUploadBean(mStationId);
        if (uploadBean != null) {
            Intent intent = LectureReleaseSetupActivity.getCallingIntent(this, uploadBean);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DialogManager.getConfrimDialog("尚未编辑完成，您确定要离开吗")
                     .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                         @Override
                         public void onPositiveClick(ConfirmDialog dialog, View v) {
                             super.onPositiveClick(dialog, v);
                             LectureReleaseVideoActivity.super.onBackPressed();
                         }
                     }).show(getSupportFragmentManager());
    }
}
