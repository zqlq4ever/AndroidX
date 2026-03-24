package com.luqian.androidx.widget.ecgview

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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

    // 使用 EcgGridHelper 统一处理网格绘制
    private val gridHelper = EcgGridHelper()

    // 复用 RectF 对象绘制高亮区域
    private val highlightRect = RectF()

    companion object {
        // 最大绘制数据点数，防止数据量过大导致卡顿
        private const val MAX_DRAW_POINTS = 5000
    }

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

            // 尺寸变化时清除网格缓存
            gridHelper.clearCache()
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

        // 使用 RectF 替代 Path，减少对象创建和绘制开销
        highlightRect.set(
            highlightLeft,
            0f,
            highlightLeft + highlightWidth,
            viewHeight.toFloat()
        )

        canvas.drawRect(highlightRect, highlightPaint)
        canvas.drawRect(highlightRect, highlightStrokePaint)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat(), bgPaint)
    }

    private fun drawGrid(canvas: Canvas) {
        val (minorPath, majorPath) = gridHelper.getGridPaths(viewWidth, viewHeight)

        // 绘制次网格线
        gridHelper.configureMinorGridPaint(gridPaint, ContextCompat.getColor(context, R.color.grid_minor))
        canvas.drawPath(minorPath, gridPaint)

        // 绘制主网格线
        gridHelper.configureMajorGridPaint(gridPaint, ContextCompat.getColor(context, R.color.grid_major))
        canvas.drawPath(majorPath, gridPaint)
    }

    private fun drawAllData(canvas: Canvas) {
        val data = dataSource ?: return
        if (data.isEmpty() || dataNum == 0) return

        smoothPath.reset()

        // 大数据量时进行采样绘制，避免卡顿
        val step = if (dataNum > MAX_DRAW_POINTS) {
            (dataNum / MAX_DRAW_POINTS).coerceAtLeast(2)
        } else {
            1
        }

        smoothPath.moveTo(0f, getRectYCoordinate(data[0]))

        var i = step
        while (i < data.size) {
            val x = rectGapX * i
            val y = getRectYCoordinate(data[i])
            smoothPath.lineTo(x, y)
            i += step
        }

        // 确保最后一个点被绘制
        if ((i - step) < data.size - 1) {
            val lastIndex = data.size - 1
            smoothPath.lineTo(rectGapX * lastIndex, getRectYCoordinate(data[lastIndex]))
        }

        canvas.drawPath(smoothPath, glowPaint)
        canvas.drawPath(smoothPath, ecgPaint)
    }

    private fun getRectYCoordinate(data: Int): Float {
        val yInt = (data - 2048) * (-1)
        return yInt / 8f + rectYCenter
    }

    // 缓存转换后的数据，避免重复转换
    private var cachedStringData: ArrayList<String>? = null
    private var cachedIntData: ArrayList<Int>? = null

    fun setData(data: ArrayList<String>?) {
        // 避免重复转换相同数据
        if (data === cachedStringData) return
        cachedStringData = data
        cachedIntData = null

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
        // 避免重复设置相同数据
        if (data === cachedIntData) return
        cachedIntData = data
        cachedStringData = null

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
