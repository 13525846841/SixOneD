package com.yksj.consultation.station.videoUpload;

/**
 * 视频查询回调接口
 */
public interface OnVideoQueryCallback {
    void onStart();

    void onComplete(String vid);

    void onError(Throwable e);
}