package com.yksj.consultation.station.view

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.library.base.kt.afterMeasured
import com.library.base.kt.inflater
import com.luck.picture.lib.tools.DateUtils
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_player_controller.view.*

class LectureMediaController : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // 默认控制栏隐藏时间
    private val DEFAULT_HIDE = 6000L
    private val HIDE_MESSAGE = 1
    private val PROGRESS_MESSAGE = 2
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                HIDE_MESSAGE -> hide()
                PROGRESS_MESSAGE -> setProgress()
            }
        }
    }
    // 是否静音
    private var mute: Boolean = true
    private var playerStatus: Boolean = true
    private var isShowing: Boolean = false
    private var isDagging: Boolean = false
    private val seekRunnable: Runnable by lazy {
        Runnable {
            mediaControllerListener?.seekTo(progressBar.progress.toLong())
        }
    }
    var mediaControllerListener: MediaControllerListener? = null
    var onHideListener: OnControllerHideListener? = null
    var onShowListener: OnControllerShowListener? = null

    init {
        inflater(context, R.layout.layout_lecture_player_controller, this)
        volumeBtn.setOnClickListener {
            mute = !mute
            updataMute(mute)
        }
        playerBtn.setOnClickListener {
            playerStatus = !playerStatus
            updataPlayer(playerStatus)
        }
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isDagging) {
                    handler.removeCallbacks(seekRunnable)
                    handler.postDelayed(seekRunnable, 200)
                    startTime.text = DateUtils.timeParse(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isDagging = true
                show(mediaControllerListener?.getTotalDuration()!!)
                handler.removeMessages(PROGRESS_MESSAGE)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isDagging = false
            }
        })
        // 隐藏
        afterMeasured { translationY = measuredHeight.toFloat() }
    }

    /**
     * 重置数据
     */
    fun reset() {
        isShowing = false
        afterMeasured { translationY = measuredHeight.toFloat() }
        handler.removeMessages(PROGRESS_MESSAGE)
        startTime.text = "00:00"
        endTime.text = DateUtils.timeParse(mediaControllerListener?.getTotalDuration()!!)
        progressBar.progress = 0
        playerBtn.setImageResource(R.drawable.ic_player_controller_pause)
    }

    /**
     * 设置播放进度
     */
    fun setProgress() {
        if (isDagging) {
            return
        }
        val duration: Long = mediaControllerListener?.getTotalDuration()!!
        val curDuration: Long = mediaControllerListener?.getCurDuration()!!
        if (duration > 0) {
            progressBar.max = duration.toInt()
            progressBar.progress = curDuration.toInt()
            startTime.text = DateUtils.timeParse(curDuration)
            endTime.text = DateUtils.timeParse(duration)
            updataPlayer(playerStatus)
            if (curDuration <= duration) {
                startTimeLoop()
            } else {
                hide()
            }
        }
    }

    /**
     * 循环获取当前播放时间
     */
    fun startTimeLoop() {
        stopTimeLoop()
        handler.sendMessageDelayed(handler.obtainMessage(PROGRESS_MESSAGE), 1000)
    }

    /**
     * 停止获取当前播放时间
     */
    fun stopTimeLoop() {
        if (handler.hasMessages(PROGRESS_MESSAGE)) {
            handler.removeMessages(PROGRESS_MESSAGE)
        }
    }

    /**
     * 显示
     */
    fun show() {
        show(DEFAULT_HIDE)
    }

    /**
     * 显示 根据时间隐藏
     */
    fun show(time: Long) {
        if (!isShowing) {
            isShowing = true
            toggleAnimation(true)
            onShowListener?.onShow()
        }
        updataPlayer(playerStatus)
        startTimeLoop()
        if (time > 0) {
            handler.removeMessages(HIDE_MESSAGE)
            handler.sendMessageDelayed(handler.obtainMessage(HIDE_MESSAGE), DEFAULT_HIDE)
        }
    }

    /**
     * 隐藏
     */
    fun hide() {
        if (isShowing) {
            isShowing = false
            toggleAnimation(false)
            onHideListener?.onHide()
        }
        updataPlayer(playerStatus)
        stopTimeLoop()
    }

    fun getFullButton(): View {
        return shrinkBtn
    }

    /**
     * 开启或关闭
     */
    private fun toggleAnimation(visiable: Boolean) {
        val start = if (visiable) measuredHeight.toFloat() else 0f
        val end = if (visiable) 0f else measuredHeight.toFloat()
        val animator = ObjectAnimator.ofFloat(this, "translationY", start, end)
        animator.apply {
            duration = 300
            start()
        }
    }

    /**
     * 更新播放状态
     */
    private fun updataPlayer(isPlaying: Boolean) {
        if (isPlaying) {
            playerBtn.setImageResource(R.drawable.ic_player_controller_play)
            mediaControllerListener?.start()
            startTimeLoop()
        } else {
            playerBtn.setImageResource(R.drawable.ic_player_controller_pause)
            mediaControllerListener?.pause()
            stopTimeLoop()
        }
    }

    /**
     * 更新静音状态
     */
    private fun updataMute(mute: Boolean) {
        mediaControllerListener?.mute(mute)
        if (mute) {
            volumeBtn.setImageResource(R.drawable.ic_controller_volume_open)
        } else {
            volumeBtn.setImageResource(R.drawable.ic_controller_volume_close)
        }
    }

    /**
     * 是否显示中
     */
    fun isShowing(): Boolean = isShowing

    /**
     * 触摸监听
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> show(0) // show until hide is called
            MotionEvent.ACTION_UP -> show(DEFAULT_HIDE) // start timeout
            MotionEvent.ACTION_CANCEL -> hide()
        }
        return true
    }

    interface MediaControllerListener {
        fun start()
        fun pause()
        fun mute(mute: Boolean)
        fun getTotalDuration(): Long
        fun getCurDuration(): Long
        fun isPlaying(): Boolean
        fun seekTo(time: Long)
    }

    interface OnControllerShowListener {
        fun onShow()
    }

    interface OnControllerHideListener {
        fun onHide()
    }
}