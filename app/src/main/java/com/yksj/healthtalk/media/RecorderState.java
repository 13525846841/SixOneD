package com.yksj.healthtalk.media;

/**
 * 录音状态
 */
public class RecorderState {
    public static final int STATE_CANCEL = 3;//取消
    public static final int STATE_IDLE = 0;//未开始
    public static final int STATE_START = 2;//录音中
    public static final int STATE_PARE = 1;//准备
    public static final int ERROR_UNKNOWN = -1;//未知错误
    public static final int ERROR_SHORT = -2;//录音时间太短
}
