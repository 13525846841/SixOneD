package com.yksj.consultation.station.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

abstract class LecturePlayerView : RelativeLayout, LectureMediaController.MediaControllerListener {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//    // 空闲
//    private val IDLE = 0
//    // 配置中
//    private val PREPARING = 2
//    // 配置完成
//    private val PREPARED_COMPLETE = 3
//    // 播放
//    private val STARTED = 4
//    // 暂停
//    private val PAUSED = 5
//    // 播放完毕
//    private val PLAYER_COMPLETION = 7
//    // 错误
//    private val ERROR = -1
//    // 网易播放器
//    private val mediaPlayer: NEMediaPlayer by lazy { initMediaPlayer() }
//    // 播放地址
//    private var playUri: Uri? = null
//    // 设置预处理完成的监听器，在视频预处理完成后回调
//    private val preparedListener = NELivePlayer.OnPreparedListener { player -> preparedComplete(player) }
//    // 在视频大小发生变化时调用
//    private val videoSizeChangedListener = NELivePlayer.OnVideoSizeChangedListener { neLivePlayer: NELivePlayer, width: Int, height: Int, sarNum: Int, sarDen: Int ->
//        videoWidth = neLivePlayer.videoWidth
//        videoHeight = neLivePlayer.videoHeight
//        videoSartNum = sarNum
//        videoSartDen = sarDen
//        renderView.setVideoSampleAspectRatio(videoSartNum, videoSartDen)
//        requestLayout()
//    }
//    // 在视频播放完成后调用
//    private val completionListener = NELivePlayer.OnCompletionListener { playerComplete() }
//    // 在播放发生错误时调用，收到回调时可以进行播放重试或者提示播放错误等操作
//    private val errorListener = NELivePlayer.OnErrorListener { neLivePlayer: NELivePlayer, i: Int, result: Int -> playerError(result) }
//    // 在有状态变化时调用
//    private val infoListener = NELivePlayer.OnInfoListener { neLivePlayer: NELivePlayer, i: Int, i1: Int -> true }
//    // 在seek操作完成时调用
//    private val seekCompleteListener = NELivePlayer.OnSeekCompleteListener { seekComplete() }
//    // 在视频码流解析出错时调用，收到回调时可以进行播放重试或者提示播放错误等操作
//    private val videoParseErrorListener = NELivePlayer.OnVideoParseErrorListener {}
//    // 播放状态
//    private var status: Int = IDLE
//    // 播放器宽度
//    private var videoWidth = 0
//    // 播放器高度
//    private var videoHeight = 0
//
//    private var videoSartNum = 0
//    private var videoSartDen = 0
//
//    // 资源释放广播
//    private var releaseReceiver: NEVideoViewReceiver? = null
//    private var surfaceHolder: NERenderView.ISurfaceHolder? = null
//
//    private val renderCallback = object : NERenderView.IRenderCallback {
//        override fun onSurfaceCreated(holder: NERenderView.ISurfaceHolder, width: Int, height: Int) {
//            surfaceHolder = holder
//            mediaPlayer.let { holder.bindToMediaPlayer(it) }
//        }
//
//        override fun onSurfaceChanged(holder: NERenderView.ISurfaceHolder, format: Int, width: Int, height: Int) {
//        }
//
//        override fun onSurfaceDestroyed(holder: NERenderView.ISurfaceHolder) {
//            surfaceHolder = null
//            mediaPlayer.setDisplay(null)
//        }
//    }
//
//    init {
//        inflater(context, R.layout.layout_lecture_video_player, this)
//        requestFocus()
//        registerBroadCast()
//        // 开始播放监听
//        startBtn.setOnClickListener { startPlayer("http://yun.it7090.com/video/XHLaunchAd/video03.mp4") }
//        // 暂停播放监听
//        pauseBtn.setOnClickListener { pause() }
//        renderView.addRenderCallback(renderCallback)
//        mediaController.mediaControllerListener = this
//
//        mediaController.getFullButton().setOnClickListener {
//            val activity = context as Activity
//            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            val contentView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
//            val containerView = FrameLayout(context)
//            containerView.setBackgroundColor(Color.BLACK)
//            val containerParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//            contentView.addView(containerView, containerParams)
//
//            val playerView = LecturePlayerView(context)
//            val playerParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//            playerParams.gravity = Gravity.CENTER
//            containerView.addView(playerView, playerParams)
//        }
//    }
//
//    /**
//     * 设置播放地址
//     */
//    fun startPlayer(path: String) {
//        if (status == IDLE) {//首次加载初始化播放器和预处理
//            startPlayerForUri(Uri.parseDate(path))
//        } else {// 重新播放
//            restart()
//        }
//    }
//
//    /**
//     * 设置播放地址
//     */
//    private fun startPlayerForUri(uri: Uri) {
//        playUri = uri
//        if (playUri == null) {
//            return
//        }
//        val result: Int = mediaPlayer.setDataSource(playUri.toString())
//        // 返回结果小于0视频播放出错
//        if (result < 0) {
//            errorListener.onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, result)
//        }
//        // 预处理视频源，等待接收onPrepared预处理完成通知
//        mediaPlayer.prepareAsync()
//        status = PREPARING
//        showProgress()
//    }
//
//    /**
//     * 初始化播放器
//     */
//    private fun initMediaPlayer(): NEMediaPlayer {
//        val i = Intent("com.android.music.musicservicecommand")
//        i.putExtra("command", "pause")
//        context.sendBroadcast(i)
//        val mediaPlayer = NEMediaPlayer(context)
//        mediaPlayer.apply {
//            setBufferStrategy(NELivePlayer.NELPANTIJITTER)
//            setShouldAutoplay(false)
//            setHardwareDecoder(false)
//            setOnPreparedListener(preparedListener)
//            setOnVideoSizeChangedListener(videoSizeChangedListener)
//            setOnCompletionListener(completionListener)
//            setOnErrorListener(errorListener)
//            setOnInfoListener(infoListener)
//            setOnSeekCompleteListener(seekCompleteListener)
//            setOnVideoParseErrorListener(videoParseErrorListener)
//            setScreenOnWhilePlaying(true)
//        }
//        surfaceHolder?.bindToMediaPlayer(mediaPlayer)
//        return mediaPlayer
//    }
//
//    /**
//     * 播放出错
//     */
//    private fun playerError(result: Int): Boolean {
//        status = ERROR
//        ToastUtils.showShort("视频播放出错")
//        LogUtils.e("播放出错 result = ${result}")
//        loadingProgress.visibility = View.GONE
//        return true
//    }
//
//    /**
//     * seek完成
//     */
//    private fun seekComplete() {
//        mediaController.startTimeLoop()
//    }
//
//    /**
//     * 播放完成
//     */
//    private fun playerComplete() {
//        status = PLAYER_COMPLETION
//        renderView.visibility = View.GONE
//        avatarView.visibility = View.VISIBLE
//        centerController.visibility = View.VISIBLE
//        startBtn.visibility = View.VISIBLE
//        pauseBtn.visibility = View.GONE
//        mediaController.reset()
//    }
//
//    /**
//     * 视频预处理完成
//     */
//    private fun preparedComplete(player: NELivePlayer) {
//        hideProgress()
//        status = PREPARED_COMPLETE
//        startRecorder()
//
//        renderView.visibility = View.VISIBLE
//        avatarView.visibility = View.GONE
//        centerController.visibility = View.GONE
//
//        videoWidth = player.videoWidth
//        videoHeight = player.videoHeight
//        renderView?.setVideoSampleAspectRatio(videoSartNum, videoSartDen)
//    }
//
//    /**
//     * 显示视频加载
//     */
//    private fun showProgress() {
//        loadingProgress.visibility = View.VISIBLE
//        centerController.visibility = View.GONE
//    }
//
//    /**
//     * 隐藏视频加载
//     */
//    private fun hideProgress() {
//        loadingProgress.visibility = View.GONE
//        centerController.visibility = View.VISIBLE
//    }
//
//    /**
//     * 重新播放
//     */
//    fun restart() {
//        if (isInPlayerStatus() && !isPlaying()) {
//            renderView.visibility = View.VISIBLE
//            avatarView.visibility = View.GONE
//            centerController.visibility = View.GONE
//            startRecorder()
//        }
//    }
//
//    /**
//     * 播放
//     */
//    override fun startRecorder() {
//        if (isInPlayerStatus() && !isPlaying()) {
//            mediaPlayer.startRecorder()
//            mediaController.show()
//            status = STARTED
//        }
//    }
//
//    /**
//     * 暂停
//     */
//    override fun pause() {
//        if (isInPlayerStatus() && isPlaying()) {
//            mediaPlayer.pause()
//            status = PAUSED
//        }
//    }
//
//    /**
//     * 监听触摸时间动态显示/隐藏
//     */
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (status == STARTED || status == PAUSED) {
//            if (!mediaController.isShowing()) {
//                mediaController.show()
//            }
//        }
//        return super.onTouchEvent(event)
//    }
//
//    /**
//     * 静音
//     */
//    override fun mute(mute: Boolean) {
//        if (isInPlayerStatus()) {
//            mediaPlayer.setMute(mute)
//        }
//    }
//
//    /**
//     * 拖动
//     */
//    override fun seekTo(time: Long) {
//        if (isInPlayerStatus()) {
//            mediaPlayer.seekTo(time)
//        }
//    }
//
//    private fun isInPlayerStatus(): Boolean = status != ERROR && status != IDLE && status != PREPARING
//
//    /**
//     * 是否在播放中
//     */
//    override fun isPlaying(): Boolean = mediaPlayer.run { isPlaying }
//
//    /**
//     * 获取当前已缓存位置的时间点 单位:ms
//     */
//    override fun getTotalDuration(): Long = mediaPlayer.run { duration }
//
//    /**
//     * 获取当前播放位置的时间点 单位: ms
//     */
//    override fun getCurDuration(): Long = mediaPlayer.run { currentPosition }
//
//    /**
//     * 释放资源
//     */
//    fun release() {
//        mediaPlayer.reset()
//        mediaPlayer.release()
//    }
//
//    /**
//     * @brief 注册接收资源释放结束消息的监听器
//     */
//    private fun registerBroadCast() {
//        val filter = IntentFilter()
//        filter.addAction(NEMediaPlayer.NELP_RELEASE_SUCCESS)
//        releaseReceiver = NEVideoViewReceiver()
//        context.registerReceiver(releaseReceiver, filter)
//    }
//
//    /**
//     * @brief 反注册接收资源释放结束消息的监听器
//     */
//    private fun unRegisterBroadCast() {
//        releaseReceiver?.let {
//            context.unregisterReceiver(releaseReceiver)
//        }
//    }
//
//    /**
//     * @brief 资源释放成功通知的消息接收器类
//     */
//    private inner class NEVideoViewReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action == NEMediaPlayer.NELP_RELEASE_SUCCESS) {
//                unRegisterBroadCast()
//            }
//        }
//    }
}