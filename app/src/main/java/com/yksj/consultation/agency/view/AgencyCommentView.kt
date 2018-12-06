package com.yksj.consultation.agency.view

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.library.base.kt.inflater
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_comment.view.*

/**
 * 机构详情 评论View
 * TODO 暂时没有接口
 */
class AgencyCommentView(context: Context, presenter: IPresenter):FrameLayout(context){

    private val presenter = presenter

    init {
        inflater(context, R.layout.layout_agency_comment, this)
    }

    fun getScrollableView(): View {
        return recycler_view
    }

    interface IPresenter{

    }
}