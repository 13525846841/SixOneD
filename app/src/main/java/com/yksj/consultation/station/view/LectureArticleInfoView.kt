package com.yksj.consultation.station.view

import android.content.Context
import android.support.v4.widget.NestedScrollView
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.inflater
import com.library.base.utils.ResourceHelper
import com.yksj.consultation.app.AppContext
import com.yksj.consultation.bean.LectureBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_graphic_info.view.*

/**
 * 健康讲堂-视频详情-View
 */
class LectureArticleInfoView(context: Context, p: IPresenter): NestedScrollView(context) {

    private val mPresenter = p
    private lateinit var mAvatarPath: String

    init{
        inflater(context, R.layout.layout_lecture_graphic_info, this)
        isFillViewport = true
        setBackgroundColor(ResourceHelper.getColor(R.color.bg_gray))
        iv_avatar.setOnClickListener {
            mPresenter.onAvatarClick(mAvatarPath)
        }
    }

    /**
     * 绑定数据
     */
    fun bindData(lectureBean: LectureBean){
        doctor_info.bindData(lectureBean)
        tv_content.text = lectureBean.COURSE_DESC
        mAvatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + lectureBean.SMALL_PIC
        ImageLoader.load(mAvatarPath).into(iv_avatar)
    }

    interface IPresenter{
        /**
         * 封面图片点击
         */
        fun onAvatarClick(path: String)
    }
}