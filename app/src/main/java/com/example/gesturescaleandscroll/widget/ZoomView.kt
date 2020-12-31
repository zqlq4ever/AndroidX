package com.example.gesturescaleandscroll.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * 自定义缩放控件
 * 可实现单指移动,双指缩放.
 * 用法:将需要缩放的布局,放在 ZoomView 子布局中.
 */
class ZoomView : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // 移动X
    private var transX = 0f

    // 移动Y
    private var trans = 0f

    // 伸缩比例
    private var scale = 1f

    // 旋转角度
    private var rotationValue = 0f

    // 移动过程中临时变量
    private var actionX = 0f
    private var actionY = 0f
    private var spacing = 0f
    private var degree = 0f

    // 0=未选择，1=拖动，2=缩放
    private var moveType = 0

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                moveType = 1
                actionX = event.rawX
                actionY = event.rawY
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                moveType = 2
                spacing = getSpacing(event)
                degree = getDegree(event)
            }
            MotionEvent.ACTION_MOVE -> if (moveType == 1) {
                transX = transX + event.rawX - actionX
                trans = trans + event.rawY - actionY
                translationX = transX
                translationY = trans
                actionX = event.rawX
                actionY = event.rawY
            } else if (moveType == 2) {
                scale = scale * getSpacing(event) / spacing
                if (scale < 1) {
                    scale = 1f
                } else if (scale > 6) {
                    scale = 6f
                }
                scaleX = scale
                scaleY = scale
                rotationValue = rotationValue + getDegree(event) - degree
                if (rotationValue > 360) {
                    rotationValue -= 360
                }
                if (rotationValue < -360) {
                    rotationValue += 360
                }
                rotation = rotationValue
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> moveType = 0
        }
        return super.onTouchEvent(event)
    }

    // 触碰两点间距离
    private fun getSpacing(event: MotionEvent): Float {
        //通过三角函数得到两点间的距离
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    // 取旋转角度
    private fun getDegree(event: MotionEvent): Float {
        //得到两个手指间的旋转角度
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(deltaY, deltaX)
        return Math.toDegrees(radians).toFloat()
    }

    init {
        isClickable = true
    }
}