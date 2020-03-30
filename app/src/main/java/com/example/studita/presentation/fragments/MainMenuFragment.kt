package com.example.studita.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.navigateTo
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.MainMenuFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.main_menu_layout.*


class MainMenuFragment : BaseFragment(R.layout.main_menu_layout), ViewTreeObserver.OnScrollChangedListener {

    private val RC_SIGN_IN: Int = 0
    private var fragmentViewModel: MainMenuFragmentViewModel? = null
    private var activityViewModel: ToolbarFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuFragmentViewModel::class.java)
        }

        activityViewModel = activity?.run {
            ViewModelProviders.of(this).get(ToolbarFragmentViewModel::class.java)
        }

        fragmentViewModel?.let { viewModel ->
            viewModel.signUpMethodState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { state ->
                    if (state == MainMenuFragmentViewModel.SignUpMethod.WITH_GOOGLE) {
                        context?.let {
                            startActivityForResult(fragmentViewModel?.signIn(it)?.signInIntent, RC_SIGN_IN)
                        }
                    } else {
                        (activity as AppCompatActivity).navigateTo(
                            AuthorizationFragment(),
                            R.id.doubleConstraintFrameLayout
                        )
                    }
                })
            mainMenuWithGoogleButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
            mainMenuUseEmailButton.setOnClickListener { viewModel.onSignUpLogInClick(it.id) }
        }

        mainMenuScrollView.viewTreeObserver.addOnScrollChangedListener(this)

        if (!isHidden)
            checkScrollable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            context?.let { fragmentViewModel?.handleSignInResult(task, it) }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkScrollable()
            checkScrollY()
        } else {
            activityViewModel?.showToolbarDivider(false)
        }
    }

    private fun checkScrollable() {
        mainMenuTitle.visibility = View.VISIBLE
        OneShotPreDrawListener.add(mainMenuScrollView) {
            if (mainMenuScrollView.height < mainMenuScrollView.getChildAt(0).height
                + mainMenuScrollView.paddingTop + mainMenuScrollView.paddingBottom
            ) {
                mainMenuTitle.visibility = View.GONE
                activityViewModel?.setToolbarText(mainMenuTitle.text.toString())
            } else {
                activityViewModel?.setToolbarText(null)
            }
        }
    }

    override fun onScrollChanged() {
        checkScrollY()
    }

    private fun checkScrollY() {
        val scrollY: Int = mainMenuScrollView.scrollY
        activityViewModel?.showToolbarDivider(scrollY != 0)
    }

}