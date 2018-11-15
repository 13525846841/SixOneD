package com.yksj.consultation.station;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.bean.LectureUploadBean;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.station.view.LectureReleaseArticleView;

import io.reactivex.functions.Consumer;

/**
 * 健康讲堂 发布图文
 */
public class LectureReleaseArticleActivity extends BaseTitleActivity implements LectureReleaseArticleView.IPresenter {

    LectureReleaseArticleView mDocView;
    private String mStationId;

    public static Intent getCallingIntent(Context context, String stationId) {
        Intent intent = new Intent(context, LectureReleaseArticleActivity.class);
        intent.putExtra(Constant.Station.STATION_ID, stationId);
        return intent;
    }

    @Override
    public View createLayout() {
        return mDocView = new LectureReleaseArticleView(this, this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mStationId = getIntent().getStringExtra(Constant.Station.STATION_ID);
        setTitle("图文编辑");
        setRight("下一步", this::onNextClick);
    }

    /**
     * 选择图片
     */
    @SuppressLint("CheckResult")
    @Override
    public void onPictureChooseClick() {
        RxChooseHelper.chooseImage(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              mDocView.setPicture(s);
                          }
                      });
    }

    /**
     * 下一步
     * @param view
     */
    private void onNextClick(View view) {
        LectureUploadBean bean = mDocView.toBean(mStationId);
        if (checkFormat(bean)) {
            Intent intent = LectureReleaseSetupActivity.getCallingIntent(this, bean);
            startActivity(intent);
        }
    }

    /**
     * 验证数据是否填写正确
     */
    private boolean checkFormat(LectureUploadBean bean){
        if(bean.picturePath.isEmpty()){
            ToastUtils.showShort("请选择一张图片");
            return false;
        }
        if(bean.title.isEmpty()){
            ToastUtils.showShort("请输入标题");
            return false;
        }
        if (bean.content.isEmpty()){
            ToastUtils.showShort("请输入课件内容");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DialogManager.getConfrimDialog("尚未编辑完成，您确定要离开吗")
                .addListener(new ConfirmDialog.SimpleConfirmDialogListener() {
                    @Override
                    public void onPositiveClick(ConfirmDialog dialog, View v) {
                        super.onPositiveClick(dialog, v);
                        LectureReleaseArticleActivity.super.onBackPressed();
                    }
                }).show(getSupportFragmentManager());
    }
}
