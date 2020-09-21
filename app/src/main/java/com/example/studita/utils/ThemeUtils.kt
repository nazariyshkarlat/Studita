package com.example.studita.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.utils.PrefsUtils.getTheme

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
    fun getRedColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.red_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getGreenColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.green_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getSwipeRefreshBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.swipe_refresh_layout_background_color,
            typedValue,
            true
        )
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    fun getPressAlpha(context: Context): Float {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.press_view_press_alpha, typedValue, true)
        return typedValue.float
    }

    fun getDefaultTheme(resources: Resources): Theme {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val currentNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) Theme.LIGHT else Theme.DARK
        } else
            Theme.DARK
    }

    fun Context.getCurrentTheme(): Theme{
        val prefsTheme = PrefsUtils.getTheme()
        return if(prefsTheme != Theme.DEFAULT)
            prefsTheme
        else
            getDefaultTheme(this.resources)
    }

    enum class Theme {
        DARK,
        LIGHT,
        DEFAULT
    }
}