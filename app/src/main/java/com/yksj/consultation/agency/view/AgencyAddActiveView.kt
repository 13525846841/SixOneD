package com.yksj.consultation.agency.view

import android.app.DatePickerDialog
import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.widget.DatePicker
import com.library.base.kt.fromat
import com.library.base.kt.inflater
import com.library.base.kt.parseDate
import com.yksj.consultation.bean.AgencyActiveBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_add_active.view.*
import java.text.SimpleDateFormat
import java.util.*

class AgencyAddActiveView : NestedScrollView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val DEFAULT_FROMAT = SimpleDateFormat("yyyy年MM月dd日")
    private val presenter = context as IPresenter
    private var activeBean: AgencyActiveBean? = null
    private val nowDate by lazy { getWeeOfToday() }
    private val startDate by lazy { getWeeOfToday() }
    private val endDate by lazy { getWeeOfToday() }
    private var activeSpan: Int = 0
    private var activeTime: String = ""

    init {
        inflater(context, R.layout.layout_agency_add_active, this)
        // 提交活动
        submit.setOnClickListener {
            activeBean?.ACTIV_TITLE = active_name.text.toString()
            activeBean?.ACTIV_TIME_DESC = activeTime
            activeBean?.ACTIV_DESC = active_desc.text.toString()
            presenter.submitActive(activeBean)
        }
        // 开始时间
        start_time.setOnClickListener {
            showDatePicker(startDate) { c, y, m, d ->
                if (c.timeInMillis >= nowDate.timeInMillis) {// 选择的时间不能小于当前时间
                    startDate.set(y, m, d, 0, 0, 0)
                    endDate.set(y, m, d + activeSpan)
                    start_time.setRightString(startDate fromat DEFAULT_FROMAT)
                    end_time.setRightString(startDate fromat DEFAULT_FROMAT)
                    activeTime = "${startDate fromat DEFAULT_FROMAT}-${endDate fromat DEFAULT_FROMAT}"
                }
            }
        }
        // 结束时间
        end_time.setOnClickListener {
            showDatePicker(endDate) { c, y, m, d ->
                if (c.timeInMillis >= startDate.timeInMillis) {// 选择的时间要大于设置的时间
                    endDate.set(y, m, d)
                    end_time.setRightString(endDate fromat DEFAULT_FROMAT)
                    // 计算活动天数
                    activeSpan = endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR)
                    activeTime = "${startDate fromat DEFAULT_FROMAT}-${endDate fromat DEFAULT_FROMAT}"
                }
            }
        }
        // 初始化开始时间
        start_time.setRightString(nowDate fromat DEFAULT_FROMAT)
        // 初始化结束时间
        end_time.setRightString(nowDate fromat DEFAULT_FROMAT)
        // 开始时间-结束时间  用于提交
        activeTime = "${startDate fromat DEFAULT_FROMAT}-${endDate fromat DEFAULT_FROMAT}"
    }

    /**
     * 获取当前当天凌晨
     */
    private fun getWeeOfToday(): Calendar {
        val nowDate = Calendar.getInstance()
        nowDate.set(Calendar.HOUR_OF_DAY, 0)
        nowDate.set(Calendar.SECOND, 0)
        nowDate.set(Calendar.MINUTE, 0)
        nowDate.set(Calendar.MILLISECOND, 0)
        return nowDate
    }

    /**
     * show时间选择dialog
     */
    private inline fun showDatePicker(calendar: Calendar, crossinline callback: (calendar: Calendar, year: Int, month: Int, day: Int) -> Unit) {
        val datePicker = DatePickerDialog(context, R.style.DatePickerDialog, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val temp = Calendar.getInstance()
                temp.set(year, month, dayOfMonth, 0, 0, 0)
                callback.invoke(temp, year, month, dayOfMonth)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    /**
     * 绑定数据
     */
    fun bindData(active: AgencyActiveBean) {
        activeBean = active
        active.ACTIV_TITLE?.apply { active_name.append(this) }
        active.ACTIV_TIME_DESC?.apply {
            for ((key, value) in split("-").withIndex()) {
                if (key == 0) {
                    startDate.timeInMillis = value parseDate DEFAULT_FROMAT
                    start_time.setRightString(startDate fromat DEFAULT_FROMAT)
                } else if (key == 1) {
                    endDate.timeInMillis = value parseDate DEFAULT_FROMAT
                    end_time.setRightString(endDate fromat DEFAULT_FROMAT)
                }
            }
            activeSpan = endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR)
            activeTime = "${startDate fromat DEFAULT_FROMAT}-${endDate fromat DEFAULT_FROMAT}"
        }
        active.ACTIV_DESC?.apply { active_desc.append(this) }
    }

    interface IPresenter {
        /**
         * 上传活动
         */
        fun submitActive(active: AgencyActiveBean?)
    }
}