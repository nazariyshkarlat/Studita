package com.example.studita.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuLanguageDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.MainMenuThemeDialogAlertFragment
import com.example.studita.utils.PrefsUtils
import com.example.studita.presentation.view_model.MainMenuFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.main_menu_layout.*


class MainMenuFragment : BaseFragment(R.layout.main_menu_layout), ViewTreeObserver.OnScrollChangedListener {

    private val RC_SIGN_IN: Int = 0
    private var mainMenuFragmentViewModel: MainMenuFragmentViewModel? = null
    private var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null

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
                    if(signIn)
                        MainActivity.needsRecreate = true
                        activity?.finish()
                })
            mainMenuWithGoogleButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
            mainMenuUseEmailButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
        }

        mainMenuScrollView.viewTreeObserver.addOnScrollChangedListener(this)

        mainMenuLayoutOfflineSwitch.isChecked = PrefsUtils.isOfflineMode()

        mainMenuLayoutOfflineSwitchView.setOnClickListener {
            PrefsUtils.setOfflineMode(!mainMenuLayoutOfflineSwitch.isChecked)
            mainMenuLayoutOfflineSwitch.isChecked = !mainMenuLayoutOfflineSwitch.isChecked
        }

        mainMenuLayoutThemeView.setOnClickListener {
            val dialogFragment =
                MainMenuThemeDialogAlertFragment()
            dialogFragment.show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        mainMenuLayoutLanguageView.setOnClickListener {
            MainMenuLanguageDialogAlertFragment()
                .show((activity as AppCompatActivity).supportFragmentManager, null)
        }

        if (!isHidden)
            checkScrollable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == RC_SIGN_IN) and (resultCode == RESULT_OK)) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            context?.let { mainMenuFragmentViewModel?.handleSignInResult(task, it) }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollable()
            checkScrollY()
        } else {
            toolbarFragmentViewModel?.showToolbarDivider(false)
        }
    }

    private fun checkScrollable() {
        mainMenuTitle.visibility = View.VISIBLE
        OneShotPreDrawListener.add(mainMenuScrollView) {
            if (mainMenuScrollView.height < mainMenuScrollView.getChildAt(0).height
                + mainMenuScrollView.paddingTop + mainMenuScrollView.paddingBottom
            ) {
                mainMenuTitle.visibility = View.GONE
                toolbarFragmentViewModel?.setToolbarText(mainMenuTitle.text.toString())
            } else {
                toolbarFragmentViewModel?.setToolbarText(null)
            }
        }
    }

    override fun onScrollChanged() {
        checkScrollY()
    }

    private fun checkScrollY() {
        val scrollY: Int = mainMenuScrollView.scrollY
        toolbarFragmentViewModel?.showToolbarDivider(scrollY != 0)
    }

}