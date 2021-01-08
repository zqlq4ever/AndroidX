package com.example.gesturescaleandscroll.gesture

import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup

class ScaleGestureListener internal constructor(private val targetView: View, viewGroup: ViewGroup?)
    : ScaleGestureDetector.OnScaleGestureListener /*, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener */ {
    var scale = 1f
        private set
    private var scaleTemp = 1f
    var isFullGroup = false


    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scale = scaleTemp * detector.scaleFactor
        targetView.scaleX = scale
        targetView.scaleY = scale
        return false
    }


    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }


    override fun onScaleEnd(detector: ScaleGestureDetector) {
        scaleTemp = scale
    }


    fun onActionUp() {
        if (isFullGroup && scaleTemp < 1) {
            scale = 1f
            targetView.scaleX = scale
            targetView.scaleY = scale
            scaleTemp = scale
        }
    }
}