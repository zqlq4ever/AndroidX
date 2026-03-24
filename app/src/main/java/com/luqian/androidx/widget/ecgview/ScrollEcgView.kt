package com.luqian.androidx.widget.ecgview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.luqian.androidx.R
import kotlin.math.ceil

class ScrollEcgView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    fun interface OnScrollChangeListener {
        fun onScrollChanged(scrollX: Float, maxScrollX: Float)
    }

    private var scrollChangeListener: OnScrollChangeListener? = null

    fun setOnScrollChangeListener(listener: OnScrollChangeListener?) {
        this.scrollChangeListener = listener
    }

    companion object {
        private const val RECT_HEIGHT = 80
        private const val DATA_NUM_PER_GRID = 18
        private const val GAP_GRID = 30.0f
        private val DASH_PATTERN = floatArrayOf(2f, 6f)
        private val DASH_PATTERN_MAJOR = floatArrayOf(6f, 4f)
    }

    private var gapGrid = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var xori = 0
    private var gridHori = 0
    private var gridVer = 0
    private var gapX = 0f
    private var yCenter = 0f
    private var dataSource: List<Int>? = null

    private var xChanged = 0f
    private var startX = 0f
    private var dataNum = 0
    private var offsetXMax = 0f

    private var rectWidth = 0f
    private var rectGapX = 0f
    private var multipleForRectWidth = 0f

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.cyan_200)
        strokeWidth = 1.0f
    }

    private val ecgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.ecg_green)
        strokeWidth = 2.5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.ecg_green_glow)
        strokeWidth = 8.0f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.cyan_700)
        strokeWidth = 1.0f
    }

    private val gridPath = Path()
    private val ecgPath = Path()
    private val rectPath = Path()

    private val dashPathEffect: PathEffect = DashPathEffect(DASH_PATTERN, 1f)
    private val dashPathEffectMajor: PathEffect = DashPathEffect(DASH_PATTERN_MAJOR, 1f)

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.black))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            xori = 0
            gapGrid = GAP_GRID
            viewWidth = width
            viewHeight = height
            gridHori = viewHeight / gapGrid.toInt()
            gridVer = viewWidth / gapGrid.toInt()
            yCenter = (viewHeight - RECT_HEIGHT) / 2f
            gapX = gapGrid / DATA_NUM_PER_GRID
            dataNum = dataSource?.size ?: 0
            xChanged = 0.0f
            offsetXMax = viewWidth - gapX * dataNum
            rectGapX = viewWidth.toFloat() / dataNum
            rectWidth = viewWidth.toFloat() * viewWidth / (gapX * dataNum)
            multipleForRectWidth = viewWidth.toFloat() / rectWidth
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawECGWave(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val offsetY = (viewHeight - gridHori * gapGrid) / 2
        for (i in 1 until gridHori + 2) {
            val y = gapGrid * (i - 1) + offsetY
            val isMajor = i % 5 == 0
            gridPath.reset()
            gridPath.moveTo(xori.toFloat(), y)
            gridPath.lineTo(viewWidth.toFloat(), y)
            gridPaint.pathEffect = if (isMajor) dashPathEffectMajor else dashPathEffect
            gridPaint.strokeWidth = if (isMajor) 1.5f else 1.0f
            canvas.drawPath(gridPath, gridPaint)
        }

        val offsetX = (viewWidth - gridVer * gapGrid) / 2
        for (i in 1 until gridVer + 2) {
            val x = gapGrid * (i - 1) + offsetX
            val isMajor = i % 5 == 0
            gridPath.reset()
            gridPath.moveTo(x, 0f)
            gridPath.lineTo(x, viewHeight.toFloat())
            gridPaint.pathEffect = if (isMajor) dashPathEffectMajor else dashPathEffect
            gridPaint.strokeWidth = if (isMajor) 1.5f else 1.0f
            canvas.drawPath(gridPath, gridPaint)
        }
    }

    private fun drawECGWave(canvas: Canvas) {
        val data = dataSource ?: return
        if (data.isEmpty()) return

        ecgPath.reset()

        when {
            xChanged > xori -> xChanged = xori.toFloat()
            xChanged < offsetXMax -> xChanged = offsetXMax
        }

        var iXor = 1
        for (i in 1 until data.size) {
            val nnn = xori + gapX * i + xChanged
            if (nnn >= 0) {
                iXor = i
                ecgPath.moveTo(nnn, getYCoordinate(data[i]))
                break
            }
        }

        for (i in iXor until data.size) {
            val nnn = xori + gapX * i + xChanged
            if (nnn < viewWidth + gapX) {
                ecgPath.lineTo(xori + gapX * i + xChanged, getYCoordinate(data[i]))
            }
        }

        canvas.drawPath(ecgPath, glowPaint)
        canvas.drawPath(ecgPath, ecgPaint)

        rectPath.reset()
        val rectXori = (0f - xChanged) / multipleForRectWidth
        rectPath.moveTo(rectXori, viewHeight - RECT_HEIGHT - 20f)
        rectPath.lineTo(rectXori + rectWidth, viewHeight - RECT_HEIGHT - 20f)
        rectPath.lineTo(rectXori + rectWidth, viewHeight.toFloat())
        rectPath.lineTo(rectXori, viewHeight.toFloat())
        canvas.drawPath(rectPath, rectPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x
                xChanged += currentX - startX
                startX = currentX
                invalidate()
                notifyScrollChanged()
            }
        }
        return true
    }

    private fun notifyScrollChanged() {
        scrollChangeListener?.onScrollChanged(-xChanged, -offsetXMax)
    }

    private fun getYCoordinate(data: Int): Float {
        val yInt = (data - 2048) * (-1)
        return yInt * 3 / 4f + yCenter
    }

    fun setData(data: ArrayList<String>?) {
        dataSource = data?.map { s ->
            s.toIntOrNull() ?: 2048
        }
        invalidate()
        post { notifyScrollChanged() }
    }

    fun setIntegerData(data: ArrayList<Int>?) {
        dataSource = data?.toList()
        invalidate()
        post { notifyScrollChanged() }
    }
}
