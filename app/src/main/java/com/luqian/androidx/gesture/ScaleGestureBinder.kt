package com.example.gesturescaleandscroll.gesture

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class ScaleGestureBinder internal constructor(context: Context?, scaleGestureListener: ScaleGestureListener?)
    : ScaleGestureDetector(context, scaleGestureListener) {
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }
}