package com.yksj.consultation.union.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.library.base.imageLoader.ImageLoader
import com.library.base.kt.confrimDialog
import com.library.base.kt.inflater
import com.library.base.kt.resColor
import com.yksj.consultation.bean.UnionBean
import com.yksj.consultation.comm.ImageBrowserActivity
import com.yksj.consultation.sonDoc.R
import com.yksj.consultation.sonDoc.consultation.main.BarCodeActivity
import com.yksj.consultation.union.UnionIncidentActivity
import com.yksj.consultation.union.UnionMemberListActivity
import kotlinx.android.synthetic.main.layout_union_action.view.*
import kotlinx.android.synthetic.main.layout_union_header.view.*
import kotlinx.android.synthetic.main.layout_union_home.view.*

/**
 * 医生联盟首页界面
 */
class UnionHomeView : RelativeLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var unionBean: UnionBean? = null

    init {
        inflater(context, R.layout.layout_union_home, this)
        setBackgroundColor(resColor(R.color.white))
        lay_incident_tree.setScrollingEnabled(false)
        // 大事件点击事件
        act_incident.setOnClickListener {
            unionBean?.apply {
                val intent = UnionIncidentActivity.getCallingIntent(context, UNION_ID)
                context.startActivity(intent)
            }
        }
        // 专家团点击事件
        act_member.setOnClickListener {
            unionBean?.apply {
                context.startActivity(UnionMemberListActivity.getCallingIntent(context, UNION_ID))
            }
        }
        // 二维码点击事件
        act_barcode.setOnClickListener {
            unionBean?.let {
                //跳转到二维码界面
                BarCodeActivity.from(context)
                        .setQrPath(it.qrCodeUrl)
                        .setId(it.UNION_ID)
                        .setName(it.UNION_NAME)
                        .setTitle(it.UNION_NAME)
                        .toStart()
            }
        }
    }

    /**
     * 绑定显示数据
     */
    fun bindData(unionBean: UnionBean) {
        this.unionBean = unionBean
        // TODO 暂时没有图片，这里写死
        setCover("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530617806661&di=813ade4163b16fab51e80255a4c9f602&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F10dfa9ec8a136327db729f349b8fa0ec08fac71e.jpg")
        tv_browse.text = unionBean.VISIT_TIME.toString()
        tv_member.text = unionBean.EXPERT_COUNT.toString()
        tv_follow.text = unionBean.FOLLOW_COUNT.toString()
        tv_message.text = "这几天出去玩耍，请多多包含～～～"
        exp_speciality.setTitle("擅长领域").setContent(unionBean.BE_GOOD)
        exp_desc.setTitle("联盟简介").setContent(unionBean.UNION_DESC)
        lay_incident_tree.requestIncident(unionBean.UNION_ID, false)
        tv_join.apply {
            when (unionBean.JOIN_FLAG) {
                1 -> text = "退出联盟"
                2 -> text = "加入联盟"
                else -> visibility = View.GONE
            }
        }
    }

    /**
     * 设置医生联盟封面图片
     */
    fun setCover(url: String) {
        ImageLoader.load(url).into(iv_cover)
        // 点击封面跳转到图片详情界面
        iv_cover.setOnClickListener {
            ImageBrowserActivity.BrowserSpace
                    .from(context)
                    .setImagePath(url)
                    .startActivity()
        }
    }

    /**
     * 设置医生联盟加入按钮点击事件
     */
    fun setJoinClickListener(listener: View.OnClickListener) {
        tv_join.setOnClickListener {
            context.confrimDialog("是否加入，${unionBean?.UNION_NAME}", listener)
        }
    }
}