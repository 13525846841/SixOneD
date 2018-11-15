package com.yksj.consultation.station.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.library.base.kt.resDimen
import com.yksj.consultation.bean.LectureUploadBean
import com.yksj.consultation.constant.LectureType
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_release_article.view.*

/**
 * 图文编辑-View
 */
class LectureReleaseArticleView(context: Context, presenter: IPresenter) : ConstraintLayout(context) {

    private var picturePath: String = ""
    private val mPresenter = presenter

    init {
        inflater(context, R.layout.layout_lecture_release_article, this)
        setBackgroundColor(resColor(R.color.white))
        setPadding(resDimen(R.dimen.padding_left), 0, resDimen(R.dimen.padding_right), 0)
        iv_picture.setOnClickListener {
            mPresenter.onPictureChooseClick()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        requestLayout()
    }

    /**
     * 设置图片路径
     */
    fun setPicture(picturePath: String) {
        this.picturePath = picturePath
        ImageLoader.load(picturePath).into(iv_picture)
    }

    fun toBean(stationId: String): LectureUploadBean? {
        val uploadBean = LectureUploadBean()
        uploadBean.lectureType = LectureType.GRAPHIC_TYPE
        uploadBean.title = et_title.text.toString()
        uploadBean.content = et_content.text.toString()
        uploadBean.picturePath = picturePath
        uploadBean.stationId = stationId
        uploadBean.avatarPath = ""
        uploadBean.videoPath = ""
        return uploadBean
    }

    interface IPresenter{

        /**
         * 选择图片
         */
        fun onPictureChooseClick()
    }
}