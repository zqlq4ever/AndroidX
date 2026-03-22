package com.fubao.baselibrary.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 所有Activity的管理类，可以将所有Activity统一保存起来，以便于退出应用的时候kill所有Activity。
 */
public class ActivityManager {

    private static final ArrayList<WeakReference<Activity>> activityList = new ArrayList<>();

    public static void add(WeakReference<Activity> activityWeakReference) {
        activityList.add(activityWeakReference);
    }

    public static void remove(WeakReference<Activity> activityWeakReference) {
        activityList.remove(activityWeakReference);
    }

    public static void finishAll() {
        for (WeakReference<Activity> activityWeakReference : activityList) {
            Activity activity = activityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }
}
