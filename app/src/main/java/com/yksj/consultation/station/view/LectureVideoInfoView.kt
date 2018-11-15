package com.yksj.consultation.station.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import com.library.base.kt.inflater
import com.yksj.consultation.app.AppContext
import com.yksj.consultation.bean.LectureBean
import com.yksj.consultation.dialog.PriceChooseDialog
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_video_info.view.*

class LectureVideoInfoView(context: Context, p: IPersent) : ConstraintLayout(context) {

    private var lectureBean: LectureBean? = null
    private val persent = p

    init {
        inflater(context, R.layout.layout_lecture_video_info, this)
        reward_lay.setOnClickListener {
            PriceChooseDialog.newInstance()
                    .setListener { payType, price ->
                        persent.requestPay(payType, price.price)
                    }
                    .show((context as FragmentActivity).supportFragmentManager)
        }
    }

    fun bindData(lectureBean: LectureBean) {
        this.lectureBean = lectureBean
        doctor_info.bindData(lectureBean)
        tv_mark.text = lectureBean.avgStar.toString()
        rating.progress = lectureBean.avgStar
        tv_content.text = lectureBean.COURSE_DESC
        comment_num.text = "${lectureBean.EvaNum}条评论"
        val videoPath = lectureBean.COURSE_ADDRESS
        val avatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + lectureBean.SMALL_PIC
        player_view.setUrl(videoPath, avatarPath)
    }

    fun getPlayer(): PlayerView{
        return player_view
    }

    interface IPersent{
        fun requestPay(payType: Int, price: Float)
    }
}
