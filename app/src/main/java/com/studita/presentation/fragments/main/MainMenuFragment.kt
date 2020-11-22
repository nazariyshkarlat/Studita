package com.studita.presentation.fragments.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.SignInWithGoogleStatus
import com.studita.presentation.activities.MainActivity.Companion.startMainActivityClearTop
import com.studita.presentation.fragments.AuthorizationFragment
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.ProgressDialogAlertFragment
import com.studita.presentation.view_model.MainMenuFragmentViewModel
import com.studita.presentation.view_model.ToolbarFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.authenticationState
import kotlinx.android.synthetic.main.main_menu_layout.*
import kotlinx.android.synthetic.main.settings_offline_mode_item.*


class MainMenuFragment : NavigatableFragment(R.layout.main_menu_layout) {

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
                            startActivityForResult(
                                mainMenuFragmentViewModel?.signIn(it)?.signInIntent,
                                RC_SIGN_IN
                            )
                        }
                    } else {
                        (activity as AppCompatActivity).navigateTo(
                            AuthorizationFragment(),
                            R.id.doubleFrameLayoutFrameLayout
                        )
                    }
                })

            viewModel.progressState.observe(viewLifecycleOwner, Observer {
                if(it){
                    if(fragmentManager?.findFragmentByTag("PROGRESS_FRAGMENT") == null)
                        ProgressDialogAlertFragment().show(fragmentManager!!, "PROGRESS_FRAGMENT")
                }else{
                    (fragmentManager?.findFragmentByTag("PROGRESS_FRAGMENT") as? DialogFragment)?.dismiss()
                }
            })

            viewModel.googleSignInState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    if (it is SignInWithGoogleStatus.Success) {
                        authenticationState.value = CheckTokenIsCorrectStatus.Correct to false
                        activity?.startMainActivityClearTop(extras = bundleOf("IS_AFTER_SIGN_UP" to it.result.isAfterSignUp))
                    }else{
                        CustomSnackbar(context!!).show(
                            resources.getString(R.string.sign_in_with_google_error_text),
                            ThemeUtils.getRedColor(context!!),
                            contentView = view.parent as ViewGroup,
                            duration = resources.getInteger(R.integer.error_snackbar_duration).toLong()
                        )
                    }
                })
            mainMenuWithGoogleButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
            mainMenuUseEmailButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
        }


        mainMenuLayoutOfflineSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()

        mainMenuLayoutOfflineSwitchView.setOnClickListener {
            PrefsUtils.setOfflineMode(!mainMenuLayoutOfflineSwitch.isChecked)
            mainMenuLayoutOfflineSwitch.isChecked = !mainMenuLayoutOfflineSwitch.isChecked

            authenticate(UserUtils.getUserIDTokenData(), true)
        }

        mainMenuLayoutThemeView.setWithMinClickInterval(true)
        mainMenuLayoutThemeView.setOnClickListener {
            MainMenuThemeDialogAlertFragment().show(
                (activity as AppCompatActivity).supportFragmentManager,
                null
            )
        }

        mainMenuLayoutLanguageView.setWithMinClickInterval(true)
        mainMenuLayoutLanguageView.setOnClickListener {
            MainMenuLanguageDialogAlertFragment()
                .show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        scrollingView = mainMenuScrollView
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mainMenuLayoutOfflineSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                activity?.application?.let {
                    mainMenuFragmentViewModel?.handleSignInResult(
                        task,
                        it
                    )
                }
            }
        }
    }

}