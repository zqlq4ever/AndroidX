package com.zqlq.common.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.DisplayMetrics

object ScreenUtils {
    /**
     * 获取屏幕真实宽度
     */
    @JvmStatic
    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 获取屏幕真实高度，包括状态栏和标题栏高度。
     */
    @JvmStatic
    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * 简单的用屏幕所谓的宽高(宽高只是相对的)模拟屏幕的方向。<br/>
     * 宽>高：横屏；<br/>
     * 宽<高：竖屏。<br/>
     * 有正方形的屏吗？
     */
    @JvmStatic
    fun getScreenOrientation(activity: Activity): Int {
        return when {
            getScreenWidth(activity) > getScreenHeight(activity) -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            getScreenWidth(activity) < getScreenHeight(activity) -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
