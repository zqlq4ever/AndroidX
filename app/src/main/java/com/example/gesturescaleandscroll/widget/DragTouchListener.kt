package com.example.gesturescaleandscroll.widget

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 对 view 实现拖拽移动、双指缩放效果（默认全开启）
 * 使用方法：
 * 1创建DragTouchListener实例;
 * 2设置监听 view.setOnTouchListener(DragTouchListener);
 */
@Deprecated("不推荐使用,问题很多.仅用来提供思路(放大 view 后不会模糊)")
class DragTouchListener @JvmOverloads constructor(limitParent: ViewGroup? = null) :
    View.OnTouchListener {

    private var maxWidth = 0
    private var maxHeight = 0
    private var lastX = 0
    private var lastY = 0

    //  刚触摸时的 view 坐标（用来获取按下时 view 的大小）
    private var oriLeft = 0
    private var oriRight = 0
    private var oriTop = 0
    private var oriBottom = 0
    private var baseValue = 0f

    private var dragListener: DragListener

    private var moveFlag = false
    private var lastScale = 1f

    /**
     * 当前触摸模式：
     * 无触摸;
     * 单指触摸;
     * 双指触摸;
     */
    private var currentTouchMode = TOUCH_NONE

    /**
     * 是否开启：双指触摸缩放
     */
    private var touchTwoZoomEnable = true

    /**
     * 是否取消：触摸移动
     */
    private var isCancleTouchDrag = false

    /**
     * 产生效果的 view（缩放、拖拽效果）
     */
    var mEffectView: View? = null

    /**
     * 控制是否开启两指触摸缩放
     *
     * @param touchTwoZoomEnable
     */
    fun setTouchTwoZoomEnable(touchTwoZoomEnable: Boolean): DragTouchListener {
        this.touchTwoZoomEnable = touchTwoZoomEnable
        return this
    }


    /**
     * 设置：是否取消拖拽移动
     *
     * @param cancleTouchDrag
     */
    fun setCancleTouchDrag(cancleTouchDrag: Boolean): DragTouchListener {
        isCancleTouchDrag = cancleTouchDrag
        return this
    }


    interface DragListener {
        fun actionDown(v: View?)
        fun actionUp(v: View?)
        fun dragging(listenerView: View?, left: Int, top: Int, right: Int, bottom: Int)
        fun zooming(scale: Float)
    }


    constructor(limitParent: ViewGroup?, dragListener: DragListener) : this(limitParent) {
        this.dragListener = dragListener
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK
        // 屏蔽父控件拦截 onTouch 事件
        v.parent.requestDisallowInterceptTouchEvent(true)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                dragListener.actionDown(v)
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                oriLeft = v.left
                oriRight = v.right
                oriTop = v.top
                oriBottom = v.bottom
                currentTouchMode = TOUCH_ONE
                baseValue = 0f
                lastScale = 1f
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oriLeft = v.left
                oriRight = v.right
                oriTop = v.top
                oriBottom = v.bottom
                currentTouchMode = TOUCH_TWO
                baseValue = 0f
                lastScale = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                moveFlag = !moveFlag
                if (event.pointerCount == 2) {
                    if (touchTwoZoomEnable) {
                        val x = event.getX(0) - event.getX(1)
                        val y = event.getY(0) - event.getY(1)
                        // 计算两点的距离
                        val value = sqrt((x * x + y * y).toDouble()).toFloat()
                        if (baseValue == 0f) {
                            baseValue = value
                        } else {
                            if (value - baseValue >= 10 || value - baseValue <= -10) {
                                // 当前两点间的距离 除以 手指落下时两点间的距离就是需要缩放的比例。
                                val scale = value / baseValue
                                // 缩放 view (不能用当前 touch 方法里的 view,会造成频闪效果)（只能在其他view调用）
//                                mEffectView.setScaleX(scale)
//                                mEffectView.setScaleY(scale)
                                // 改变大小进行缩放
                                // (只能缩放当前view的大小,如果是父布局,则里面的子控件无法缩小)
                                touchZoom(v, scale)
                                dragListener.zooming(scale)
                            }
                        }
                    }
                } else if (currentTouchMode == TOUCH_ONE) { // 1 个手指
                    // 如果取消拖拽,触摸就交给系统处理
                    if (isCancleTouchDrag) {
                        return false
                    }
                    // 移动图片位置
                    touchDrag(v, event)
                }
            }
            MotionEvent.ACTION_UP -> {
                baseValue = 0f
                dragListener.actionUp(v)
            }
            else -> currentTouchMode = TOUCH_NONE
        }
        return true
    }


    /**
     * 缩放view
     *
     * @param v
     * @param scale 当前距离按下时的比例  (0.8：缩小到0.8倍)
     */
    private fun touchZoom(v: View, scale: Float) {
        val oriWidth = abs(oriRight - oriLeft)
        val oriHeight = abs(oriBottom - oriTop)

        // 需要缩放的比例（1-0.9=0.1,需要缩小0.1倍;-0.1：放大0.1倍）
        val zoomScale = lastScale - scale
        val dx = (oriWidth * zoomScale / 2f).toInt()
        val dy = (oriHeight * zoomScale / 2f).toInt()
        val left = v.left + dx
        val top = v.top + dy
        val right = v.right - dx
        val bottom = v.bottom - dy
        v.layout(left, top, right, bottom)
        lastScale = scale
    }

    private fun touchDrag(v: View, event: MotionEvent) {
        val dx = event.rawX.toInt() - lastX
        val dy = event.rawY.toInt() - lastY
        var left = v.left + dx
        var top = v.top + dy
        var right = v.right + dx
        var bottom = v.bottom + dy
        if (maxWidth != 0 && maxHeight != 0) {
            // 防止移出屏幕
            if (left < 0) {
                left = 0
                right = left + v.width
            }
            if (right > maxWidth) {
                right = maxWidth
                left = right - v.width
            }
            if (top < 0) {
                top = 0
                bottom = top + v.height
            }
            if (bottom > maxHeight) {
                bottom = maxHeight
                top = bottom - v.height
            }
        }
        v.layout(left, top, right, bottom)
        dragListener.dragging(v, left, top, right, bottom)
        lastX = event.rawX.toInt()
        lastY = event.rawY.toInt()
    }

    companion object {
        private const val TOUCH_NONE = 0x00
        private const val TOUCH_ONE = 0x20
        private const val TOUCH_TWO = 0x21
    }

    /**
     * @param limitParent 拖动限制区域,防止移出屏幕(null:拖动无限制)
     */
    init {
        if (limitParent != null) {
            val vto = limitParent.viewTreeObserver
            vto.addOnPreDrawListener {
                maxHeight = limitParent.measuredHeight
                maxWidth = limitParent.measuredWidth
                true
            }
        }
        dragListener = object : DragListener {
            override fun actionDown(v: View?) {}
            override fun actionUp(v: View?) {}
            override fun dragging(
                listenerView: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int
            ) {
            }

            override fun zooming(scale: Float) {}
        }
    }
}