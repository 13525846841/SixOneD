package com.yksj.consultation.station.view

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ImageUtils
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.*
import com.yksj.consultation.bean.LectureBean
import com.yksj.consultation.sonDoc.R
import com.yksj.consultation.utils.DoctorHelper
import kotlinx.android.synthetic.main.layout_lecture_desc.view.*

/**
 * 健康讲堂 课程介绍view界面
 * 主要处理课程介绍界面数据的显示
 */
class LectureDescView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var data: LectureBean? = null
    private var listener: OnActiveListener? = null

    init {
        inflater(context, R.layout.layout_lecture_desc, this)
        setBackgroundColor(resColor(R.color.bg_gray))
        comment_num.setOnClickListener { v ->
            data?.let { listener?.onCommentClick(v, it.COURSE_ID) }
        }
        pay_btn.setOnClickListener { v ->
            data?.let { listener?.onPayClick(v, it) }
        }
        look_btn.setOnClickListener { v ->
            data?.let { listener?.onLookClick(v, it) }
        }
        val placeholderAvatar: Array<Bitmap?> = arrayOf(resBitmap(R.drawable.waterfall_default)
                , resBitmap(R.drawable.waterfall_default)
                , resBitmap(R.drawable.waterfall_default))
        join_avatar.setImageBitmap(combineAvatar(placeholderAvatar))
    }

    fun setActiveListener(listener: OnActiveListener) {
        this.listener = listener
    }

    /**
     * 组合头像
     */
    private fun combineAvatar(avatars: Array<Bitmap?>): Bitmap {
        val surplus = 0.6f
        val subSize = resDimen(R.dimen.dp_40)
        val height = resDimen(R.dimen.dp_40)
        val width = ((subSize * surplus) * (avatars.size - 1) + subSize).toInt()
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        var subBitmap: Bitmap
        var left = 0f
        for ((index, value) in avatars.withIndex()) {
            if (index != 0) left = (subSize * surplus) * index
            val cirBitmap = ImageUtils.toRound(value, dp2px(3f), Color.WHITE, false)
            subBitmap = Bitmap.createScaledBitmap(cirBitmap, subSize, subSize, true)
            canvas.drawBitmap(subBitmap, left, 0f, null)
        }
        return result
    }

    /**
     * 绑定数据
     */
    fun bindData(data: LectureBean) {
        this.data = data
        doctor_info.bindData(data)
        look_btn.visibility = if (isToInfo()) View.VISIBLE else View.GONE
        pay_btn.visibility = if (isToInfo()) View.GONE else View.VISIBLE
        tv_content.text = data.COURSE_DESC
        tv_price.text = "￥${data.price}"
        tv_mark.text = data.avgStar.toString()
        rating.progress = data.avgStar
        comment_num.text = "${data.EvaNum}条评论"
        pay_num.text = "${data.BuyerNum}人参与"
        val urls = arrayOf("http://img.hb.aicdn.com/eca438704a81dd1fa83347cb8ec1a49ec16d2802c846-laesx2_fw658",
                "http://img.hb.aicdn.com/729970b85e6f56b0d029dcc30be04b484e6cf82d18df2-XwtPUZ_fw658",
                "http://img.hb.aicdn.com/85579fa12b182a3abee62bd3fceae0047767857fe6d4-99Wtzp_fw658")
        downloadAvatar(urls) { join_avatar.setImageBitmap(combineAvatar(it)) }
    }

    /**
     * 是否可直接跳转到详情界面
     */
    private fun isToInfo(): Boolean {
        // 课件是自己发布的，不用付费、课件已购买，不用付费、课件免费，不用付费
        return data?.COURSE_UP_ID == DoctorHelper.getId() || data?.isPay!! || data?.price == 0f
    }

    /**
     * 下载参与人头像
     */
    private inline fun downloadAvatar(urls: Array<String>, crossinline listener: (Array<Bitmap?>) -> Unit) {
        var downIndex = 0
        val container = arrayOfNulls<Bitmap>(urls.size)
        val downCount = urls.size
        for (index in urls.indices) {
            val url = urls[index]
            Thread(Runnable {
                var bitmap = ImageLoader.load(url).submit()// 下载图片
                if (bitmap == null) bitmap = resBitmap(R.drawable.default_head_doctor)// 如果下载失败设置默认图片
                container[index] = bitmap
                downIndex++// 下载完毕后，角标加一
                if (downIndex === downCount) {// 下载完全部头像 在主线程回调
                    (context as Activity).runOnUiThread { listener(container) }
                }
            }).start()
        }
    }

    interface OnActiveListener {
        fun onCommentClick(v: View, id: String)
        fun onPayClick(v: View, data: LectureBean)
        fun onLookClick(v: View, data: LectureBean)
    }
}