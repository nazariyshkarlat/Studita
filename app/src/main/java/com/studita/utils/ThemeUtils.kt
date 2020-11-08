package com.studita.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.utils.PrefsUtils.getTheme

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
    fun getStatusBarColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getNavigationBarColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.navigationBarColor, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getGreenColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.green_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    @ColorInt
    fun getNavigationColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.bottom_navigation_color, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    fun getLightStatusBar(context: Context): Boolean {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowLightStatusBar, typedValue, true)
        return typedValue.data == 1
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    fun getLightNavigation(context: Context): Boolean {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowLightNavigationBar, typedValue, true)
        return typedValue.data == 1
    }

    fun getSelectableItemBackgroundBorderless(context: Context): Drawable {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true)
        return ContextCompat.getDrawable(context, typedValue.resourceId)!!
    }

    @ColorInt
    fun getSwipeRefreshBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.gray_focused_color,
            typedValue,
            true
        )
        return ColorUtils.compositeColors(getPageBackgroundColor(context), ContextCompat.getColor(context, typedValue.resourceId), 1F)
    }

    @ColorInt
    fun getSwipeRefreshIconColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            R.attr.primary_color,
            typedValue,
            true
        )
        return ColorUtils.compositeColors(getPageBackgroundColor(context), ContextCompat.getColor(context, typedValue.resourceId), 1F)
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

    fun nightModeApiAbove28Enabled(resources: Resources) = with((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)){
        this == Configuration.UI_MODE_NIGHT_YES
    }

    fun phoneThemeChanged(resources: Resources) = if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) false
    else nightModeApiAbove28Enabled(resources) != PrefsUtils.nightModeWhenSaveWasEnabled()

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