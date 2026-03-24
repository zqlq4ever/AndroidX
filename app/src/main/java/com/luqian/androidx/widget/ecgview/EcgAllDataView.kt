package com.luqian.androidx.widget.ecgview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.luqian.androidx.R

class EcgAllDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var viewWidth = 0
    private var viewHeight = 0
    private var dataSource: List<Int>? = null
    private var dataNum = 0
    private var rectGapX = 0f
    private var rectYCenter = 0f
    private var gapX = 1f

    private var scrollX = 0f
    private var maxScrollX = 0f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ecgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.ecg_green)
        strokeWidth = 3.0f
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

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.grid_minor)
        strokeWidth = 1.0f
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.cyan_700)
        alpha = 80
    }

    private val highlightStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.cyan_200)
        strokeWidth = 2.0f
    }

    private val smoothPath = Path()
    private val gridPath = Path()
    private val highlightPath = Path()

    private val dashEffect = DashPathEffect(floatArrayOf(4f, 8f), 0f)
    private val dashEffectMajor = DashPathEffect(floatArrayOf(8f, 4f), 0f)

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            LinearGradient(
                0f, 0f, 0f, h.toFloat(),
                ContextCompat.getColor(context, R.color.ecg_background),
                ContextCompat.getColor(context, R.color.black),
                Shader.TileMode.CLAMP
            ).also { bgPaint.shader = it }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            viewWidth = width
            viewHeight = height
            dataNum = dataSource?.size ?: 0
            if (dataNum > 0) {
                rectGapX = viewWidth.toFloat() / dataNum
            }
            rectYCenter = viewHeight.toFloat() / 2
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawGrid(canvas)
        drawAllData(canvas)
        drawHighlight(canvas)
    }

    private fun drawHighlight(canvas: Canvas) {
        if (dataNum <= 0 || maxScrollX == 0f) return

        val visibleRatio = viewWidth / (gapX * dataNum)
        val scrollRatio = scrollX / maxScrollX

        val highlightLeft = scrollRatio * viewWidth * (1 - visibleRatio)
        val highlightWidth = visibleRatio * viewWidth

        highlightPath.reset()
        highlightPath.moveTo(highlightLeft, 0f)
        highlightPath.lineTo(highlightLeft + highlightWidth, 0f)
        highlightPath.lineTo(highlightLeft + highlightWidth, viewHeight.toFloat())
        highlightPath.lineTo(highlightLeft, viewHeight.toFloat())
        highlightPath.close()

        canvas.drawPath(highlightPath, highlightPaint)
        canvas.drawPath(highlightPath, highlightStrokePaint)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat(), bgPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        val gridSize = 40f
        val gridCols = (Math.ceil(viewWidth / gridSize.toDouble()).toInt() + 1)
        val gridRows = (Math.ceil(viewHeight / gridSize.toDouble()).toInt() + 1)

        for (i in 0 until gridCols) {
            val x = i * gridSize
            val isMajor = i % 5 == 0
            gridPath.reset()
            gridPaint.pathEffect = if (isMajor) dashEffectMajor else dashEffect
            gridPaint.strokeWidth = if (isMajor) 1.5f else 1.0f
            gridPaint.color = ContextCompat.getColor(
                context,
                if (isMajor) R.color.grid_major else R.color.grid_minor
            )
            gridPath.moveTo(x, 0f)
            gridPath.lineTo(x, viewHeight.toFloat())
            canvas.drawPath(gridPath, gridPaint)
        }

        for (i in 0 until gridRows) {
            val y = i * gridSize
            val isMajor = i % 5 == 0
            gridPath.reset()
            gridPaint.pathEffect = if (isMajor) dashEffectMajor else dashEffect
            gridPaint.strokeWidth = if (isMajor) 1.5f else 1.0f
            gridPaint.color = ContextCompat.getColor(
                context,
                if (isMajor) R.color.grid_major else R.color.grid_minor
            )
            gridPath.moveTo(0f, y)
            gridPath.lineTo(viewWidth.toFloat(), y)
            canvas.drawPath(gridPath, gridPaint)
        }
    }

    private fun drawAllData(canvas: Canvas) {
        val data = dataSource ?: return
        if (data.isEmpty() || dataNum == 0) return

        smoothPath.reset()
        smoothPath.moveTo(0f, getRectYCoordinate(data[0]))

        for (i in 1 until data.size) {
            val x = rectGapX * i
            val y = getRectYCoordinate(data[i])
            smoothPath.lineTo(x, y)
        }

        canvas.drawPath(smoothPath, glowPaint)
        canvas.drawPath(smoothPath, ecgPaint)
    }

    private fun getRectYCoordinate(data: Int): Float {
        val yInt = (data - 2048) * (-1)
        return yInt / 8f + rectYCenter
    }

    fun setData(data: ArrayList<String>?) {
        dataSource = data?.map { s ->
            s.toIntOrNull() ?: 2048
        }
        dataNum = dataSource?.size ?: 0
        if (dataNum > 0 && viewWidth > 0) {
            rectGapX = viewWidth.toFloat() / dataNum
        }
        invalidate()
    }

    fun setIntegerData(data: ArrayList<Int>?) {
        dataSource = data?.toList()
        dataNum = dataSource?.size ?: 0
        if (dataNum > 0 && viewWidth > 0) {
            rectGapX = viewWidth.toFloat() / dataNum
        }
        invalidate()
    }

    fun setGapX(gapX: Float) {
        this.gapX = gapX
    }

    fun setScrollRange(scrollX: Float, maxScrollX: Float) {
        this.scrollX = scrollX
        this.maxScrollX = maxScrollX
        invalidate()
    }
}
