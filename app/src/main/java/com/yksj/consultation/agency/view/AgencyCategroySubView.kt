package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.library.base.kt.inflater
import com.library.base.widget.DividerGridItemDecoration
import com.library.base.widget.DividerListItemDecoration
import com.yksj.consultation.adapter.AgencyCategroyAdapter
import com.yksj.consultation.agency.AgencyHomeActivity
import com.yksj.consultation.agency.constant.AgencyCategroy
import com.yksj.consultation.bean.AgencyBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_categroy_sub.view.*

/**
 * 机构分类子View
 */
class AgencyCategroySubView(context: Context, p: IPresenter) : FrameLayout(context) {

    private val adapter by lazy { createAdapter() }
    private val presenter = p

    init {
        inflater(context, R.layout.layout_agency_categroy_sub, this)
        recycler_view.apply {
            layoutManager = createLayoutManager()
            addItemDecoration(createItemDecoration())
            adapter = this@AgencyCategroySubView.adapter
        }

        // 是否可刷新，首页不能下拉刷新
        if (getContext() is AgencyHomeActivity){
            refresh_layout.isEnableRefresh = false
        }else{
            refresh_layout.autoRefresh()
            refresh_layout.isEnableRefresh = true
        }
        refresh_layout.setOnRefreshListener { presenter.requestData(false) }
    }

    /**
     * 创建适配器
     */
    private fun createAdapter(): BaseQuickAdapter<AgencyBean, BaseViewHolder> {
        val adapter = if (AgencyCategroy.RECOMMENT === presenter.getCategroy()) {
            AgencyCategroyAdapter(R.layout.item_agency_home)
        } else {
            AgencyCategroyAdapter(R.layout.item_agency_categroy)
        }
        adapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position)
            presenter.toAgencyInfo(item as AgencyBean)
        }
        adapter.setOnLoadMoreListener { presenter.requestData(true) }
        return adapter
    }

    /**
     * 获取可滑动的View
     */
    fun getScrollableView(): View {
        return recycler_view
    }

    /**
     * 加载新数据
     */
    fun setNewData(datas: List<AgencyBean>?) {
        if (datas != null && !datas.isEmpty()) {
            adapter.setNewData(datas)
            empty_layout.visibility = View.GONE
            refresh_layout.finishRefresh()
        } else {
            empty_layout.visibility = View.VISIBLE
            adapter.replaceData(emptyList())
            refresh_layout.finishRefresh()
        }
    }

    /**
     * 加载数据
     */
    fun addData(datas: List<AgencyBean>?) {
        if (datas != null && !datas.isEmpty()) {
            adapter.addData(datas)
            adapter.loadMoreComplete()
        } else {
            adapter.loadMoreEnd()
        }
    }

    /**
     * 加载数据出错
     */
    fun error(isMore: Boolean) {
        if (isMore) {
            adapter.loadMoreFail()
        } else {
            refresh_layout.finishRefresh()
            empty_layout.visibility = View.VISIBLE
        }
    }

    /**
     * 创建布局管理器
     */
    private fun createLayoutManager(): RecyclerView.LayoutManager {
        return if (AgencyCategroy.RECOMMENT === presenter.getCategroy()) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }
    }

    /**
     * 创建item之间的分割线
     */
    private fun createItemDecoration(): RecyclerView.ItemDecoration {
        return if (AgencyCategroy.RECOMMENT == presenter.getCategroy()) {
            DividerGridItemDecoration(SizeUtils.dp2px(8f), false)
        } else {
            DividerListItemDecoration()
        }
    }

    interface IPresenter {
        fun toAgencyInfo(bean: AgencyBean)
        fun getCategroy(): String
        fun requestData(isMore: Boolean)
    }
}