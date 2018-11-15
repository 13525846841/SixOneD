package com.library.base.kt

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.library.base.dialog.ConfirmDialog
import java.text.DateFormat
import java.util.*

fun Any.inflater(context: Context, layoutRes: Int, viewGroup: ViewGroup?): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    return when {
        viewGroup == null -> layoutInflater.inflate(layoutRes, null, false)
        else -> layoutInflater.inflate(layoutRes, viewGroup, true)
    }
}

fun Context.confrimDialog(msg: String, listener: View.OnClickListener) {
    ConfirmDialog.newInstance("", msg)
            .addListener(object : ConfirmDialog.SimpleConfirmDialogListener() {
                override fun onPositiveClick(dialog: ConfirmDialog, v: View) {
                    super.onPositiveClick(dialog, v)
                    listener.onClick(v)
                }
            })
            .show((this as? FragmentActivity)?.supportFragmentManager)
}

fun Any.toast(msg: String) {
    ToastUtils.showShort(msg)
}

fun View.resString(stringRes: Int): String{
    return resources.getString(stringRes)
}

fun View.resColor(colorRes: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return resources.getColor(colorRes, context.theme)
    } else {
        return resources.getColor(colorRes)
    }
}

fun View.resDimen(resId: Int): Int{
    return resources.getDimensionPixelSize(resId)
}

fun View.resBitmap(resId: Int): Bitmap{
    return ImageUtils.drawable2Bitmap(resources.getDrawable(resId))
}

inline fun View.afterMeasured(crossinline f: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            f.invoke()
        }
    })
}

/**
 * 格式化时间
 */
infix fun Calendar.fromat(fromat: DateFormat): String{
    return fromat.format(time)
}

/**
 * 解析时间
 */
infix fun String.parseDate(format: DateFormat): Long{
    return try {
        format.parse(this).time
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

/**
 * dp转px
 */
fun Any.dp2px(dp: Float): Int{
    return SizeUtils.dp2px(dp)
}

/**
 * px转dp
 */
fun Any.px2dp(px: Float): Int{
    return SizeUtils.px2dp(px)
}