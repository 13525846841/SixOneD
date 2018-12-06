package com.yksj.doctorhome.agency.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.library.base.kt.inflater
import com.library.base.widget.DividerListItemDecoration
import com.yksj.consultation.sonDoc.R
import com.yksj.healthtalk.db.DictionaryHelper

/**
 * 地址选择悬浮chu窗口View
 */
class LocationPopu(context: Context) : PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) {

    private var changeListener: OnChangeListener? = null
    private var visibleListener: OnVisibleListener? = null
    private lateinit var provinceName: String
    private lateinit var provinceCode : String
    private var cityName: String
    private var cityCode: String

    init {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        isFocusable = true
        contentView = inflater(context, R.layout.ppw_agency_location_choose, null)
        contentView.findViewById<View>(R.id.shade).setOnClickListener { dismiss() }
        // 市级列表
        val province = contentView.findViewById<RecyclerView>(R.id.province)
        val provinceAdapter = ProvinceAdapter(R.layout.item_location_province)
        provinceAdapter.bindToRecyclerView(province)
        province.addItemDecoration(DividerListItemDecoration())

        // 县级列表
        val city = contentView.findViewById<RecyclerView>(R.id.city)
        val citAdapter = CityAdapter(R.layout.item_choose_station)
        citAdapter.bindToRecyclerView(city)
        city.addItemDecoration(DividerListItemDecoration())

        // 初始化市级列表数据
        val provinceData = DictionaryHelper.getInstance(context).queryCity(context, 0.toString())
        provinceAdapter.setNewData(provinceData)
        // 初始化当前选择
        province.post { provinceAdapter.setSelected(0) }

        // 初始化县级列表数据
        val cityData = DictionaryHelper.getInstance(context).queryCity(context, provinceData[0].getValue("code"))
        citAdapter.setNewData(cityData)

        // 监听市级列表点击事件
        provinceAdapter.setOnItemClickListener { adapter, view, position ->
            val item = provinceAdapter.getItem(position)
            item?.apply {
                provinceName = getValue("name")
                provinceCode = getValue("code")
            }
            val citys = DictionaryHelper.getInstance(context).queryCity(context, provinceCode)
            citAdapter.setNewData(citys)// 重新设置县级列表数据
            provinceAdapter.setSelected(position)
        }

        // 监听县级列表点击事件
        citAdapter.setOnItemClickListener { adapter, view, position ->
            val item = citAdapter.getItem(position)
            item?.apply {
                cityName = getValue("name")
                cityCode = getValue("code")
                changeListener?.onChanged(provinceName + cityName, cityName, cityCode)
                dismiss()
            }
        }

        // 初始化当前选择的地区
        provinceName = provinceData[0].getValue("name")
        provinceCode = provinceData[0].getValue("code")
        cityName = DictionaryHelper.getInstance(context).queryCity(context, provinceCode)[0].getValue("name")
        cityCode = DictionaryHelper.getInstance(context).queryCity(context, provinceCode)[0].getValue("code")
    }

    fun setLocation(loc: String, code: String){
        cityName = loc
        cityCode = code
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        visibleListener?.onShow()
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        visibleListener?.onShow()
    }

    override fun dismiss() {
        super.dismiss()
        visibleListener?.onDismiss()
    }

    /**
     * 获取选择的地区名称
     */
    fun getLocationName(): String = cityName

    /**
     * 获取选择的地区编码
     */
    fun getLocationCode(): String = cityCode

    /**
     * 显示在屏幕底部
     */
    fun showScreenBottom(v: View) {
        val contentContainer = contentView.findViewById<ViewGroup>(R.id.content_container)
        val lp: FrameLayout.LayoutParams = contentContainer.layoutParams as FrameLayout.LayoutParams
        lp.gravity = Gravity.BOTTOM
        showAtLocation(v, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 设置地址变化监听
     */
    fun setOnChangeListener(changeListener: OnChangeListener): LocationPopu {
        this.changeListener = changeListener
        return this
    }

    /**
     * 设置显示\隐藏监听
     */
    fun setOnVisibleListener(visibleListener: OnVisibleListener): LocationPopu{
        this.visibleListener = visibleListener
        return this
    }

    /**
     * 省级适配器
     */
    inner class ProvinceAdapter(itemRes: Int) : BaseQuickAdapter<Map<String, String>, BaseViewHolder>(itemRes) {
        private var selectedItem: View? = null
        override fun convert(helper: BaseViewHolder, item: Map<String, String>) {
            helper.setText(R.id.menu_text, item.getValue("name"))
        }

        fun setSelected(position: Int) {
            val itemView = getViewByPosition(position, R.id.root_view)
            if (selectedItem != itemView) {
                itemView?.setBackgroundColor(Color.parseColor("#f8f8f8"))
                itemView?.findViewById<View>(R.id.iv_arrow)?.visibility = View.GONE
                selectedItem?.setBackgroundColor(Color.WHITE)
                selectedItem?.findViewById<View>(R.id.iv_arrow)?.visibility = View.VISIBLE
                selectedItem = itemView
            }
        }
    }

    /**
     * 市级适配器
     */
    inner class CityAdapter(itemRes: Int) : BaseQuickAdapter<Map<String, String>, BaseViewHolder>(itemRes) {
        override fun convert(helper: BaseViewHolder, item: Map<String, String>) {
            helper.setText(R.id.menu_text, item.getValue("name"))
        }
    }

    interface OnChangeListener {
        fun onChanged(completeCity: String, city: String, code: String)
    }

    interface OnVisibleListener{
        fun onShow()
        fun onDismiss()
    }
}