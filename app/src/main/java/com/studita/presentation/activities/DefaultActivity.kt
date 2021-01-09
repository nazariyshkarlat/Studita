package com.studita.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.authenticationState
import com.studita.App.Companion.recreateAppEvent
import com.studita.R
import com.studita.authenticator.AccountAuthenticator
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.presentation.activities.MainActivity.Companion.startMainActivityClearTop
import com.studita.presentation.fragments.main.MainMenuFragment
import com.studita.presentation.view_model.MainFragmentViewModel
import com.studita.utils.PrefsUtils
import com.studita.utils.ThemeUtils
import com.studita.utils.ThemeUtils.getDefaultTheme
import com.studita.utils.UserUtils
import org.koin.android.ext.android.inject


@SuppressLint("Registered")
open class DefaultActivity : AppCompatActivity(),
    MainMenuFragment.OnThemeChangeListener {

    private val myViewModel : MainFragmentViewModel by inject()

    private var themeState = ThemeUtils.Theme.DARK

    private val themes = mutableListOf(R.style.DarkTheme, R.style.LightTheme)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        authenticationState.observe(this, { pair ->
            when (pair.first) {
                CheckTokenIsCorrectStatus.Incorrect -> {
                    AccountAuthenticator.removeAccount(this)
                    UserUtils.deviceSignOut()
                    authenticate(UserUtils.getUserIDTokenData(), false)
                    startLogInActivityStack()
                }
            }
        })
        recreateAppEvent.observe(this, {
            this.startMainActivityClearTop()
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