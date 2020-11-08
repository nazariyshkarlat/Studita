package com.studita.utils

import android.content.res.Resources
import java.math.BigDecimal

fun Int.pxToDp(): Float = (this / Resources.getSystem().displayMetrics.density)
fun Float.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.pxToSp(): Float = (this / Resources.getSystem().displayMetrics.scaledDensity)
fun Float.spToPx(): Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

fun Float.round(decimalPlace: Int): Float {
    var bd = BigDecimal(this.toString())
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
    return bd.toFloat()
}
