package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.dp2px
import com.library.base.kt.inflater
import com.yksj.consultation.bean.AgencyBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_apply.view.*

/**
 * 机构申请View
 */
class AgencyApplyView : NestedScrollView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val presenter = context as IPresenter
    private var locationName: String = ""
    private var locationCode: String = ""
    private var picturePath: String = ""
    private var agencyType: Int = 0

    init {
        inflater(context, R.layout.layout_agency_apply, this)
        agency_picture.setOnClickListener { presenter.openChoosePicture() }
        agency_location.setOnClickListener { showLocation(it) }
        agency_type.setOnClickListener { showTypeMenu(it) }
        submit.setOnClickListener { onSubmit() }
    }

    /**
     * 提交
     */
    private fun onSubmit(){
        val agency = AgencyBean()
        agency.name = agency_name.text.toString()
        agency.avatar = picturePath
        agency.desc = agency_desc.text.toString()
        agency.type = agencyType
        agency.addressCode = locationCode
        agency.address = locationName
        agency.detailAddredd = agency_location_detile.text.toString()
        agency.telephone = agency_telephone.text.toString()
        presenter.submit(agency)
    }

    /**
     * 显示机构类型选择
     */
    private fun showTypeMenu(v: View) {
        KeyboardUtils.hideSoftInput(v)
        val catePopu = CategroyPopu(context)
        val location = intArrayOf(1, 2)
        agency_type.getLocationOnScreen(location)
        location[0] -= dp2px(25f)
        catePopu.setOnItemClickListener(object : CategroyPopu.OnItemClickListener {
            override fun onItemClick(item: String, position: Int) {
                agency_type.text = item
                agencyType = when (item) {
                    "体验中心" -> 1
                    "拓展中心" -> 2
                    "康复中心" -> 3
                    "兴趣中心" -> 4
                    else -> 0
                }
            }
        })
        catePopu.showAtLocation(v, Gravity.TOP or Gravity.LEFT, location[0], location[1])
    }

    /**
     * 显示位置选择
     */
    private fun showLocation(v: View): LocationPopu {
        KeyboardUtils.hideSoftInput(v)
        val locationPopu = LocationPopu(context)
        locationPopu.showScreenBottom(v)
        locationPopu.setOnChangeListener(object : LocationPopu.OnChangeListener {
            override fun onChanged(completeCity: String, city: String, code: String) {
                agency_location.text = ""
                agency_location.text = completeCity
                locationCode = code
                locationName = completeCity
            }
        })
        return locationPopu
    }

    /**
     * 设置机构预览图片
     */
    fun setPicture(path: String) {
        ImageLoader.load(path).into(agency_picture)
        picturePath = path
    }

    interface IPresenter {
        /**
         * 打开图片选择
         */
        fun openChoosePicture()

        /**
         * 上传机构信息
         */
        fun submit(agency: AgencyBean)
    }
}