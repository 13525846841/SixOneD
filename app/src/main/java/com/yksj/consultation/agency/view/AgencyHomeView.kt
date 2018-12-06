package com.yksj.consultation.agency.view

import android.content.Context
import android.os.Build
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.library.base.baidu.BaiduLocationHelper
import com.library.base.kt.inflater
import com.library.base.kt.resDimen
import com.library.base.widget.ScrollableHelper
import com.library.base.widget.ScrollableLayout
import com.yksj.consultation.agency.constant.AgencyCategroy
import com.yksj.consultation.agency.fragment.AgencyCategroySubFragment
import com.yksj.consultation.bean.AgencyBean
import com.yksj.consultation.sonDoc.R
import com.yksj.doctorhome.agency.view.LocationPopu
import com.yksj.doctorhome.agency.view.MenuPopu
import kotlinx.android.synthetic.main.layout_agency_home.view.*

/**
 * 机构首页View
 */
class AgencyHomeView(context: Context, p: IPresenter) : LinearLayout(context) {

    private val presenter = p
    private val menuPopu = createMenu()
    private val locationPopu by lazy { createLocation() }
    private var locationName = locationPopu.getLocationName()
    private var locationCode = locationPopu.getLocationCode()

    init {
        inflater(context, R.layout.layout_agency_home, this)
        // 设置布局方向
        orientation = VERTICAL
        search_layout.setSearchHint("搜索机构")
        title_back.setOnClickListener { presenter.onBackClick() }
        title_more.setOnClickListener { menuPopu.showAsDropDown(it) }
        tv_location.setOnClickListener { locationPopu.showAsDropDown(it) }
        experience_active.setOnClickListener { presenter.onExperienceClick(it, locationCode) }
        expand_active.setOnClickListener { presenter.onExpandClick(it, locationCode) }
        rehabilitation_active.setOnClickListener { presenter.onRehabilitationClick(it, locationCode) }
        interest_active.setOnClickListener { presenter.onInterestClick(it, locationCode) }

        // 监听滑动事件 动态改变Z轴变化
        scrollable.setStickedChangeListener { direction, isSticked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tab_layout.elevation = if (isSticked && direction == ScrollableLayout.DIRECTION.UP) {
                    resDimen(R.dimen.title_elevation).toFloat()
                } else {
                    0f
                }
            }
        }
        //获取当前位置
        BaiduLocationHelper.getInstance(context)
                .setChangeListener { helper, location ->
                    helper.stopLocation()
                    val loc = location.district
                    val code = location.adCode
                    initPager(code)
                    refreshLocation(loc, code)
                    locationPopu.setLocation(loc, code)
                }
                .startLocation()
    }

    /**
     * 初始化Page
     */
    private fun initPager(code: String) {
        categroy_view.initialize(AgencyCategroy.RECOMMENT, code)
        tab_layout.setViewPager(categroy_view.getPager())
        // 设置滑动
        scrollable.helper.setCurrentScrollableContainer(categroy_view.getFragments()[0] as ScrollableHelper.ScrollableContainer)
        categroy_view.getPager().addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                scrollable.helper.setCurrentScrollableContainer(categroy_view.getFragments()[position] as ScrollableHelper.ScrollableContainer)
            }
        })
    }

    /**
     * 刷新地址
     */
    private fun refreshLocation(loc: String, code: String) {
        locationName = loc
        locationCode = code
        tv_location.text = loc
        for (f in categroy_view.getFragments()) {
            if (f is AgencyCategroySubFragment) {
                f.refreshData(code)
            }
        }
    }

    /**
     * 显示更多菜单
     */
    private fun createMenu(): PopupWindow {
        val menuPopu = MenuPopu(context, arrayListOf("我的机构", "申请加入"))
        menuPopu.setOnItemClickListener(object : MenuPopu.OnMenuItemClickListener {
            override fun onItemClick(popu: MenuPopu, position: Int, item: String) {
                popu.dismiss()
                when (position) {
                    0 -> presenter.toSelfAgency()
                    1 -> presenter.onApplyJoin()
                }
            }
        })
        return menuPopu
    }

    /**
     * 显示地址选择菜单
     */
    private fun createLocation(): LocationPopu {
        val locationPopu = LocationPopu(context)
        locationPopu.setOnChangeListener(object : LocationPopu.OnChangeListener {
            override fun onChanged(completeCity: String, city: String, code: String) {
                refreshLocation(city, code)
            }
        })
        locationPopu.setOnVisibleListener(object : LocationPopu.OnVisibleListener {
            override fun onDismiss() {
                iv_arrow.animate()
                        .rotation(0f)
                        .setInterpolator(DecelerateInterpolator())
                        .setDuration(300)
                        .start()
            }

            override fun onShow() {
                iv_arrow.animate()
                        .rotation(-180f)
                        .setInterpolator(DecelerateInterpolator())
                        .setDuration(300)
                        .start()
            }
        })
        return locationPopu
    }

    interface IPresenter {
        fun onExperienceClick(v: View, areaCode: String)
        fun onExpandClick(v: View, areaCode: String)
        fun onRehabilitationClick(v: View, areaCode: String)
        fun onInterestClick(v: View, areaCode: String)
        fun onApplyJoin()
        fun toSelfAgency()
        fun toAgencyInfo(bean: AgencyBean)
        fun onBackClick()
    }
}