package com.example.studita.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.ThemeUtils.getDefaultTheme
import com.example.studita.utils.UserUtils


@SuppressLint("Registered")
open class DefaultActivity : AppCompatActivity(),
    MainMenuThemeDialogAlertFragment.OnThemeChangeListener {

    private var themeState = ThemeUtils.Theme.DARK

    private val themes = mutableListOf(R.style.DarkTheme, R.style.LightTheme)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        App.authenticationState.observe(this, { pair ->
            when (pair.first) {
                CheckTokenIsCorrectStatus.Incorrect -> {
                    UserUtils.deviceSignOut()
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
        themeState = if(ThemeUtils.phoneThemeChanged(resources)) ThemeUtils.Theme.DEFAULT else ThemeUtils.Theme.values()[PrefsUtils.getTheme().ordinal]

        if (themeState == ThemeUtils.Theme.DEFAULT)
            themeState = getDefaultTheme(resources)
        setTheme(themes[themeState.ordinal])
        PrefsUtils.setTheme(themeState, ThemeUtils.nightModeApiAbove28Enabled(resources))
        window.setBackgroundDrawable(resources.getDrawable(R.drawable.page_background, theme))
    }

    override fun onThemeChanged(theme: ThemeUtils.Theme) {
        if (themeState != theme) {
            themeState = theme
            PrefsUtils.setTheme(themeState, PrefsUtils.nightModeWhenSaveWasEnabled())
            finish()
        }
    }

}