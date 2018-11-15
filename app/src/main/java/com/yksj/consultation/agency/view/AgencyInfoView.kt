package com.yksj.consultation.agency.view

import android.animation.LayoutTransition
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.RelativeLayout
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.dp2px
import com.library.base.kt.inflater
import com.library.base.widget.ScrollableHelper
import com.yksj.consultation.agency.constant.AgencyCategroy
import com.yksj.consultation.agency.fragment.AgencyActiveFragment
import com.yksj.consultation.agency.fragment.AgencyCommentFragment
import com.yksj.consultation.agency.fragment.AgencyDescFragment
import com.yksj.consultation.sonDoc.R
import kotlinx.android.synthetic.main.layout_agency_info.view.*

/**
 * 机构详情View
 */
class AgencyInfoView(context: Context, p: IPresenter) : RelativeLayout(context) {

    private val presenter = p
    private val contents by lazy { createContent(presenter.getAgencyId(), presenter.getCategroy()) }

    init {
        layoutTransition = LayoutTransition()
        inflater(context, R.layout.layout_agency_info, this)
        add_active.setOnClickListener { presenter.addActive() }
        view_pager.apply {
            adapter = ContentAdapter((context as FragmentActivity).supportFragmentManager, contents)
            addOnPageChangeListener(createPageListener())
            offscreenPageLimit = contents.size
        }
        tab_layout.setViewPager(view_pager)
        val container = contents[0] as ScrollableHelper.ScrollableContainer
        scrollableLayout.helper.setCurrentScrollableContainer(container)
        val bottomMargin = if (AgencyCategroy.SELF == presenter.getCategroy()) dp2px(56f) else 0
        (scrollableLayout.layoutParams as MarginLayoutParams).bottomMargin = bottomMargin
    }

    /**
     * 创建一个viewpage页面切换监听
     */
    private fun createPageListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val container = contents[position] as ScrollableHelper.ScrollableContainer
                scrollableLayout.helper.setCurrentScrollableContainer(container)
                fixed_layout.visibility = visibleActive(position)
            }

            /**
             * 是否显示添加活动／修改活动
             */
            private fun visibleActive(position: Int): Int {
                val categroy = presenter.getCategroy()
                return if (position == 1 && AgencyCategroy.SELF == categroy) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    /**
     * 设置机构封面
     */
    fun setAvatar(imgPath: String?) {
        imgPath?.let { ImageLoader.load(imgPath).into(avatar) }
    }

    /**
     * 创建fragment用于填充viewpager
     */
    private fun createContent(infoId: String, categroy: String): Array<Fragment> {
        return arrayOf(AgencyDescFragment.newInstance(infoId, categroy),
                AgencyActiveFragment.newInstance(infoId, categroy),
                AgencyCommentFragment.newInstance(infoId))
    }

    inner class ContentAdapter(fm: FragmentManager, fragments: Array<Fragment>) : FragmentPagerAdapter(fm) {
        private val fragments = fragments

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "简介"
                1 -> "活动"
                2 -> "评论"
                else -> ""
            }
        }
    }

    interface IPresenter {
        fun addActive()
        fun getAgencyId(): String
        fun getCategroy(): String
    }
}