package com.studita.presentation.fragments.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.studita.R
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.SignInWithGoogleStatus
import com.studita.presentation.activities.MainActivity.Companion.startMainActivityClearTop
import com.studita.presentation.fragments.AuthorizationFragment
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.ProgressDialogAlertFragment
import com.studita.presentation.view_model.MainMenuFragmentViewModel
import com.studita.presentation.view_model.ToolbarFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.authenticationState
import com.studita.presentation.activities.MainActivity
import com.studita.presentation.view_model.MainMenuActivityViewModel
import com.studita.utils.ThemeUtils.getCurrentTheme
import kotlinx.android.synthetic.main.main_menu_layout.*
import kotlinx.android.synthetic.main.settings_switch_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainMenuFragment : NavigatableFragment(R.layout.main_menu_layout) {

    private val RC_SIGN_IN: Int = 0
    private var mainMenuFragmentViewModel: MainMenuFragmentViewModel? = null
    private var mainMenuActivityViewModel: MainMenuActivityViewModel? = null
    private var changeThemeJob : Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainMenuFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuFragmentViewModel::class.java)
        }

        mainMenuActivityViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
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
                            ContextCompat.getColor(context!!, R.color.white),
                            contentView = view.parent as ViewGroup,
                            duration = resources.getInteger(R.integer.error_snackbar_duration).toLong()
                        )
                    }
                })
            mainMenuWithGoogleButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
            mainMenuUseEmailButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
        }


        with(mainMenuLayoutListParent.getChildAt(0) as ViewGroup) {
            mainMenuLayoutSwitchItemSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()
            mainMenuLayoutSwitchItemIcon.setImageResource(R.drawable.ic_cloud_secondary)
            mainMenuLayoutSwitchItemTextView.text = resources.getString(R.string.offline_mode)

            mainMenuLayoutSwitchItemSwitchView.setOnClickListener {
                PrefsUtils.setOfflineMode(!mainMenuLayoutSwitchItemSwitch.isChecked)
                mainMenuLayoutSwitchItemSwitch.isChecked = !mainMenuLayoutSwitchItemSwitch.isChecked

                authenticate(UserUtils.getUserIDTokenData(), true)
            }
        }

        with(mainMenuLayoutListParent.getChildAt(1) as ViewGroup) {
            mainMenuLayoutSwitchItemSwitch.isChecked = activity!!.getCurrentTheme() == ThemeUtils.Theme.DARK
            mainMenuLayoutSwitchItemIcon.setImageResource(R.drawable.ic_palette_secondary)
            mainMenuLayoutSwitchItemTextView.text = resources.getString(R.string.dark_theme)

            mainMenuLayoutSwitchItemSwitchView.setOnClickListener {

                if(changeThemeJob == null ||
                    changeThemeJob?.isActive == false) {
                    mainMenuLayoutSwitchItemSwitch.isChecked =
                        !mainMenuLayoutSwitchItemSwitch.isChecked
                    changeThemeJob = lifecycleScope.launch(Dispatchers.Main) {
                        delay(200)
                        changeTheme(mainMenuLayoutSwitchItemSwitch.isChecked)
                    }
                }
            }
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
        (mainMenuLayoutListParent.getChildAt(0) as ViewGroup).mainMenuLayoutSwitchItemSwitch.isChecked = PrefsUtils.isOfflineModeEnabled()
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

    private fun changeTheme(darkModeEnabled: Boolean) {
        mainMenuActivityViewModel?.onThemeChangeListener?.onThemeChanged(ThemeUtils.Theme.values()[if(darkModeEnabled) 0 else 1])
        MainActivity.needsRefresh = true
    }

    interface OnThemeChangeListener {
        fun onThemeChanged(theme: ThemeUtils.Theme)
    }

}