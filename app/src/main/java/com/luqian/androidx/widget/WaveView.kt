package com.luqian.androidx.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import java.util.ArrayList

/**
 * 圆形中心扩散特效
 */
class WaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var mInitialRadius: Float = 0f   // 初始波纹半径
    private var mMaxRadius: Float = 0f   // 最大波纹半径
    private var mDuration: Long = 2000 // 一个波纹从创建到消失的持续时间
    private var mSpeed: Long = 500   // 波纹的创建速度，每500ms创建一个
    private var mMaxRadiusRate = 0.85f
    private var mMaxRadiusSet = false

    private var mIsRunning = false
    private var mLastCreateTime: Long = 0
    private val mCircleList = ArrayList<Circle>()

    private val mCreateCircle: Runnable = object : Runnable {
        override fun run() {
            if (mIsRunning) {
                newCircle()
                postDelayed(this, mSpeed)
            }
        }
    }

    private var mInterpolator: Interpolator = LinearInterpolator()

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        // 初始化代码可以放在这里
    }

    fun setStyle(style: Paint.Style) {
        mPaint.style = style
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (!mMaxRadiusSet) {
            mMaxRadius = w.coerceAtMost(h) * mMaxRadiusRate / 2.0f
        }
    }

    fun setMaxRadiusRate(maxRadiusRate: Float) {
        mMaxRadiusRate = maxRadiusRate
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    /**
     * 开始
     */
    fun start() {
        if (!mIsRunning) {
            mIsRunning = true
            mCreateCircle.run()
        }
    }

    /**
     * 缓慢停止
     */
    fun stop() {
        mIsRunning = false
    }

    /**
     * 立即停止
     */
    fun stopImmediately() {
        mIsRunning = false
        removeCallbacks(mCreateCircle)
        mCircleList.clear()
        invalidate()
    }

    override fun onDetachedFromWindow() {
        mIsRunning = false
        removeCallbacks(mCreateCircle)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        val iterator = mCircleList.iterator()
        while (iterator.hasNext()) {
            val circle = iterator.next()
            val radius = circle.getCurrentRadius()
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.alpha = circle.getAlpha()
                canvas.drawCircle(width / 2f, height / 2f, radius, mPaint)
            } else {
                iterator.remove()
            }
        }
        if (mCircleList.size > 0) {
            postInvalidateDelayed(10)
        }
    }

    fun setInitialRadius(radius: Float) {
        mInitialRadius = radius
    }

    fun setDuration(duration: Long) {
        mDuration = duration
    }

    fun setMaxRadius(maxRadius: Float) {
        mMaxRadius = maxRadius
        mMaxRadiusSet = true
    }

    fun setSpeed(speed: Long) {
        mSpeed = speed
    }

    private fun newCircle() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastCreateTime < mSpeed) {
            return
        }
        val circle = Circle()
        mCircleList.add(circle)
        invalidate()
        mLastCreateTime = currentTime
    }

    private inner class Circle {
        val mCreateTime: Long = System.currentTimeMillis()

        fun getAlpha(): Int {
            val percent = (getCurrentRadius() - mInitialRadius) / (mMaxRadius - mInitialRadius)
            return (255 - mInterpolator.getInterpolation(percent) * 255).toInt()
        }

        fun getCurrentRadius(): Float {
            val percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius)
        }
    }

    fun setInterpolator(interpolator: Interpolator?) {
        mInterpolator = interpolator ?: LinearInterpolator()
    }
}
