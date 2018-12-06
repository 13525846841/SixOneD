package com.yksj.doctorhome.agency.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.library.base.kt.inflater
import com.yksj.consultation.sonDoc.R

/**
 * 菜单悬浮窗口View
 */
class MenuPopu(context: Context, menus: List<String>) : PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) {

    private val adapter by lazy { createContentAdapter(menus) }

    init {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        contentView = inflater(context, R.layout.ppw_agency_menu, null)
        contentView.findViewById<View>(R.id.shade).setOnClickListener { dismiss() }
        val menuRecycler = contentView.findViewById<RecyclerView>(R.id.menu_recycler)
        menuRecycler.adapter = adapter
        isOutsideTouchable = true
        isFocusable = true
    }

    private fun createContentAdapter(menus: List<String>): BaseQuickAdapter<String, BaseViewHolder> {
        return object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_choose_station, menus) {
            override fun convert(helper: BaseViewHolder, item: String) {
                helper.setText(R.id.menu_text, item)
            }
        }
    }

    /**
     * 菜单列表点击事件
     */
    fun setOnItemClickListener(listener: OnMenuItemClickListener){
        adapter.setOnItemClickListener { adapter, view, position ->
            listener.onItemClick(this@MenuPopu, position, adapter.getItem(position) as String)
            dismiss()
        }
    }

    interface OnMenuItemClickListener{
        fun onItemClick(popu: MenuPopu, position:Int, item: String)
    }
}