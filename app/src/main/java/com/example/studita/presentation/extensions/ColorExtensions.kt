package com.example.studita.presentation.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.studita.R
import kotlin.math.roundToInt

@ColorInt
fun getSecondaryColor(context: Context): Int{
    val typedValue = TypedValue()
    context.theme.resolveAttribute(R.attr.secondary_color, typedValue, true)
    return  ContextCompat.getColor(context, typedValue.resourceId)
}

@ColorInt
fun getPageBackroundColor(context: Context): Int{
    val typedValue = TypedValue()
    context.theme.resolveAttribute(R.attr.page_background_color, typedValue, true)
    return  ContextCompat.getColor(context, typedValue.resourceId)
}

fun getSelectableItemBackground(context: Context): Int{
    val typedValue = TypedValue()
    context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
    return typedValue.resourceId
}

@ColorInt
fun compositeColors(backColor: Int, topColor: Int, factor: Float): Int {
    val factorRounded = round(factor, 2)
    val alpha = (Color.alpha(topColor) * factorRounded).roundToInt()
    val red = Color.red(topColor)
    val green = Color.green(topColor)
    val blue = Color.blue(topColor)
    return ColorUtils.compositeColors(Color.argb(alpha, red, green, blue), backColor)
}