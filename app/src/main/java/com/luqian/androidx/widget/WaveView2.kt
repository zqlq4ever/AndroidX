package com.luqian.androidx.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import com.luqian.androidx.R
import java.util.ArrayList

class WaveView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 颜色
     */
    private var color = Color.BLUE
    /**
     * 速度
     */
    private var speed = 20
    /**
     * 圆圈之间最大间距
     */
    private var mGap = 10f
    /**
     * 是否填充
     */
    private var isFill = true
    // View 的宽
    private var mWidth = 0f
    // View 的高
    private var mHeight = 0f
    // 声波的圆圈集合
    private var mCircles: ArrayList<Circle>
    // 画笔
    private var mPaint: Paint

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.mSpreadView)
        color = typedArray.getColor(R.styleable.mSpreadView_color, color)
        speed = typedArray.getInt(R.styleable.mSpreadView_speed, speed)
        mGap = typedArray.getFloat(R.styleable.mSpreadView_gap, mGap)
        isFill = typedArray.getBoolean(R.styleable.mSpreadView_isFill, isFill)
        typedArray.recycle()

        // 创建画笔
        mPaint = Paint()
        // 设置画笔颜色
        mPaint.color = color
        // 设置空心/实心
        if (isFill) {
            mPaint.style = Paint.Style.FILL
        } else {
            mPaint.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            mPaint.style = Paint.Style.STROKE
        }
        // 设置形状为圆形
        mPaint.strokeCap = Paint.Cap.ROUND
        // 抗锯齿
        mPaint.isAntiAlias = true

        // 添加第一个圆圈
        mCircles = ArrayList()

        mGap = ConvertUtils.dp2px(mGap).toFloat()

        // 设置View的圆为半透明
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)// 宽的测量大小，模式
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)

        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)// 高的测量大小，模式
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)

        mWidth = widthSpecSize.toFloat()   // 定义测量宽，高(不包含测量模式),并设置默认值，查看 View#getDefaultSize 可知
        mHeight = heightSpecSize.toFloat()

        // 处理 wrap_content 的几种特殊情况
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            mWidth = 200f  // 单位是 px
            mHeight = 200f
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            // 只要宽度布局参数为 wrap_content， 宽度给固定值 200dp
            mWidth = 200f
            mHeight = heightSpecSize.toFloat()
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize.toFloat()
            mHeight = 200f
        }

        // 给两个字段设置值，完成最终测量
        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            // 初始化圆的个数
            val number = mWidth.toInt() / 2 / ConvertUtils.dp2px(mGap)
            mCircles.add(Circle(0, 255))
            for (i in 1..number) {
                val circle = Circle(
                    ConvertUtils.dp2px(mGap) * i,
                    255 - ConvertUtils.dp2px(mGap) * i * (255 / (mWidth.toInt() / 2))
                )
                mCircles.add(circle)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSpecialCircles(canvas)
    }

    private fun drawSpecialCircles(canvas: Canvas) {
        canvas.save()

        // 处理每个圆的宽度和透明度
        var i = 0
        while (i < mCircles.size) {
            val c = mCircles[i]
            mPaint.alpha = c.alpha// （透明）0-255（不透明）
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.strokeWidth, mPaint)

            if (c.alpha == 0) {
                //到最后一圈后,降透明度降为0，并逐渐缩小至最小的圆
                if (c.width > mGap) {
                    c.width--
                    c.alpha = 0
                } else {
                    // 为了防止抖动，缩小至最小的圆宽度并且为透明的就删除
                    mCircles.removeAt(i)
                    i--
                }
            } else {
                // 改变透明度
                val alpha = 255 - c.width * (255 / (mWidth / 2))
                c.alpha = alpha.toInt()
                // 宽度逐渐增加
                c.width++
            }
            i++
        }

        // 添加圆圈
        if (mCircles.size > 0) {
            // 控制接下来出来的圆的间距
            val lastCircle = mCircles[mCircles.size - 1]
            if (lastCircle.alpha != 0 && lastCircle.width > ConvertUtils.dp2px(mGap)) {
                mCircles.add(Circle(0, 255))
            }
        }

        postInvalidateDelayed(speed.toLong())

        canvas.restore()
    }

    class Circle(var width: Int, var alpha: Int)
}
