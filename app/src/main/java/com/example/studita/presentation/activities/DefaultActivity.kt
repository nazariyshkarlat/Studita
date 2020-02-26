package com.example.studita.presentation.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import androidx.annotation.ColorInt
import android.util.TypedValue
import com.example.studita.presentation.extensions.startActivity
import com.example.studita.presentation.fragments.HomeFragment


open class DefaultActivity : AppCompatActivity(), HomeFragment.OnThemeChangeListener {
    var themeIndex = 0
    var oldAndroidTheme = 1
    var themeChanged = false
    var currentAndroidTheme = 0
    val themes = mutableListOf(R.style.DarkTheme, R.style.LightTheme)

    override fun onCreate(savedInstanceState: Bundle?) {
        themeIndex = getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("theme", -1)
        api28setTheme()
        if(themeIndex == -1)
            themeIndex = 0
        setTheme(themes[themeIndex])
        window.setBackgroundDrawable(resources.getDrawable(R.drawable.page_background, theme))
        super.onCreate(savedInstanceState)
    }

    private fun api28setTheme(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            currentAndroidTheme = if (currentNightMode == Configuration.UI_MODE_NIGHT_NO)  1 else 0
            oldAndroidTheme =
                getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("android theme", 1)
            themeChanged = currentAndroidTheme != oldAndroidTheme
            if (themeChanged) {
                getSharedPreferences("prefs", Context.MODE_PRIVATE)?.edit()?.remove("theme")
                    ?.apply()
                themeIndex = -1
            }
            if (themeIndex == -1) {
                themeIndex = if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) 1 else 0
                getSharedPreferences("prefs", Context.MODE_PRIVATE)?.edit()
                    ?.putInt("android theme", currentAndroidTheme)?.apply()
            }
        }
    }

    override fun onThemeChanged() {
        if(themeIndex < themes.size-1)
            themeIndex++
        else
            themeIndex = 0
        getSharedPreferences("prefs", Context.MODE_PRIVATE)?.edit()?.putInt("theme", themeIndex)?.apply()
        startActivity<MainActivity>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}