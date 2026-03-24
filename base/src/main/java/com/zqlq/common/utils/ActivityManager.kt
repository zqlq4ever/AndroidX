package com.zqlq.common.utils

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * 所有Activity的管理类，可以将所有Activity统一保存起来，以便于退出应用的时候kill所有Activity。
 */
object ActivityManager {

    private val activityList = ArrayList<WeakReference<Activity>>()

    @JvmStatic
    fun add(activityWeakReference: WeakReference<Activity>) {
        activityList.add(activityWeakReference)
    }

    @JvmStatic
    fun remove(activityWeakReference: WeakReference<Activity>) {
        activityList.remove(activityWeakReference)
    }

    @JvmStatic
    fun finishAll() {
        for (activityWeakReference in activityList) {
            val activity = activityWeakReference.get()
            if (activity != null && !activity.isFinishing) {
                activity.finish()
            }
        }
        activityList.clear()
    }
}
