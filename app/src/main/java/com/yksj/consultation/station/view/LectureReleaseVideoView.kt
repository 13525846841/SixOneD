package com.yksj.consultation.station.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.library.base.utils.StorageUtils
import com.luck.picture.lib.PictureVideoPlayActivity
import com.yksj.consultation.bean.LectureUploadBean
import com.yksj.consultation.constant.LectureType
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_release_video.view.*
import kotlin.properties.Delegates

class LectureReleaseVideoView : NestedScrollView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var coverPath: String by Delegates.notNull()
    private var videoPath: String  = ""

    init {
        isFillViewport = true
        View.inflate(context, R.layout.layout_lecture_release_video, this)
        video_lay.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("video_path", videoPath)
            val intent = Intent(context, PictureVideoPlayActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent, bundle)
        }
    }

    fun setUploadClick(listener: OnClickListener) {
        btn_upload.setOnClickListener(listener)
    }

    fun setCaptureClick(listener: OnClickListener) {
        btn_capture.setOnClickListener(listener)
    }

    fun toUploadBean(stationId: String): LectureUploadBean? {
        val uploadBean = LectureUploadBean()
        uploadBean.stationId = stationId
        uploadBean.title = et_title.text.toString()
        uploadBean.content = et_introduce.text.toString()
        uploadBean.videoPath = videoPath
        uploadBean.lectureType = LectureType.VIDEO_TYPE
        uploadBean.picturePath = ""
        uploadBean.avatarPath = generateAvatar(videoPath)
        return if (checkFormat(uploadBean)) { uploadBean } else { null }
    }

    /**
     * 生成视频封面地址
     * @param videoPath
     * @return
     */
    private fun generateAvatar(videoPath: String): String {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(videoPath)
        val bitmap = mmr.frameAtTime
        val fileName = System.currentTimeMillis().toString() + "_avatar"
        val avatarFile = StorageUtils.createImageFileByName(fileName)
        ImageUtils.save(bitmap, avatarFile, Bitmap.CompressFormat.JPEG)
        return if (avatarFile!!.exists()) avatarFile.absolutePath else ""
    }

    /**
     * 验证数据是否填写正确
     */
    fun checkFormat(uploadBean: LectureUploadBean): Boolean {
        if (uploadBean.title.isEmpty()) {
            et_title.requestFocus()
            ToastUtils.showShort("请输入标题")
            return false
        }
        if (et_introduce.text.isEmpty()) {
            et_introduce.requestFocus()
            ToastUtils.showShort("请输入课件内容")
            return false
        }
        if (videoPath.isEmpty()) {
            ToastUtils.showShort("请选择一短视频")
            return false
        }
        return true
    }

    /**
     * 视频类型
     */
    fun setVideoPath(path: String) {
        // 保存视频地址
        videoPath = path
        // 显示视频封面界面
        video_lay.visibility = View.VISIBLE
        // 用于获取视频封面
        val media = MediaMetadataRetriever()
        // 设置视频路径
        media.setDataSource(path)
        // 获取视频第一帧图片
        val bitmap = media.frameAtTime
        // 设置视频封面
        iv_cover.setImageBitmap(bitmap)
        // 保存封面图片
        val tempFile = StorageUtils.createImageFile()
        ImageUtils.save(bitmap, tempFile, Bitmap.CompressFormat.JPEG)
        coverPath = tempFile.absolutePath
    }
}