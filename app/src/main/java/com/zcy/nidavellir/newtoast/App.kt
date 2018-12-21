package com.zcy.nidavellir.newtoast

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.SoftReference
import kotlin.math.log

/**
 * @author:       zhaochunyu
 * @description:  ${DESP}
 * @date:         2018/12/19
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        this.registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity) {
                topActivity = SoftReference(activity)
                Log.e("123","onActivityStarted ${activity::class.java.simpleName}")
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
    }

    companion object {
        lateinit var instance: Application
        lateinit var topActivity: SoftReference<Activity>

    }
}