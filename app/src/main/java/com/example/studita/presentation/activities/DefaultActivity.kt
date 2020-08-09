package com.example.studita.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils

@SuppressLint("Registered")
open class DefaultActivity : AppCompatActivity(),
    MainMenuThemeDialogAlertFragment.OnThemeChangeListener {

    private var themeState = Theme.DARK

    private val themes = mutableListOf(R.style.DarkTheme, R.style.LightTheme)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        App.authenticationState.observe(this, Observer { pair ->
            when (pair.first) {
                CheckTokenIsCorrectStatus.Incorrect -> {
                    UserUtils.deviceSignOut(this)
                    App.authenticate(UserUtils.getUserIDTokenData(), false)
                    startLogInActivityStack()
                }
            }
        })
    }

    private fun startLogInActivityStack() {
        val intent = Intent(
            this,
            MainMenuActivity::class.java
        )
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setTheme() {
        themeState = Theme.values()[PrefsUtils.getTheme().ordinal]
        if (themeState == Theme.DEFAULT)
            themeState = getDefaultTheme()
        setTheme(themes[themeState.ordinal])
        PrefsUtils.setTheme(themeState)
        window.setBackgroundDrawable(resources.getDrawable(R.drawable.page_background, theme))
    }

    private fun getDefaultTheme(): Theme {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val currentNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) Theme.LIGHT else Theme.DARK
        } else
            Theme.DARK
    }

    override fun onThemeChanged(theme: Theme) {
        if (themeState != theme) {
            themeState = theme
            PrefsUtils.setTheme(themeState)
            finish()
        }
    }

    enum class Theme {
        DARK,
        LIGHT,
        DEFAULT
    }

}