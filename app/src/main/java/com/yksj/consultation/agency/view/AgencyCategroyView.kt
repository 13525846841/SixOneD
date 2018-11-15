package com.yksj.consultation.agency.view

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.widget.FrameLayout
import com.library.base.kt.inflater
import com.yksj.consultation.agency.constant.AgencyType
import com.yksj.consultation.agency.fragment.AgencyCategroySubFragment
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_interest.view.*

class AgencyCategroyView : FrameLayout {

    constructor(context: Context):this(context, null)
    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr)

    private var contents : Array<Fragment> = emptyArray()

    init {
        inflater(context, R.layout.layout_agency_interest, this)
    }

    fun initialize(categroy: String, areaCode: String){
        contents = createContents(categroy, areaCode)
        view_pager.adapter = ContentAdapter((context as FragmentActivity).supportFragmentManager, contents)
        view_pager.offscreenPageLimit = contents.size
    }

    private fun createContents(categroy: String, areaCode: String): Array<Fragment> {
        return arrayOf(AgencyCategroySubFragment.newInstance(categroy, AgencyType.HOT, areaCode),
                AgencyCategroySubFragment.newInstance(categroy, AgencyType.NEW, areaCode),
                AgencyCategroySubFragment.newInstance(categroy, AgencyType.NEAR, areaCode))
    }

    fun getPager(): ViewPager {
        return view_pager
    }

    fun getFragments():Array<Fragment>{
        return contents
    }

    interface IPresenter{}

    inner class ContentAdapter(fm: FragmentManager, fragments: Array<Fragment>) : FragmentPagerAdapter(fm) {

        private val fragments = fragments

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "热门"
                1 -> "最新"
                2 -> "附近"
                else -> ""
            }
        }
    }
}