package com.studita.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.*

object LanguageUtils {

    fun getResourcesRussianLocale(context: Context): Resources {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale("ru"))
        return context.createConfigurationContext(configuration).resources
    }
}