package com.yksj.consultation.station.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.library.base.widget.SuperTextView
import com.yksj.consultation.constant.PayType
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_pay_type_choose.view.*

class LecturePayModelChooseView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var checkedId: Int = 0
    private val checkedListener = SuperTextView.OnRadioCheckChangeListener { v, b ->
        if (checkedId != 0) {
            findViewById<SuperTextView>(checkedId).setRadioChecked(false)
        }
        if (checkedId == v.id) {
            return@OnRadioCheckChangeListener
        }
        checkedId = v.id
        v.setRadioChecked(true)
    }

    init {
        inflater(context, R.layout.layout_pay_type_choose, this)
        setBackgroundColor(resColor(R.color.bg_gray))

        checkedId = overage_pay.id
        overage_pay.setRadioChecked(true)
        overage_pay.setRadioCheckedChangeListener(checkedListener)
        union_pay.setRadioCheckedChangeListener(checkedListener)
        wechat_pay.setRadioCheckedChangeListener(checkedListener)
        ali_pay.setRadioCheckedChangeListener(checkedListener)
    }

    fun payModel(): Int = when {
        union_pay.radioIsChecked() -> PayType.UNION
        wechat_pay.radioIsChecked() -> PayType.WECHAT
        ali_pay.radioIsChecked() -> PayType.ALI
        else -> PayType.OVERAGE
    }

    fun setOverage(overage: Float) {
        overage_pay.setLeftString("${overage}元")
    }
}