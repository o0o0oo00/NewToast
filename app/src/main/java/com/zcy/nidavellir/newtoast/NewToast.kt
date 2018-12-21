package com.zcy.nidavellir.newtoast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PointF
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.zcy.nidavellir.newtoast.R
import com.zcy.nidavellir.newtoast.svgDrawable

/**
 * @author:       zhaochunyu
 * @description:  新型Toast采用的是 WindowManager
 *                are displayed on top their attached window
 *                可实现Alert出现在Dialog背景阴影上层
 * @date:         2018/12/12
 */
class NewToast {

    private var duration = Toast.LENGTH_SHORT
    private val DURATION_LONG = 3500L
    private val DURATION_SHORT = 2000L
    private val DURATION_ANIM = 500L
    private val TRIGGER_OFFSET = App.instance.dp2px(3f)
    private val FIXED_MARGIN = App.instance.dp2px(25f)
    private var isShowing = false
    private val root: View // 根布局
    private val imageView: ImageView // icon
    private val textView: TextView // text
    private val bg: View // bg
    private var downPoint: PointF = PointF(0F, 0F) // 触摸位置
    private var dismissTime = 0L
    private var isDismissing = false // 正在取消

    private var windowManager: WindowManager
    val act: Context = App.topActivity.get() as? Activity ?: App.instance as Context

    // 初始化布局
    init {
        root = LayoutInflater.from(act).inflate(R.layout.widget_toast, null, false)
        windowManager = act.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        root.isClickable = true
        root.isFocusableInTouchMode = true
        root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downPoint = PointF(event.x, event.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    val downX = downPoint.x
                    val downY = downPoint.y
                    if (Math.abs(event.x - downX) > TRIGGER_OFFSET || Math.abs(event.y - downY) > TRIGGER_OFFSET) {
                        dismiss()
                    }
                }
                else -> {
                }
            }
            false
        }
        imageView = root.findViewById(R.id.image_view)
        textView = root.findViewById(R.id.text_view)
        bg = root.findViewById(R.id.root_view)
    }

    fun isShowing(): Boolean {
        return isShowing || (System.currentTimeMillis() - dismissTime) < DURATION_ANIM
    }

    fun setToastText(text: String) {
        var text = text
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        textView.text = text
        if (textView.lineCount == 1) {
            textView.gravity = Gravity.CENTER_VERTICAL
        }
        duration = if (text.length > 10) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
    }

    fun setViewBg(ctx: Context, @ColorRes color: Int) {
        val up = bg.background
        val drawableUp = DrawableCompat.wrap(up)
        DrawableCompat.setTint(drawableUp, ContextCompat.getColor(ctx, color))
    }

    fun setImageRes(@DrawableRes id: Int) {
        val ctx = imageView.context
        imageView.background = ctx.svgDrawable(id)
    }


    fun show() {
        isShowing = true
        App.instance.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                if (root.isAttachedToWindow) {
                    windowManager.removeView(root)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {

            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })

        // init layout params
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0, 0,
            PixelFormat.TRANSPARENT
        )
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.TOP

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or // 不获取焦点，以便于在弹出的时候 下层界面仍然可以进行操作
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR // 确保你的内容不会被装饰物(如状态栏)掩盖.
        // popWindow的层级为 TYPE_APPLICATION_PANEL
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL
        if ((act as? Activity)?.isFinishing == true) {
            return
        }
        windowManager.addView(root, layoutParams)

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthMeasureSpec, heightMeasureSpec)
        val totalOffset = (root.measuredHeight + FIXED_MARGIN).toFloat()
        val sa = ObjectAnimator.ofFloat(root, "translationY", -totalOffset, 0F)
        sa.duration = DURATION_ANIM
        sa.start()
        root.postDelayed({ dismiss() }, (if (duration == Toast.LENGTH_LONG) {
            DURATION_LONG
        } else {
            DURATION_SHORT
        }) - DURATION_ANIM)
    }

    @Synchronized
    fun dismiss() {
        if (isDismissing) {
            return
        }
        isShowing = false
        dismissTime = System.currentTimeMillis()
        isDismissing = true
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthMeasureSpec, heightMeasureSpec)
        val totalOffset = (root.measuredHeight + FIXED_MARGIN).toFloat()
        val ea = ObjectAnimator.ofFloat(root, "translationY", 0f, -totalOffset)
        ea.duration = DURATION_ANIM
        ea.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (root.isAttachedToWindow && (act as? Activity)?.isFinishing != true) {
                    windowManager.removeView(root)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        ea.start()
    }

}