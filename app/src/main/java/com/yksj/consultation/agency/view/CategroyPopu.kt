package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.library.base.kt.inflater
import com.library.base.utils.ResourceHelper
import com.yksj.consultation.sonDoc.R

/**
 * 机构类型选择悬浮窗口View
 */
class CategroyPopu(context: Context) : PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

    private val adapter by lazy { createAdapter(listOf("体验中心", "拓展中心", "康复中心", "兴趣中心")) }

    init {
        contentView = inflater(context, R.layout.ppw_agency_categroy, null)
        setBackgroundDrawable(ResourceHelper.getDrawable(R.drawable.ic_popup_bg))
        val cateRecycler = contentView.findViewById<RecyclerView>(R.id.categroy_recycler)
        cateRecycler.adapter = adapter
        isOutsideTouchable = true
        isFocusable = true
    }

    private fun createAdapter(categroys: List<String>): BaseQuickAdapter<String, BaseViewHolder> {
        return object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_choose_station, categroys) {
            override fun convert(helper: BaseViewHolder, item: String) {
                helper.setText(R.id.menu_text, item)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        adapter.setOnItemClickListener { adapter, view, position ->
            val item: String = adapter.getItem(position) as String
            listener.onItemClick(item, position)
            dismiss()
        }
    }

    interface OnItemClickListener{
        fun onItemClick(item: String, position: Int)
    }
}