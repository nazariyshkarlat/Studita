package com.example.studita.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.activities.MainActivity.Companion.startMainActivityNewTask
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.utils.PrefsUtils
import com.example.studita.presentation.view_model.MainMenuFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.example.studita.utils.UserUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.main_menu_layout.*
import kotlinx.android.synthetic.main.settings_offline_mode_item.*


class MainMenuFragment : NavigatableFragment(R.layout.main_menu_layout){

    private val RC_SIGN_IN: Int = 0
    private var mainMenuFragmentViewModel: MainMenuFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainMenuFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuFragmentViewModel::class.java)
        }

        toolbarFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ToolbarFragmentViewModel::class.java)
        }

        mainMenuFragmentViewModel?.let { viewModel ->
            viewModel.signUpMethodState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { state ->
                    if (state == MainMenuFragmentViewModel.SignUpMethod.WITH_GOOGLE) {
                        context?.let {
                            startActivityForResult(mainMenuFragmentViewModel?.signIn(it)?.signInIntent, RC_SIGN_IN)
                        }
                    } else {
                        (activity as AppCompatActivity).navigateTo(
                            AuthorizationFragment(),
                            R.id.doubleFrameLayoutFrameLayout
                        )
                    }
                })

            viewModel.googleSignInState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { signIn ->
                    if(signIn) {
                        if(activity?.isTaskRoot == false)
                            MainActivity.needsRecreate = true
                        App.authenticationState.value = CheckTokenIsCorrectStatus.Correct
                        activity?.startMainActivityNewTask()
                    }
                })
            mainMenuWithGoogleButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
            mainMenuUseEmailButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
        }


        mainMenuLayoutOfflineSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()

        mainMenuLayoutOfflineSwitchView.setOnClickListener {
            PrefsUtils.setOfflineMode(!mainMenuLayoutOfflineSwitch.isChecked)
            mainMenuLayoutOfflineSwitch.isChecked = !mainMenuLayoutOfflineSwitch.isChecked
        }

        mainMenuLayoutThemeView.setOnSingleClickListener {
            MainMenuThemeDialogAlertFragment().show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        mainMenuLayoutLanguageView.setOnSingleClickListener {
            MainMenuLanguageDialogAlertFragment()
                .show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        scrollingView = mainMenuScrollView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == RC_SIGN_IN) and (resultCode == RESULT_OK)) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            activity?.application?.let { mainMenuFragmentViewModel?.handleSignInResult(task, it) }
        }
    }

}