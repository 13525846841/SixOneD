package com.yksj.healthtalk.media;

import java.io.File;

/**
 * 录音事件
 */
public interface ArmMediaRecordListener {
    void onRecordStateChnage(int state);//状态改变

    void onRecordError(MediaRecordHelper record, int error);

    void onRecordOver(MediaRecordHelper record, File file, String time, long durationTime);//录音结束
}
