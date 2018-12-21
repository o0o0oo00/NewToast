package com.zcy.nidavellir.newtoast

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.View
import java.lang.ref.WeakReference

/**
 * @author:       zhaochunyu
 * @description:  工具类
 * @date:         2018/12/19
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.svgDrawable(id: Int): Drawable {
    try {
        return VectorDrawableCompat.create(resources, id, null) ?: resources.getDrawable(id)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return resources.getDrawable(id)
}

// warning
private var lastNewToast = WeakReference<NewToast?>(null)
private var lastNewToastMsg = WeakReference<String?>(null)
fun Context.warning(msg: String) {
    if (lastNewToast.get()?.isShowing() == true && lastNewToastMsg.get() == msg) {
        return
    }
    Handler(Looper.getMainLooper()).postDelayed({
        val toast = NewToast()
        lastNewToast = WeakReference(toast)
        lastNewToastMsg = WeakReference(msg)
        toast.setImageRes(R.drawable.ic_error)
        toast.setToastText(msg)
        toast.setViewBg(this, R.color.warning)
        toast.show()
    }, 50)
}

// 黑暗 0.0F ~ 1.0F 透明
fun Context.setBackgroundAlpha(alpha: Float) {
    val act = this as? Activity ?: return
    val attributes = act.window.attributes
    attributes.alpha = alpha
    act.window.attributes = attributes
}
