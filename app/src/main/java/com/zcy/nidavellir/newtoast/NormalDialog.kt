package com.zcy.nidavellir.newtoast

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button

/**
 * @author:       zhaochunyu
 * @description:  普通DIALOG
 * @date:         2018/12/21
 */
class NormalDialog(context: Context) : NoDarkDialog(context) {

    class Builder(private val mContext: Context) {

        fun create(): NormalDialog {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialog = NormalDialog(mContext)
            val v = inflater.inflate(R.layout.layout_dialog, null)
            dialog.setContentView(v)
            v.findViewById<Button>(R.id.button).setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }
    }

}