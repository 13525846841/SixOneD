package com.yksj.consultation.station.videoUpload;

/**
 * 视频上传回调接口
 */
public interface OnVideoUploadCallback {
    void onStart();

    void onProgress(long current, long total);

    void onComplete(String bucket, String name, String videoPath);

    void onCancel();

    void onError(String msg);
}