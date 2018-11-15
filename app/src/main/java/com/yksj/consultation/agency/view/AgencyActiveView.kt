package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.yksj.consultation.adapter.AgencyActiveAdapter
import com.yksj.consultation.bean.AgencyActiveBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_angency_active.view.*

class AgencyActiveView(context: Context, p: IPresenter) : FrameLayout(context) {

    private val presenter = p
    private val adapter by lazy { createAdapter() }

    init {
        inflater(context, R.layout.layout_angency_active, this)
        setBackgroundColor(resColor(R.color.white))
        recycler_view.adapter = adapter
    }

    /**
     * 创建适配器
     */
    private fun createAdapter(): AgencyActiveAdapter {
        val adapter = AgencyActiveAdapter(presenter.getCategroy())
        adapter.setOnItemClickListener { _, view, position ->
            var item: AgencyActiveBean = adapter.getItem(position)!!
            // 活动详情
            view.findViewById<View>(R.id.item_lay)
                    .setOnClickListener { presenter.toActiveInfo(item) }
            //  修改活动
            view.findViewById<View>(R.id.active_alter)
                    .setOnClickListener { presenter.toAlterActive(item) }
        }
        return adapter
    }

    /**
     * 绑定数据
     */
    fun bindData(datas: List<AgencyActiveBean>?) {
        if (datas != null && !datas.isEmpty()) {
            adapter.setNewData(datas)
            empty_layout.visibility = View.GONE
        } else {
            empty_layout.visibility = View.VISIBLE
            adapter.replaceData(emptyList())
        }
    }

    /**
     * 获取滑动View
     */
    fun getScrollableView(): RecyclerView = recycler_view

    interface IPresenter {
        /**
         * 跳转到活动详情
         */
        fun toActiveInfo(activeBean: AgencyActiveBean)

        /**
         * 跳转到修改活动
         */
        fun toAlterActive(activeBean: AgencyActiveBean)

        /**
         * 获取类型
         */
        fun getCategroy(): String
    }
}