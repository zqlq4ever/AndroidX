package com.luqian.androidx.widget.ecgview

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect

/**
 * ECG 网格绘制辅助类
 * 提供网格 Path 缓存和复用功能，优化绘制性能
 */
class EcgGridHelper {

    companion object {
        // 默认网格大小
        const val DEFAULT_GRID_SIZE = 40f

        // 主网格线间隔（每5格）
        const val MAJOR_GRID_INTERVAL = 5
    }

    // 次网格线 Path 缓存
    private var minorGridPath: Path? = null

    // 主网格线 Path 缓存
    private var majorGridPath: Path? = null

    // 缓存的尺寸
    private var cachedWidth = 0
    private var cachedHeight = 0
    private var cachedGridSize = DEFAULT_GRID_SIZE

    // 虚线效果
    private val minorDashEffect: PathEffect = DashPathEffect(floatArrayOf(4f, 8f), 0f)
    private val majorDashEffect: PathEffect = DashPathEffect(floatArrayOf(8f, 4f), 0f)

    /**
     * 获取网格绘制路径
     * @param width 视图宽度
     * @param height 视图高度
     * @param gridSize 网格大小
     * @return Pair<minorPath, majorPath>
     */
    fun getGridPaths(width: Int, height: Int, gridSize: Float = DEFAULT_GRID_SIZE): Pair<Path, Path> {
        // 检查是否需要重建缓存
        if (minorGridPath == null || majorGridPath == null ||
            width != cachedWidth || height != cachedHeight || gridSize != cachedGridSize
        ) {
            buildGridPaths(width, height, gridSize)
            cachedWidth = width
            cachedHeight = height
            cachedGridSize = gridSize
        }
        return Pair(minorGridPath!!, majorGridPath!!)
    }

    /**
     * 清除缓存，强制下次重建
     */
    fun clearCache() {
        minorGridPath = null
        majorGridPath = null
        cachedWidth = 0
        cachedHeight = 0
    }

    /**
     * 配置 Paint 绘制次网格线
     */
    fun configureMinorGridPaint(paint: Paint, color: Int) {
        paint.pathEffect = minorDashEffect
        paint.strokeWidth = 1.0f
        paint.color = color
    }

    /**
     * 配置 Paint 绘制主网格线
     */
    fun configureMajorGridPaint(paint: Paint, color: Int) {
        paint.pathEffect = majorDashEffect
        paint.strokeWidth = 1.5f
        paint.color = color
    }

    private fun buildGridPaths(width: Int, height: Int, gridSize: Float) {
        val minorPath = Path()
        val majorPath = Path()

        val cols = (kotlin.math.ceil(width / gridSize.toDouble()).toInt() + 1)
        val rows = (kotlin.math.ceil(height / gridSize.toDouble()).toInt() + 1)

        // 垂直线
        for (i in 0 until cols) {
            val x = i * gridSize
            val isMajor = i % MAJOR_GRID_INTERVAL == 0
            val targetPath = if (isMajor) majorPath else minorPath
            targetPath.moveTo(x, 0f)
            targetPath.lineTo(x, height.toFloat())
        }

        // 水平线
        for (i in 0 until rows) {
            val y = i * gridSize
            val isMajor = i % MAJOR_GRID_INTERVAL == 0
            val targetPath = if (isMajor) majorPath else minorPath
            targetPath.moveTo(0f, y)
            targetPath.lineTo(width.toFloat(), y)
        }

        minorGridPath = minorPath
        majorGridPath = majorPath
    }
}
