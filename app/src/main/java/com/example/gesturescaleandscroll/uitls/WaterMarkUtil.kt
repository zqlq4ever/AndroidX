package com.example.gesturescaleandscroll.uitls

import android.app.Activity
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntRange
import com.blankj.utilcode.util.ConvertUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sqrt

/**
 * 添加水印
 */
class WaterMarkUtil private constructor() {
    /**
     * 水印文本
     */
    private var mText = ""

    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    private var mTextColor: Int

    /**
     * 字体大小，单位为sp
     */
    private var mTextSize: Float

    /**
     * 旋转角度
     */
    private var mRotation: Float

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WaterMarkUtil() }
    }

    init {
        mTextColor = -0x51515152
        mTextSize = 18f
        mRotation = -25f
    }


    /**
     * 设置水印文本
     *
     * @param text 文本
     * @return Watermark实例
     */
    fun setText(text: String): WaterMarkUtil {
        mText = text
        return sInstance
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     * @return Watermark实例
     */
    fun setTextColor(color: Int): WaterMarkUtil {
        mTextColor = color
        return sInstance
    }

    /**
     * 设置字体大小
     *
     * @param size 大小，单位为sp
     * @return Watermark实例
     */
    fun setTextSize(size: Float): WaterMarkUtil {
        mTextSize = size
        return sInstance
    }

    /**
     * 设置旋转角度
     *
     * @param degrees 度数
     * @return Watermark实例
     */
    fun setRotation(degrees: Float): WaterMarkUtil {
        mRotation = degrees
        return sInstance
    }

    /**
     * 显示水印，铺满整个页面
     */
    @JvmOverloads
    fun show(activity: Activity, text: String? = mText) {
        val drawable = WatermarkDrawable()
        drawable.mText = text
        drawable.mTextColor = mTextColor
        drawable.mTextSize = mTextSize
        drawable.mRotation = mRotation
        val layout = FrameLayout(activity)
        layout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layout.background = drawable
        activity.root.addView(layout)
    }

    inner class WatermarkDrawable : Drawable() {

        private val mPaint: Paint = Paint()

        /**
         * 水印文本
         */
        var mText: String? = null

        /**
         * 字体颜色，十六进制形式，例如：0xAEAEAEAE
         */
        var mTextColor = 0X66D3D3D3

        /**
         * 字体大小，单位为sp
         */
        var mTextSize = 0f

        /**
         * 旋转角度
         */
        var mRotation = 0f

        override fun draw(canvas: Canvas) {
            val width = bounds.right
            val height = bounds.bottom
            val diagonal = sqrt((width * width + height * height).toDouble()).toInt() // 对角线的长度

            mPaint.color = mTextColor
            mPaint.textSize = ConvertUtils.sp2px(mTextSize).toFloat()
            mPaint.isAntiAlias = true

            val textWidth = mPaint.measureText(mText)
            canvas.drawColor(0x00000000)
            canvas.rotate(mRotation)
            var index = 0
            var fromX: Float
            var positionY = diagonal / 10
            while (positionY <= diagonal) {
                fromX = -width + index++ % 2 * textWidth // 上下两行的X轴起始点不一样，错开显示
                var positionX = fromX
                while (positionX < width) {
                    canvas.drawText(mText ?: "", positionX, positionY.toFloat(), mPaint)
                    positionX += textWidth * 2
                }
                positionY += diagonal / 10
            }
            canvas.save()
            canvas.restore()
        }

        override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {}

        override fun setColorFilter(colorFilter: ColorFilter?) {}

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

    }
}