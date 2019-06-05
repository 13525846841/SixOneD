package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v4.widget.NestedScrollView
import com.library.base.kt.inflater
import com.yksj.consultation.bean.AgencyBean
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_info_desc.view.*

/**
 * 机构详情-简介View
 */
class AgencyDescView(context: Context, p: IPresenter): NestedScrollView(context){

    private val presenter = p

    init{
        inflater(context, R.layout.layout_agency_info_desc, this)
    }

    /**
     * 绑定显示数据
     */
    fun bindData(data: AgencyBean){
        angency_name.text = data.name
        angency_location.text = data.address +"\n"+ data.detailAddredd
        angency_traffic.text = data.address + data.detailAddredd
        angency_desc.text = data.desc
        angency_telephone.setOnClickListener { presenter.callPhone(data.telephone) }
    }

    interface IPresenter{
        fun callPhone(telephone: String)
    }
}