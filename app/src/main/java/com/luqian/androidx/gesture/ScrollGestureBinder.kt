package com.luqian.androidx.gesture

import android.content.Context
import android.view.GestureDetector

internal class ScrollGestureBinder(context: Context?, scrollGestureListener: ScrollGestureListener?)
    : GestureDetector(context, scrollGestureListener)