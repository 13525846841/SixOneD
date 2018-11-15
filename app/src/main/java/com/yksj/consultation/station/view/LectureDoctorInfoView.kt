package com.yksj.consultation.station.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.library.base.kt.inflater
import com.yksj.consultation.bean.LectureBean
import com.yksj.consultation.doctor.DoctorHomeActivity
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_doctor_info.view.*

class LectureDoctorInfoView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var data: LectureBean? = null

    init {
        inflater(context, R.layout.layout_lecture_doctor_info, this)
        setOnClickListener {
            data?.let {
                context.startActivity(DoctorHomeActivity.getCallingIntent(context, data?.COURSE_UP_ID))
            }
        }
    }

    fun bindData(data: LectureBean) {
        this.data = data
        lectureTitle.text = data.COURSE_NAME
        doctorName.text = data.COURSE_UP_NAME
    }
}