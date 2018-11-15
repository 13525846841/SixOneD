package com.library.base.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.library.base.R;
import com.library.base.docloader.DocChooseActivity;
import com.library.base.docloader.DocEntity;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * 选择帮助类
 * 包含图片、视频、预览、编辑、拍照
 */
public class RxChooseHelper {

    private static final String RESULT_TAG = "RxChooseResultFragment";

    private static final String IMAGE_SAVE_PATH = File.separator + StorageUtils.ROOT_PATH + File.separator + StorageUtils.IMAGE_PATH;
    private static final String VIDEO_SAVE_PATH = File.separator + StorageUtils.ROOT_PATH + File.separator + StorageUtils.VIDEO_PATH;

    public static Observable<String> chooseDoc(FragmentActivity activity) {
        return Observable.just(getResultFragment(activity))
                         .compose(docChooses(activity))
                         .flatMap(RxChooseHelper::makeResult)
                         .map(intent -> {
                             DocEntity entity = DocChooseActivity.obtainResult(intent);
                             return entity != null ? entity.path : "";
                         });
    }

    /**
     * 拍摄照片
     * @param activity
     * @return
     */
    public static Observable<String> captureImage(FragmentActivity activity) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(imageCapture())
                         .map(model -> applyOutputCameraPath(model, IMAGE_SAVE_PATH))
                         .map(model -> forResult(model))
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 拍摄照片
     * @param activity
     * @param widthRatio  剪切宽度比例
     * @param heightRatio 剪切高度比例
     * @return
     */
    public static Observable<String> captureImage(FragmentActivity activity, int widthRatio, int heightRatio) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(imageCapture())
                         .map(model -> applyAspectRatio(model, widthRatio, heightRatio))
                         .map(model -> applyOutputCameraPath(model, IMAGE_SAVE_PATH))
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 图片选择(单图)
     * @param activity
     */
    @SuppressLint("CheckResult")
    public static Observable<String> chooseImage(FragmentActivity activity) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(imageChoose())
                         .map(RxChooseHelper::applySingle)
                         .map(RxChooseHelper::applyCompress)
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 图片选择(单图)
     * @param activity
     * @param widthRatio  剪切
     * @param heightRatio 剪切
     * @return
     */
    @SuppressLint("CheckResult")
    public static Observable<String> chooseImage(FragmentActivity activity, int widthRatio, int heightRatio) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(imageChoose())
                         .map(RxChooseHelper::applySingle)
                         .map(model -> applyAspectRatio(model, widthRatio, heightRatio))
                         .map(RxChooseHelper::applyCompress)
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 图片选择(多图)
     * @param activity
     */
    @SuppressLint("CheckResult")
    public static Observable<List<String>> chooseImage(FragmentActivity activity, int maxNum) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(imageChoose())
                         .map(model -> applyMultiple(model, maxNum))
                         .map(RxChooseHelper::applyCompress)
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainMultipleResult);
    }

    /**
     * 录制视频
     * @param activity
     * @return
     */
    public static Observable<String> videoRecord(FragmentActivity activity) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(videoRecord())
                         .map(model -> applyOutputCameraPath(model, VIDEO_SAVE_PATH))
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 录制视频
     * @param activity
     * @param maxSecond 录制多少秒
     * @return
     */
    public static Observable<String> videoRecord(FragmentActivity activity, int maxSecond) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(videoRecord())
                         .map(model -> applyRecordVideoSecond(model, maxSecond))
                         .map(model -> applyOutputCameraPath(model, VIDEO_SAVE_PATH))
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 视频选择
     * @param activity
     */
    public static Observable<String> videoChoose(FragmentActivity activity) {
        RxChooseResultFragment resultFragment = getResultFragment(activity);
        return Observable.just(PictureSelector.create(resultFragment))
                         .compose(videoChoose())
                         .map(RxChooseHelper::forResult)
                         .flatMap(model -> makeResult(resultFragment))
                         .map(RxChooseHelper::obtainSingleResult);
    }

    /**
     * 获取单个结果
     * @param data
     * @return
     */
    static String obtainSingleResult(Intent data) {
        return obtainMultipleResult(data).isEmpty() ? "" : obtainMultipleResult(data).get(0);
    }

    /**
     * 获取结果
     * @param data
     */
    static List<String> obtainMultipleResult(Intent data) {
        List<String> tempResult = new ArrayList<>();
        List<LocalMedia> results = PictureSelector.obtainMultipleResult(data);
        for (LocalMedia media : results) {
            if (media.isCut()) {
                if (media.isCompressed()) {
                    tempResult.add(media.getCompressPath());
                } else {// 裁剪后的路径
                    tempResult.add(media.getCutPath());
                }
            } else if (media.isCompressed()) {
                tempResult.add(media.getCompressPath());
            } else {
                tempResult.add(media.getPath());
            }
        }
        return tempResult;
    }

    /**
     * 选择结果
     * @param resultFragment
     * @return
     */
    static Observable<Intent> makeResult(RxChooseResultFragment resultFragment) {
        PublishSubject<Intent> subject = PublishSubject.create();
        resultFragment.bindSubject(subject);
        return subject;
    }

    static ObservableTransformer<RxChooseResultFragment, RxChooseResultFragment> docChooses(FragmentActivity activity){
        return upstream -> upstream.map(new Function<RxChooseResultFragment, RxChooseResultFragment>() {
            @Override
            public RxChooseResultFragment apply(RxChooseResultFragment resultFragment) throws Exception {
                Intent intent = DocChooseActivity.getCallingIntent(activity);
                resultFragment.startActivityForResult(intent, RxChooseResultFragment.CHOOSE_REQUEST);
                return resultFragment;
            }
        });
    }

    /**
     * 视频录制
     * @return
     */
    static ObservableTransformer<PictureSelector, PictureSelectionModel> videoRecord() {
        return upstream -> upstream.map(new Function<PictureSelector, PictureSelectionModel>() {
            @Override
            public PictureSelectionModel apply(PictureSelector selector) throws Exception {
                return selector
                        .openCamera(PictureMimeType.ofVideo())
                        .theme(R.style.Choose_Whit);
            }
        });
    }

    /**
     * 视频选择
     * @return
     */
    static ObservableTransformer<PictureSelector, PictureSelectionModel> videoChoose() {
        return upstream -> upstream.map(new Function<PictureSelector, PictureSelectionModel>() {
            @Override
            public PictureSelectionModel apply(PictureSelector selector) throws Exception {
                return selector
                        .openGallery(PictureMimeType.ofVideo())
                        .theme(R.style.Choose_Whit)
                        .compress(false)
                        .imageSpanCount(3)
                        .isZoomAnim(false)
                        .previewVideo(true)
                        .isCamera(false);
            }
        });
    }

    /**
     * 拍照
     * @return
     */
    static ObservableTransformer<PictureSelector, PictureSelectionModel> imageCapture() {
        return upstream -> upstream.map((Function<PictureSelector, PictureSelectionModel>) selector -> {
            return selector
                    .openCamera(PictureMimeType.ofImage())
                    .theme(R.style.Choose_Whit);
        });
    }

    /**
     * 图片选择
     * @return
     */
    static ObservableTransformer<PictureSelector, PictureSelectionModel> imageChoose() {
        return upstream -> upstream
                .map((Function<PictureSelector, PictureSelectionModel>) selector -> {
                    return selector.openGallery(PictureMimeType.ofImage())
                                   .theme(R.style.Choose_Whit)
                                   .imageSpanCount(3)
                                   .isCamera(false)
                                   .previewImage(true)
                                   .isZoomAnim(false)
                                   .sizeMultiplier(0.5f)
                                   .freeStyleCropEnabled(true)
                                   .isGif(true)
                                   .minimumCompressSize(100)
                                   .rotateEnabled(true)
                                   .scaleEnabled(true)
                                   .isDragFrame(true);
                });
    }

    /**
     * 视频录制最大时间
     * @param model
     * @param maxSecond
     * @return
     */
    static PictureSelectionModel applyRecordVideoSecond(PictureSelectionModel model, int maxSecond) {
        return model.recordVideoSecond(maxSecond);
    }

    /**
     * 应用多选
     * @param model
     * @param max
     * @return
     */
    static PictureSelectionModel applyMultiple(PictureSelectionModel model, int max) {
        return model.maxSelectNum(max);
    }

    /**
     * 应用单选
     * @param model
     * @return
     */
    static PictureSelectionModel applySingle(PictureSelectionModel model) {
        return model.minSelectNum(1)
                    .selectionMode(PictureConfig.SINGLE);
    }

    /**
     * 应用剪切
     * @param model
     * @param widthRatio  宽度比例
     * @param heightRatio 高度比例
     * @return
     */
    static PictureSelectionModel applyAspectRatio(PictureSelectionModel model, int widthRatio, int heightRatio) {
        return model.enableCrop(widthRatio > 0 || heightRatio > 0)
                    .withAspectRatio(widthRatio, heightRatio);
    }

    /**
     * 保存路径
     * @param model
     * @param output
     * @return
     */
    public static PictureSelectionModel applyOutputCameraPath(PictureSelectionModel model, String output) {
        return model.setOutputCameraPath(output);
    }

    /**
     * 压缩
     * @param model
     * @return
     */
    static PictureSelectionModel applyCompress(PictureSelectionModel model) {
        return model.compress(true);
    }

    /**
     * onActivityForResult
     * @param model
     * @return
     */
    static PictureSelectionModel forResult(PictureSelectionModel model) {
        model.forResult(RxChooseResultFragment.CHOOSE_REQUEST);
        return model;
    }

    /**
     * 获取用于接收选择结果的fragment
     * @param activity
     * @return
     */
    static RxChooseResultFragment getResultFragment(FragmentActivity activity) {
        RxChooseResultFragment fragment = findResultFragment(activity);
        boolean isNewInstance = fragment == null;
        if (isNewInstance) {
            fragment = RxChooseResultFragment.newInstance();
            FragmentManager fm = activity.getSupportFragmentManager();
            fm.beginTransaction()
              .add(fragment, RESULT_TAG)
              .commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return fragment;
    }

    /**
     * 查找这个fragment是否有在activity中
     * @param activity
     * @return
     */
    static RxChooseResultFragment findResultFragment(FragmentActivity activity) {
        return (RxChooseResultFragment) activity.getSupportFragmentManager().findFragmentByTag(RESULT_TAG);
    }
}
