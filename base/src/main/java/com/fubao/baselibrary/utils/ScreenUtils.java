package com.fubao.baselibrary.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;

public class ScreenUtils {
    /**
     * 获取屏幕真实宽度
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    /**
     * 获取屏幕真实高度，包括状态栏和标题栏高度。
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    /**
     * 简单的用屏幕所谓的宽高(宽高只是相对的)模拟屏幕的方向。<br/>
     * 宽>高：横屏；<br/>
     * 宽<高：竖屏。<br/>
     * 有正方形的屏吗？
     */
    public static int getScreenOrientation(Activity activity) {
        if (getScreenWidth(activity) > getScreenHeight(activity)) {
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (getScreenWidth(activity) < getScreenHeight(activity)) {
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }
}
