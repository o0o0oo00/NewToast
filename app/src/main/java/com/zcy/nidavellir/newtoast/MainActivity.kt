package com.zcy.nidavellir.newtoast

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onclick(v: View) {
        warning("有木有WIFI！ 有木有WIFI！")
    }

    fun onclick2(v: View) {

        askDialog(supportFragmentManager) {
            mTitle = "我是标题"
            mMessage = "我是内容 有木有WIFI！"
            lowerBackground = true
            sureClick {
            }

            cancelClick { }
        }

        Handler().postDelayed({
            warning("我是FBI Warning警告框！！！")
        }, 1000)

    }
}
