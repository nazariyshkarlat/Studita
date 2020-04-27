package com.example.studita.presentation.activities

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.R
import com.example.studita.di.CacheModule
import com.example.studita.presentation.utils.startActivity
import com.example.studita.presentation.fragments.MainMenuThemeDialogAlertFragment

@SuppressLint("Registered")
open class DefaultActivity : AppCompatActivity(),
    MainMenuThemeDialogAlertFragment.OnThemeChangeListener {

    companion object {
        var themeState = Theme.DARK
    }

    private val themes = mutableListOf(R.style.DarkTheme, R.style.LightTheme)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
    }

    private fun setTheme(){
        themeState = Theme.values()[CacheModule.sharedPreferences.getInt("theme", Theme.DEFAULT.ordinal)]
        if(themeState == Theme.DEFAULT)
            themeState = getDefaultTheme()
        setTheme(themes[themeState.ordinal])
        window.setBackgroundDrawable(resources.getDrawable(R.drawable.page_background, theme))
    }

    private fun getDefaultTheme(): Theme{
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO)  Theme.LIGHT else Theme.DARK
        }else
            Theme.DARK
    }

    override fun onThemeChanged(theme: Theme) {
        if(themeState != theme) {
            themeState = theme
            CacheModule.sharedPreferences.edit()?.putInt("theme", themeState.ordinal)?.apply()
            startActivity<MainMenuActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    enum class Theme{
        DARK,
        LIGHT,
        DEFAULT
    }
}