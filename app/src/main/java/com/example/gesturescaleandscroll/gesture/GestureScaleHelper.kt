package com.example.gesturescaleandscroll.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

@SuppressLint("ClickableViewAccessibility")
class GestureScaleHelper private constructor(context: Context, private val viewGroup: ViewGroup, private val targetView: View) {

    private val scaleGestureBinder: ScaleGestureBinder
    private val scrollGestureBinder: ScrollGestureBinder
    private val scaleGestureListener: ScaleGestureListener = ScaleGestureListener(targetView, viewGroup)
    private val scrollGestureListener: ScrollGestureListener = ScrollGestureListener(targetView, viewGroup)
    private var onScaleListener: OnScaleListener? = null
    private var isScaleEnd = true
    var isFullGroup = false
        set(isFullGroup) {
            field = isFullGroup
            scaleGestureListener.isFullGroup = isFullGroup
            scrollGestureListener.isFullGroup = isFullGroup
            fullGroup()
        }


    companion object {
        fun bind(context: Context, viewGroup: ViewGroup, targetView: View): GestureScaleHelper =
                GestureScaleHelper(context, viewGroup, targetView)
    }


    init {
        scaleGestureBinder = ScaleGestureBinder(context, scaleGestureListener)
        scrollGestureBinder = ScrollGestureBinder(context, scrollGestureListener)
        targetView.isClickable = false

        viewGroup.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.pointerCount == 1 && isScaleEnd) {
                return@OnTouchListener scrollGestureBinder.onTouchEvent(event)
            } else if (event.pointerCount == 2 || !isScaleEnd) {
                isScaleEnd = event.action == MotionEvent.ACTION_UP
                if (isScaleEnd) {
                    scaleGestureListener.onActionUp()
                }
                scrollGestureListener.setScale(scaleGestureListener.scale)
                if (onScaleListener != null) {
                    onScaleListener!!.onScale(scaleGestureListener.scale)
                }
                return@OnTouchListener scaleGestureBinder.onTouchEvent(event)
            }
            false
        })
    }


    private fun fullGroup() {
        targetView.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        targetView.viewTreeObserver.removeOnPreDrawListener(this)
                        val targetWidth = targetView.width.toFloat()
                        val targetHeight = targetView.height.toFloat()
                        val groupWidth = viewGroup.width.toFloat()
                        val groupHeight = viewGroup.height.toFloat()
                        val widthFactor = groupWidth / targetWidth
                        val heightFactor = groupHeight / targetHeight
                        targetView.layoutParams = targetView.layoutParams.also {
                            if (targetWidth < groupWidth && widthFactor * targetHeight <= groupHeight) {
                                it.width = groupWidth.toInt()
                                it.height = (widthFactor * targetHeight).toInt()
                            } else if (targetHeight < groupHeight && heightFactor * targetWidth <= groupWidth) {
                                it.height = groupHeight.toInt()
                                it.width = (heightFactor * targetWidth).toInt()
                            }
                        }
                        return true
                    }
                })
    }


    interface OnScaleListener {
        fun onScale(scale: Float)
    }


    fun setOnScaleListener(onScaleListener: OnScaleListener?) {
        this.onScaleListener = onScaleListener
    }
}