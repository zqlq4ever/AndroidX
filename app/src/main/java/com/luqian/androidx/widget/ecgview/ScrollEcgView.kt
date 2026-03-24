package com.luqian.androidx.widget.ecgview

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.luqian.androidx.R

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
        // 使用 BlurMaskFilter 实现发光效果，避免双层绘制
        maskFilter = BlurMaskFilter(6f, BlurMaskFilter.Blur.NORMAL)
    }

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.cyan_700)
        strokeWidth = 1.0f
    }

    private val ecgPath = Path()

    // 使用 EcgGridHelper 统一处理网格绘制
    private val gridHelper = EcgGridHelper()

    // 复用 RectF 对象，避免每次绘制创建
    private val rectF = RectF()

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

            // 尺寸变化时清除网格缓存
            gridHelper.clearCache()
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawECGWave(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val (minorPath, majorPath) = gridHelper.getGridPaths(viewWidth, viewHeight, gapGrid)

        // 绘制次网格线
        gridHelper.configureMinorGridPaint(gridPaint, ContextCompat.getColor(context, R.color.cyan_200))
        canvas.drawPath(minorPath, gridPaint)

        // 绘制主网格线
        gridHelper.configureMajorGridPaint(gridPaint, ContextCompat.getColor(context, R.color.cyan_200))
        canvas.drawPath(majorPath, gridPaint)
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

        // 单层绘制实现发光效果（通过 BlurMaskFilter）
        canvas.drawPath(ecgPath, ecgPaint)

        // 使用 RectF 替代 Path 绘制底部指示器，性能更好
        val rectXori = (0f - xChanged) / multipleForRectWidth
        rectF.set(
            rectXori,
            viewHeight - RECT_HEIGHT - 20f,
            rectXori + rectWidth,
            viewHeight.toFloat()
        )
        canvas.drawRect(rectF, rectPaint)
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
        // 数据变化时重新计算相关参数
        if (viewWidth > 0 && dataNum > 0) {
            offsetXMax = viewWidth - gapX * dataNum
            rectGapX = viewWidth.toFloat() / dataNum
            rectWidth = viewWidth.toFloat() * viewWidth / (gapX * dataNum)
            multipleForRectWidth = viewWidth.toFloat() / rectWidth
        }
        invalidate()
        post { notifyScrollChanged() }
    }

    fun setIntegerData(data: ArrayList<Int>?) {
        // 避免重复设置相同数据
        if (data === cachedIntData) return
        cachedIntData = data
        cachedStringData = null

        dataSource = data?.toList()
        dataNum = dataSource?.size ?: 0
        // 数据变化时重新计算相关参数
        if (viewWidth > 0 && dataNum > 0) {
            offsetXMax = viewWidth - gapX * dataNum
            rectGapX = viewWidth.toFloat() / dataNum
            rectWidth = viewWidth.toFloat() * viewWidth / (gapX * dataNum)
            multipleForRectWidth = viewWidth.toFloat() / rectWidth
        }
        invalidate()
        post { notifyScrollChanged() }
    }
}
