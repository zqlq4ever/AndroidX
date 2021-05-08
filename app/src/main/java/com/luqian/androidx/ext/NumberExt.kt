package com.luqian.androidx.ext

import com.luqian.androidx.ui.density
import com.luqian.androidx.ui.scaledDensity


/**
 * dp、sp、px相互换算
 */
internal fun Number?.dpToPx() = (this?.toFloat() ?: 0f) * density
internal fun Number?.spToPx() = (this?.toFloat() ?: 0f) * scaledDensity
internal fun Number?.pxToDp() = (this?.toFloat() ?: 0f) / density
internal fun Number?.pxToSp() = (this?.toFloat() ?: 0f) / scaledDensity