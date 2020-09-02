package com.example.studita.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

object ColorUtils {

    @ColorInt
    fun compositeColors(backColor: Int, topColor: Int, factor: Float): Int {
        val factorRounded = factor.round(2)
        val alpha = (Color.alpha(topColor) * factorRounded).roundToInt()
        val red = Color.red(topColor)
        val green = Color.green(topColor)
        val blue = Color.blue(topColor)
        return ColorUtils.compositeColors(Color.argb(alpha, red, green, blue), backColor)
    }

}