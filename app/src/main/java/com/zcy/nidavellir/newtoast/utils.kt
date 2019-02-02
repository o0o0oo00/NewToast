package com.zcy.nidavellir.newtoast

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.View
import com.moretech.coterie.widget.NewToast
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
fun warning(msg: String) {
    val act = App.topActivity.get() ?: return
    if (lastNewToast.get()?.isShowing() == true && lastNewToastMsg.get() == msg) {
        return
    }
    Handler(Looper.getMainLooper()).postDelayed({
        val toast = NewToast()
        lastNewToast = WeakReference(toast)
        lastNewToastMsg = WeakReference(msg)
        toast.setImageRes(R.drawable.ic_error)
        toast.setToastText(msg)
        toast.setViewBg(R.color.colorAccent)
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

val Context.statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen",
            "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

private var lastClickTime: Long = 0
private val SPACE_TIME = 500
fun isDoubleClick(): Boolean {
    val currentTime = System.currentTimeMillis()
    val isDoubleClick: Boolean // in range
    isDoubleClick = currentTime - lastClickTime <= SPACE_TIME
    if (!isDoubleClick) {
        lastClickTime = currentTime
    }
    return isDoubleClick
}
fun Context.string(id: Int): String = resources.getString(id)
fun string(id: Int): String = App.instance.string(id)
fun Context.color(id: Int): Int = resources.getColor(id)
fun color(id: Int): Int = App.instance.color(id)


fun View.isVisibility(visibility: Boolean) {
    if (visibility) {
        if (this.visibility != View.VISIBLE) {
            this.visibility = View.VISIBLE
        }
    } else {
        if (this.visibility == View.VISIBLE) {
            this.visibility = View.GONE
        }
    }
}