package com.yksj.consultation.station.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.PopupWindow
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.library.base.kt.resDimen
import com.library.base.utils.ResourceHelper
import com.library.base.widget.DividerListItemDecoration
import com.library.base.widget.PriceInputFilter
import com.yksj.consultation.bean.LectureUploadBean
import com.yksj.consultation.bean.StationBean
import com.yksj.consultation.bean.StationListBean
import com.yksj.consultation.dialog.WaitDialog
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_lecture_release_setup.view.*

/**
 * 健康讲堂发布设置-View
 */
class LectureReleaseSetupView : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val stationPop: PopupWindow by lazy { creatChoosePopup() }
    private val chooseAdapter: SimpleChooseAdapter by lazy { SimpleChooseAdapter() }
    private var selectedStation: StationBean? = null
    private val waitDialog: WaitDialog by lazy { createWaitDialog() }

    init {
        inflater(context, R.layout.layout_lecture_release_setup, this)
        setBackgroundColor(resColor(R.color.white))
        setPadding(resDimen(R.dimen.padding_left), resDimen(R.dimen.padding_top), resDimen(R.dimen.padding_right), resDimen(R.dimen.padding_bottom))
        et_price.filters = arrayOf(PriceInputFilter())
        btn_all_person.isSelected = true //默认所有人可以看
        station_lay.setOnClickListener { showChoosePopup() }
        btn_all_person.setOnClickListener { v -> notifySelectedChange(v) }
        btn_station_person.setOnClickListener { v -> notifySelectedChange(v) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        requestLayout()
    }

    /**
     * 观看人员选择发生变化
     */
    private fun notifySelectedChange(v: View) {
        btn_all_person.isSelected = v.id == btn_all_person.id
        btn_station_person.isSelected = v.id == btn_station_person.id
    }

    fun wrapBean(uploadBean: LectureUploadBean): LectureUploadBean {
        uploadBean.price = if (et_price.text.isEmpty()) "0" else et_price.text.toString()
        uploadBean.isIn = when (btn_station_person.isSelected) {
            true -> "1"
            false -> "0"
        }
        uploadBean.isOut = when (btn_all_person.isSelected) {
            true -> "1"
            false -> "0"
        }
        // 选择了工作站，就将课件发布到指定工作站
        selectedStation?.let { uploadBean.stationId = it.SITE_ID }
        return uploadBean
    }

    /**
     * 选择工作站
     */
    private fun showChoosePopup() {
        if (!chooseAdapter.data.isEmpty()) {
            val recyclerView: RecyclerView = stationPop.contentView.findViewById(R.id.recycler_view)
            recyclerView.adapter = chooseAdapter
            chooseAdapter.setOnItemClickListener { _, _, position ->
                selectedStation = chooseAdapter.getItem(position)
                tv_station_name.text = selectedStation?.SITE_NAME
                stationPop.dismiss()
            }
            recyclerView.addItemDecoration(DividerListItemDecoration())
            stationPop.showAsDropDown(station_lay)
        }
    }

    private fun createWaitDialog(): WaitDialog {
        return WaitDialog.newInstance("已上传0%")
                .setBackenable(false)
    }

    /**
     * 选择的工作站
     */
    private fun getSelectedstation(): StationBean? = selectedStation

    /**
     * 绑定可选择工作站列表数据
     */
    fun bindStationList(stationList: StationListBean) {
        chooseAdapter.addData(stationList.create)
        chooseAdapter.addData(stationList.join)
    }

    fun showWait(fm: FragmentManager) {
        if (!waitDialog.isShowing) {
            waitDialog.show(fm)
        }
    }

    fun updateProgress(progress: String){
        waitDialog.setContent(progress)
    }

    fun hideWait() {
        if (waitDialog.isShowing) {
            waitDialog.dismiss()
        }
    }

    /**
     * 创建一个工作站选择界面
     */
    private fun creatChoosePopup(): PopupWindow {
        val content = inflater(context, R.layout.layout_lecture_station_choose, null)
        val width = station_lay.measuredWidth
        val height = SizeUtils.dp2px(130f)
        val stationPop = PopupWindow(content, width, height)
        stationPop.isOutsideTouchable = true
        stationPop.isFocusable = true
        stationPop.setBackgroundDrawable(ResourceHelper.getDrawable(R.drawable.ic_popup_bg))
        stationPop.contentView.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                stationPop.dismiss()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        return stationPop
    }

    class SimpleChooseAdapter : BaseQuickAdapter<StationBean, BaseViewHolder>(R.layout.item_choose_station) {
        override fun convert(helper: BaseViewHolder, item: StationBean) {
            helper.setText(R.id.menu_text, item.SITE_NAME)
        }
    }
}