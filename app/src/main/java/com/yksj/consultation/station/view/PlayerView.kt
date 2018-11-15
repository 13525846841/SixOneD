package com.yksj.consultation.station.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.inflater
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_player.view.*

class PlayerView : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var orientationUtils: OrientationUtils? = null
    private var isPlay = false
    private var isPause = false
    private var builder: GSYVideoOptionBuilder? = null
    private val activity = context as Activity

    init {
        inflater(context, R.layout.layout_player, this)
        // 初始化不打开外部的旋转
        orientationUtils?.isEnable = false
    }

    private fun buildPlayer(url: String, avatarUrl: String): GSYVideoOptionBuilder {
        val builder = GSYVideoOptionBuilder()
        val avatarImg = generateAvatar()
        // 加载封面图片
        ImageLoader.load(avatarUrl).into(avatarImg)
        orientationUtils = OrientationUtils(context as Activity, playerView)
        builder.setThumbImageView(avatarImg)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setUrl(url)
                .setCacheWithPlay(false)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPrepared(url: String?, vararg objects: Any?) {
                        super.onPrepared(url, *objects)
                        orientationUtils?.isEnable = true
                        isPlay = true
                    }

                    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                        super.onQuitFullscreen(url, *objects)
                        orientationUtils?.backToProtVideo()
                    }

                    override fun onAutoComplete(url: String?, vararg objects: Any?) {
                        super.onAutoComplete(url, *objects)
                        backPresses()
                        playerView.initUIState()
                    }
                })
                .setLockClickListener(object : LockClickListener {
                    override fun onClick(view: View?, lock: Boolean) {
                        orientationUtils?.isEnable = !lock
                    }
                })
                .build(playerView)
        playerView.backButton.visibility = View.GONE
        playerView.titleTextView.visibility = View.GONE
        playerView.fullscreenButton.setOnClickListener {
            // 旋转屏幕
            orientationUtils?.resolveByClick()
            playerView.startWindowFullscreen(context, true, true)
        }
        return builder
    }

    private fun generateAvatar(): ImageView {
        val avatar = ImageView(context)
        avatar.setImageResource(R.drawable.ic_default_union_covert)
        return avatar
    }

    /**
     * 设置播放地址
     */
    fun setUrl(url: String, avatarUrl: String) {
        if (builder == null) {
            builder = buildPlayer(url, avatarUrl)
        }
    }

    /**
     * 暂停播放
     */
    fun pause() {
        isPause = true
        playerView.currentPlayer.onVideoPause()
    }

    /**
     * 恢复播放
     */
    fun resume() {
        isPause = false
        playerView.currentPlayer.onVideoResume(false)
    }

    /**
     * 返回键触发
     */
    fun backPresses(): Boolean {
        orientationUtils?.backToProtVideo()
        return GSYVideoManager.backFromWindowFull(context)
    }

    /**
     * 屏幕方向发生改变
     */
    fun screenChange(newConfig: Configuration) {
        if (isPlay && !isPause) {
            playerView.onConfigurationChanged(activity, newConfig, orientationUtils, true, true)
        }
    }

    /**
     * 释放资源
     */
    fun release() {
        if (isPlay) {
            playerView.currentPlayer.release()
        }
        orientationUtils?.releaseListener()
    }
}