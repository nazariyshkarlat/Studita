package com.example.studita.utils

import android.content.res.Resources
import java.math.BigDecimal

fun Int.pxToDp() : Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.dpToPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.pxToSp() : Int = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()
fun Int.spToPx() : Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

fun Float.round(decimalPlace: Int): Float {
    var bd = BigDecimal(this.toString())
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
    return bd.toFloat()
}
