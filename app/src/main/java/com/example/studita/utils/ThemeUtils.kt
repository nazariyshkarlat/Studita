package com.example.studita.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.studita.R
import kotlin.math.roundToInt

object ThemeUtils {
    @ColorInt
    fun getSecondaryColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.secondary_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getPrimaryColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.primary_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getAccentColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getPageBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.page_background_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getSwipeRefreshBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.swipe_refresh_layout_background_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorRes
    fun getSelectableItemBackground(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
        return typedValue.resourceId
    }

    fun getPressAlpha(context: Context): Float {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.press_view_press_alpha, typedValue, true)
        return typedValue.float
    }
}