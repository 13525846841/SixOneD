package com.yksj.consultation.union.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.yksj.consultation.adapter.UnionIncidentAdapter
import com.yksj.consultation.bean.ResponseBean
import com.yksj.consultation.bean.UnionIncidentListBean
import com.yksj.consultation.sonDoc.R
import com.yksj.healthtalk.net.http.ApiCallbackWrapper
import com.yksj.healthtalk.net.http.ApiService
import kotlinx.android.synthetic.main.layout_empty.view.*
import kotlinx.android.synthetic.main.layout_union_incident_tree.view.*

/**
 * 医生联盟大事件时间轴界面
 */
class UnionIncidentTreeView : LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // 时间轴适配器
    var adapter: UnionIncidentAdapter
    // 联盟ID
    var unionId = ""
    // 事件分页页面
    var pageIndex = 1

    init {
        inflater(context, R.layout.layout_union_incident_tree, this)
        setBackgroundColor(resColor(R.color.white))
        orientation = LinearLayout.VERTICAL
        adapter = UnionIncidentAdapter()
        val header = inflater(context, R.layout.item_union_incodent_header, null)
        adapter.addHeaderView(header)
        adapter.setOnLoadMoreListener({ requestIncident(this.unionId, true) }, rey_incodent)
        rey_incodent.adapter = adapter
    }

    /**
     * 是否可滑动 嵌套NestedScrollView 设置不能滑动
     */
    fun setScrollingEnabled(enable: Boolean) {
        rey_incodent.isNestedScrollingEnabled = enable
    }

    /**
     * 获取大事件
     */
    fun requestIncident(unionId: String, isMore: Boolean) {
        this.unionId = unionId
        if (!isMore) pageIndex = 1
        ApiService.OkHttpUnionIncident(unionId, pageIndex, object : ApiCallbackWrapper<ResponseBean<UnionIncidentListBean>>() {
            override fun onResponse(response: ResponseBean<UnionIncidentListBean>) {
                super.onResponse(response)
                if (response.isSuccess) {//加载成功
                    val incidentBeans = response.result.list
                    pageIndex++
                    if (isMore) {// 加载更多
                        if (incidentBeans.isEmpty())
                            adapter.loadMoreEnd() else {
                            adapter.addData(incidentBeans)
                            adapter.loadMoreComplete()
                        }
                    } else {// 刷新
                        if (incidentBeans.isEmpty()) {
                            rey_incodent.visibility = View.GONE
                            empty_layout.visibility = View.VISIBLE
                        } else {
                            rey_incodent.visibility = View.VISIBLE
                            empty_layout.visibility = View.GONE
                            adapter.setNewData(incidentBeans)
                        }
                    }
                }
            }
        })
    }
}