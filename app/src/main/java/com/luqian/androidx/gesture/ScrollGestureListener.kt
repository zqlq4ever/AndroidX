package com.luqian.androidx.gesture

import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs

class ScrollGestureListener internal constructor(private val targetView: View, private val viewGroup: ViewGroup)
    : GestureDetector.SimpleOnGestureListener() {

    private var scale = 1f
    private var distanceXTemp = 0f
    private var distanceYTemp = 0f
    private var viewWidthReal = 0f
    private var viewHeightReal = 0f
    private var viewWidthRealTemp = 0f
    private var viewHeightRealTemp = 0f
    private var isCalculate = false
    private var viewWidthNormal = 0
    private var viewHeightNormal = 0
    private var groupWidth = 0
    private var groupHeight = 0
    private var maxTranslationLeft = 0f
    private var maxTranslationTop = 0f
    private var maxTranslationRight = 0f
    private var maxTranslationBottom = 0f
    var isFullGroup = false


    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        var disX = distanceX
        var disY = distanceY
        disX = -disX
        disY = -disY
        if (isFullGroup || scale > 1) {
            if (viewWidthReal > groupWidth) {
                translationXOnScrollEvent(disX)
            }
            if (viewHeightReal > groupHeight) {
                translationYOnScrollEvent(disY)
            }
        } else {
            translationXOnScrollEvent(disX)
            translationYOnScrollEvent(disY)
        }
        return super.onScroll(e1, e2, disX, disY)
    }


    private fun translationXOnScrollEvent(distanceX: Float) {
        //  最大移动距离全部为正数,所以需要通过判断 distanceX 的正负,来判断是向左移动还是向右移动,
        //  然后通过取 distanceX 的绝对值,和相应移动方向的最大移动距离比较
        if (distanceX < 0 && abs(distanceXTemp + distanceX) < maxTranslationLeft
                || distanceX > 0 && distanceXTemp + distanceX < maxTranslationRight) {
            distanceXTemp += distanceX
            targetView.translationX = distanceXTemp
            //  如果超出边界,就移动到最大距离,防止边界有剩余量
        } else if (distanceX < 0 && abs(distanceXTemp + distanceX) > maxTranslationLeft) {
            distanceXTemp = -maxTranslationLeft
            targetView.translationX = -maxTranslationLeft
        } else if (distanceX > 0 && distanceXTemp + distanceX > maxTranslationRight) {
            distanceXTemp = maxTranslationRight
            targetView.translationX = maxTranslationRight
        }
    }


    private fun translationYOnScrollEvent(distanceY: Float) {
        if (distanceY < 0 && abs(distanceYTemp + distanceY) < maxTranslationTop
                || distanceY > 0 && distanceYTemp + distanceY < maxTranslationBottom) {
            distanceYTemp += distanceY
            targetView.translationY = distanceYTemp
            //  如果超出边界,就移动到最大距离,防止边界有剩余量
        } else if (distanceY < 0 && abs(distanceYTemp + distanceY) > maxTranslationTop) {
            distanceYTemp = -maxTranslationTop
            targetView.translationY = -maxTranslationTop
        } else if (distanceY > 0 && distanceYTemp + distanceY > maxTranslationBottom) {
            distanceYTemp = maxTranslationBottom
            targetView.translationY = maxTranslationBottom
        }
    }


    override fun onDown(event: MotionEvent): Boolean {
        //  计算能移动的最大距离
        if (!isCalculate) {
            isCalculate = true
            maxTranslationLeft = targetView.left.toFloat()
            maxTranslationTop = targetView.top.toFloat()
            maxTranslationRight = (viewGroup.width - targetView.right).toFloat()
            maxTranslationBottom = (viewGroup.height - targetView.bottom).toFloat()
            viewWidthNormal = targetView.width
            viewHeightNormal = targetView.height
            viewWidthRealTemp = viewWidthNormal.toFloat()
            viewHeightRealTemp = viewHeightNormal.toFloat()
            viewWidthReal = viewWidthNormal.toFloat()
            viewHeightReal = viewHeightNormal.toFloat()
            groupWidth = viewGroup.width
            groupHeight = viewGroup.height
        }
        return true
    }


    fun setScale(scale: Float) {
        viewWidthReal = viewWidthNormal * scale
        viewHeightReal = viewHeightNormal * scale

        //  如果 view 比 group 小
        if (viewWidthReal < groupWidth) {

            if (isFullGroup) {
                distanceXTemp = 0f
                targetView.translationX = 0f
            }

            maxTranslationLeft = targetView.left - (viewWidthReal - viewWidthNormal) / 2
            maxTranslationRight = viewGroup.width - targetView.right - (viewWidthReal - viewWidthNormal) / 2

            //  如果移动距离超过最大可移动距离
            if (scale > this.scale && distanceXTemp < 0 && -distanceXTemp > maxTranslationLeft) {
                val translate = (viewWidthReal - viewWidthRealTemp) / 2
                targetView.translationX = targetView.translationX + translate
                distanceXTemp += translate
            } else if (scale > this.scale && distanceXTemp > 0 && distanceXTemp > maxTranslationRight) {
                val translate = (viewWidthReal - viewWidthRealTemp) / 2
                targetView.translationX = targetView.translationX - translate
                distanceXTemp -= translate
            }
        } else {
            maxTranslationLeft = (viewWidthReal - viewWidthNormal) / 2 - (viewGroup.width - targetView.right)
            maxTranslationRight = (viewWidthReal - viewWidthNormal) / 2 - targetView.left

            if (scale < this.scale && distanceXTemp < 0 && -distanceXTemp > maxTranslationLeft) {
                val translate = (viewWidthRealTemp - viewWidthReal) / 2
                targetView.translationX = targetView.translationX + translate
                distanceXTemp += translate
            } else if (scale < this.scale && distanceXTemp > 0 && distanceXTemp > maxTranslationRight) {
                val translate = (viewWidthRealTemp - viewWidthReal) / 2
                targetView.translationX = targetView.translationX - translate
                distanceXTemp -= translate
            }
        }

        if (viewHeightReal < groupHeight) {
            maxTranslationTop = targetView.top - (viewHeightReal - viewHeightNormal) / 2
            maxTranslationBottom = viewGroup.height - targetView.bottom - (viewHeightReal - viewHeightNormal) / 2

            if (isFullGroup) {
                distanceYTemp = 0f
                targetView.translationY = 0f
            }

            //如果移动距离超过最大可移动距离
            if (scale > this.scale && distanceYTemp < 0 && -distanceYTemp > maxTranslationTop) {
                val translate = (viewHeightReal - viewHeightRealTemp) / 2
                targetView.translationY = targetView.translationY + translate
                distanceYTemp += translate
            } else if (scale > this.scale && distanceYTemp > 0 && distanceYTemp > maxTranslationBottom) {
                val translate = (viewHeightReal - viewHeightRealTemp) / 2
                targetView.translationY = targetView.translationY - translate
                distanceYTemp -= translate
            }

        } else {
            maxTranslationTop = (viewHeightReal - viewHeightNormal) / 2 - (viewGroup.height - targetView.bottom)
            maxTranslationBottom = (viewHeightReal - viewHeightNormal) / 2 - targetView.top

            if (scale < this.scale && distanceYTemp < 0 && -distanceYTemp > maxTranslationTop) {
                val translate = (viewHeightRealTemp - viewHeightReal) / 2
                targetView.translationY = targetView.translationY + translate
                distanceYTemp += translate
            } else if (scale < this.scale && distanceYTemp > 0 && distanceYTemp > maxTranslationBottom) {
                val translate = (viewHeightRealTemp - viewHeightReal) / 2
                targetView.translationY = targetView.translationY - translate
                distanceYTemp -= translate
            }
        }

        viewWidthRealTemp = viewWidthReal
        viewHeightRealTemp = viewHeightReal
        this.scale = scale
    }


    override fun onSingleTapUp(event: MotionEvent): Boolean {
        val left: Float = if (viewWidthReal > groupWidth) 0F else targetView.left - (viewWidthReal - viewWidthNormal) / 2
        val top: Float = if (viewHeightReal > groupHeight) 0F else targetView.top - (viewHeightReal - viewHeightNormal) / 2

        val right = if (viewWidthReal > groupWidth)
            groupWidth.toFloat()
        else
            viewGroup.width - (viewGroup.width - targetView.right - (viewWidthReal - viewWidthNormal) / 2)

        val bottom = if (viewHeightReal > groupHeight)
            groupHeight.toFloat()
        else
            viewGroup.height - (viewGroup.height - targetView.bottom - (viewHeightReal - viewHeightNormal) / 2)

        val rectF = RectF(left, top, right, bottom)
        if (rectF.contains(event.x, event.y)) {
            targetView.performClick()
        }

        return super.onSingleTapUp(event)
    }
}