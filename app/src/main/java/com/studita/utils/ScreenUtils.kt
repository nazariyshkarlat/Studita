package com.studita.utils

import android.content.res.Resources

object ScreenUtils {

    fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

}