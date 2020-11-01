package com.example.studita.presentation.fragments.main

import android.app.Activity.RESULT_CANCELED
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
import com.example.studita.App
import com.example.studita.R
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.activities.MainActivity.Companion.startMainActivityNewTask
import com.example.studita.presentation.fragments.AuthorizationFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.ProgressDialogAlertFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.view_model.MainMenuFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.chapter_layout.*
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
                    ProgressDialogAlertFragment().show(fragmentManager!!, "PROGRESS_FRAGMENT")
                }else{
                    (fragmentManager?.findFragmentByTag("PROGRESS_FRAGMENT") as? DialogFragment)?.dismiss()
                }
            })

            viewModel.googleSignInState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    if (it is SignInWithGoogleStatus.Success) {
                        App.authenticationState.value = CheckTokenIsCorrectStatus.Correct to false
                        activity?.startMainActivityNewTask(extras = bundleOf("IS_AFTER_SIGN_UP" to it.result.isAfterSignUp))
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

            App.authenticate(UserUtils.getUserIDTokenData(), true)
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
            }else if(resultCode == RESULT_CANCELED){
                CustomSnackbar(context!!).show(
                    resources.getString(R.string.sign_in_with_google_error_text),
                    ThemeUtils.getRedColor(context!!),
                    contentView = view!!.parent as ViewGroup,
                    duration = resources.getInteger(R.integer.error_snackbar_duration).toLong()
                )
            }
        }
    }

}