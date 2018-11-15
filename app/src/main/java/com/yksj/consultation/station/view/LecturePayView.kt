package com.yksj.consultation.station.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.yksj.consultation.app.AppContext
import com.yksj.consultation.bean.LectureBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_pay.view.*

/**
 * 健康讲堂 支付界面view
 * 主要用于支付界面显示，跳转基本逻辑
 */
class LecturePayView : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var data: LectureBean? = null
    private var listener: OnPayClickListener? = null

    init {
        inflater(context, R.layout.layout_lecture_pay, this)
        setBackgroundColor(resColor(R.color.bg_gray))
        confirm_pay.setOnClickListener {
            val model = model_choose.payModel()
            listener?.onPayClick(it, model)
        }
        coupon_lay.setOnClickListener {
            listener?.onCouponClick(it)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    fun setOverage(overage: Float){
        model_choose.setOverage(overage)
    }

    fun setPayClickListener(listener: OnPayClickListener){
        this.listener = listener
    }

    fun bindData(lectureBean: LectureBean) {
        this.data = lectureBean
        order_price.text = String.format("￥%s", lectureBean.price)
        order_title.text = lectureBean.COURSE_NAME
        order_price.text = "￥${lectureBean.price}"
        tv_price.text = "￥${lectureBean.price}"
        val imagePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + lectureBean.SMALL_PIC
        ImageLoader.load(imagePath).into(iv_avatar)
    }

    interface OnPayClickListener{
        fun onPayClick(v:View, payModel:Int)
        fun onCouponClick(v:View)
    }
}