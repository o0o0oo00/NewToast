package com.zcy.nidavellir.newtoast

import android.app.Dialog
import android.content.Context

/**
 * @author:       zhaochunyu
 * @description:  处理Dialog背景阴影覆盖Warning的基类
 *                1、去除弹出时的背景阴影
 *                2、分别在show和dismiss时候设置阴影
 *
 *                原理：Dialog的Window层级要高于Activity, 所以阴影会覆盖在Warning之上,
 *                只需要把阴影替换成Activity的透明度
 * @date:         2018/12/20
 */
abstract class NoDarkDialog(private val mContext: Context) : Dialog(mContext) {

    init {
        window?.setDimAmount(0F) // 去除 dialog 弹出的阴影
    }

    override fun show() {
        mContext.setBackgroundAlpha(0.35F)
        super.show()
    }

    override fun dismiss() {
        mContext.setBackgroundAlpha(1F)
        super.dismiss()
    }

}