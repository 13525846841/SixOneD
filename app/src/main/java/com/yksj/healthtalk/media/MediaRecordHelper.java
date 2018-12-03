package com.yksj.healthtalk.media;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;

import com.blankj.utilcode.util.FileUtils;
import com.library.base.utils.StorageUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class MediaRecordHelper implements OnInfoListener {

    public static String TAG = MediaRecordHelper.class.getName();

    private static MediaRecordHelper INSTANCE = null;
    private final DecimalFormat mDecimalFormat2 = new DecimalFormat("0.00");
    private final DecimalFormat mDecimalFormat = new DecimalFormat("00.00");
    private int mState = RecorderState.STATE_IDLE;//初始状态
    private MediaRecorder mRecorder;
    private long mStartTime;//录音时间
    private WeakReference<ArmMediaRecordListener> mRecordListener;
    private File mSaveFile;
    private OnErrorListener mOnRecorderErrorListener = new OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            MediaRecordHelper.this.onError(RecorderState.ERROR_UNKNOWN);
        }
    };

    /**
     * 事件监听
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RecorderState.STATE_PARE://录音开始准备
                case RecorderState.STATE_IDLE://录音停止
                case RecorderState.STATE_START://录音启动
                    if (mRecordListener.get() != null) {
                        mRecordListener.get().onRecordStateChnage(msg.what);
                    }
                    break;
                case RecorderState.ERROR_SHORT://录音太短错误
                    onError(msg.what);
                    break;
                case RecorderState.ERROR_UNKNOWN://未知错误
                    onError(msg.what);
                    break;
            }
        }
    };

    private MediaRecordHelper(){}
    public static MediaRecordHelper getInstance(){
        if (INSTANCE == null) {
            synchronized (MediaRecordHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MediaRecordHelper();
                }
            }
        }
        return INSTANCE;
    }

    public void setRecordListener(ArmMediaRecordListener listener) {
        this.mRecordListener = new WeakReference<>(listener);
    }

    /**
     * 获得当前录音状态
     * @return
     */
    public synchronized int getRecordState() {
        return mState;
    }

    /**
     * 获取时间 00:00
     * @return
     */
    public String getRecordDuration() {
        long time = System.currentTimeMillis() - mStartTime;
        return mDecimalFormat.format(time / 1000f);
    }

    /**
     * 退出状态
     * @param b
     */
    public synchronized void changeCancelState(boolean b) {
        if (b) {
            this.mState = RecorderState.STATE_CANCEL;
        } else {
            this.mState = RecorderState.STATE_START;
        }
    }

    /**
     * 开始录音
     */
    public synchronized void startRecorder() {
        this.mStartTime = 0;
        if (mRecorder == null) {
            this.mRecorder = new MediaRecorder();
        }
        this.mRecorder.setOnErrorListener(mOnRecorderErrorListener);
        this.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        this.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        String saveName = System.currentTimeMillis() + "";
        this.mSaveFile = new File(StorageUtils.getVoicePath(), saveName);
        this.mRecorder.setOutputFile(mSaveFile.getAbsolutePath());
        this.mState = RecorderState.STATE_PARE;
        mStartTime = System.currentTimeMillis();
        new RecordThread().start();//启动新线程
    }

    /**
     * 停止录音
     */
    public synchronized void stopRecorder() {
        if (mRecorder == null) {
            return;
        }
        if (mState == RecorderState.STATE_PARE) {//录音处于准备阶段就停止
            mRecorder.release();
            mRecorder = null;
            onError(RecorderState.ERROR_SHORT);//录音时间太短
        } else if (mState == RecorderState.STATE_START || mState == RecorderState.STATE_CANCEL) {//处于正在录音当中停止
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                if (mStartTime == 0) {
                    onError(RecorderState.ERROR_SHORT);
                    deleteFile();
                } else {
                    long lastTime = System.currentTimeMillis() - mStartTime;
                    //移除未发送的消息
                    mHandler.removeMessages(RecorderState.STATE_PARE);
                    mHandler.removeMessages(RecorderState.STATE_START);
                    if (lastTime >= 1000L) {
                        //手动退出发送
                        if (mState == RecorderState.STATE_CANCEL) {
                            deleteFile();
                        } else {
                            if (mRecordListener.get() != null) {
                                mRecordListener.get().onRecordOver(this, mSaveFile, mDecimalFormat2.format(lastTime / 1000f), lastTime);//调用录音完成事件
                            }
                        }
                    } else {//录音时间不够
                        onError(RecorderState.ERROR_SHORT);
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                mRecorder = null;
            }
        }
        mStartTime = 0;
        mState = RecorderState.STATE_IDLE;
        if (mRecordListener.get() != null) {
            mRecordListener.get().onRecordStateChnage(RecorderState.STATE_IDLE);
        }
    }

    /**
     * 录音错误
     * @param error
     */
    private void onError(int error) {
        if (mRecordListener.get() != null) {
            mRecordListener.get().onRecordError(this, error);
        }
        try {
            deleteFile();
            if (mRecorder == null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mRecorder = null;
        }
    }

    /**
     * 删除录音文件
     */
    private void deleteFile() {
        if (mSaveFile != null) {
            FileUtils.deleteFile(mSaveFile);
        }
    }

    /**
     * 获得当前录音的最大振幅
     */
    public int getMaxAmplitude() {
        if (mRecorder == null) return 0;
        return mRecorder.getMaxAmplitude();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
		/*switch(what){
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED://录音最大时间
			break;
		case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED://最大的文件
			break;
		}*/
    }

    /**
     * 异步录音线程
     * @author zhao
     */
    private class RecordThread extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (MediaRecordHelper.this) {
                try {
                    if (getRecordState() != RecorderState.STATE_IDLE) {
                        mRecorder.prepare();
                        mHandler.sendEmptyMessage(RecorderState.STATE_PARE);//录音准备
                        mRecorder.start();
                        mHandler.sendEmptyMessage(RecorderState.STATE_START);//录音启动
                        mState = RecorderState.STATE_START;//改变状态
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(RecorderState.ERROR_UNKNOWN);//录音启动
                }
            }
        }
    }

}
