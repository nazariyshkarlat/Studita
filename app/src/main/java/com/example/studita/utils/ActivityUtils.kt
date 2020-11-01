package com.example.studita.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import java.io.IOException
import java.io.Serializable


inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
    Internals.internalStartActivity(this, T::class.java, params)

object Internals {
    fun internalStartActivity(
        ctx: Context,
        activity: Class<out Activity>,
        params: Array<out Pair<String, Any?>>
    ) {
        ctx.startActivity(createIntent(ctx, activity, params))
    }

    private fun <T> createIntent(
        ctx: Context,
        clazz: Class<out T>,
        params: Array<out Pair<String, Any?>>
    ): Intent {
        val intent = Intent(ctx, clazz)
        if (params.isNotEmpty()) fillIntentArguments(intent, params)
        return intent
    }

    private fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
        params.forEach {
            val value = it.second
            when (value) {
                null -> intent.putExtra(it.first, null as java.io.Serializable?)
                is Int -> intent.putExtra(it.first, value)
                is Long -> intent.putExtra(it.first, value)
                is CharSequence -> intent.putExtra(it.first, value)
                is String -> intent.putExtra(it.first, value)
                is Float -> intent.putExtra(it.first, value)
                is Double -> intent.putExtra(it.first, value)
                is Char -> intent.putExtra(it.first, value)
                is Short -> intent.putExtra(it.first, value)
                is Boolean -> intent.putExtra(it.first, value)
                is Serializable -> intent.putExtra(it.first, value)
                is Bundle -> intent.putExtra(it.first, value)
                is Parcelable -> intent.putExtra(it.first, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                    else -> throw IOException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(it.first, value)
                is LongArray -> intent.putExtra(it.first, value)
                is FloatArray -> intent.putExtra(it.first, value)
                is DoubleArray -> intent.putExtra(it.first, value)
                is CharArray -> intent.putExtra(it.first, value)
                is ShortArray -> intent.putExtra(it.first, value)
                is BooleanArray -> intent.putExtra(it.first, value)
                else -> throw IOException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            return@forEach
        }
    }
}

fun Activity.setStatusBarColor(@ColorInt color: Int){
    window.statusBarColor = color
}

fun Activity.setNavigationBarColor(@ColorInt color: Int){
    window.navigationBarColor = color
}

fun Activity.clearBarsColor(){
    window.statusBarColor = ThemeUtils.getStatusBarColor(this)
    window.navigationBarColor = ThemeUtils.getNavigationBarColor(this)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
        if(ThemeUtils.getLightNavigation(this)){
            window?.decorView?.setLightNavigation()
        }else
            window?.decorView?.clearLightNavigation()
    }

    if(ThemeUtils.getLightStatusBar(this))
        window?.decorView?.setLightStatusBar()
    else
        window?.decorView?.clearLightStatusBar()
}

fun Activity.makeStatusBarTransparent(){
    window?.let {
        it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or it.decorView.systemUiVisibility
        it.decorView.clearLightStatusBar()
        it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        it.statusBarColor = Color.TRANSPARENT
    }
}
