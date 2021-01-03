package com.studita.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.studita.R
import com.studita.authenticator.AccountAuthenticator
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.presentation.activities.MainActivity.Companion.startMainActivityClearTop
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.ProgressDialogAlertFragment
import com.studita.presentation.listeners.OnViewSizeChangeListener
import com.studita.presentation.view_model.AuthorizationFragmentViewModel
import com.studita.utils.setOnViewSizeChangeListener
import com.studita.App.Companion.authenticationState
import kotlinx.android.synthetic.main.authorization_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthorizationFragment : NavigatableFragment(R.layout.authorization_layout), TextWatcher,
    OnViewSizeChangeListener {

    private var authorizationFragmentViewModel: AuthorizationFragmentViewModel? = null
    private lateinit var signUpOnClick: () -> Unit
    private lateinit var onViewSizeChangeListener: View.OnLayoutChangeListener
    lateinit var contentView: ViewGroup
    private var buttonHidden = false
    private var errorJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewSizeChangeListener = view.setOnViewSizeChangeListener(this)
        contentView = view as ViewGroup

        authorizationFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(AuthorizationFragmentViewModel::class.java)
        }
        authorizationFragmentViewModel?.let { viewModel ->

            viewModel.passwordIsVisibleState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { visible ->
                    val anim = context?.let {
                        AnimatedVectorDrawableCompat.create(
                            it,
                            if (visible) R.drawable.eye_back_anim else R.drawable.eye_anim
                        )
                    }
                    authorizationVisible.setImageDrawable(anim)
                    anim?.start()
                    if (visible) {
                        authorizationPasswordEditText.transformationMethod = null
                    } else {
                        authorizationPasswordEditText.transformationMethod =
                            PasswordTransformationMethod()
                    }
                    authorizationPasswordEditText.setSelection(authorizationPasswordEditText.text.toString().length)
                })

            viewModel.errorEvent.observe(viewLifecycleOwner, Observer { message ->
                showError(message, AuthorizationFragmentViewModel.ErrorType.NoTypeError)
            })

            viewModel.progressState.observe(viewLifecycleOwner, Observer {
                if(it){
                    if(fragmentManager?.findFragmentByTag("PROGRESS_FRAGMENT") == null)
                        ProgressDialogAlertFragment().show(fragmentManager!!, "PROGRESS_FRAGMENT")
                }else{
                    (fragmentManager?.findFragmentByTag("PROGRESS_FRAGMENT") as? DialogFragment)?.dismiss()
                }
            })

            viewModel.passwordFieldIsEmptyState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { isEmpty ->
                    if (isEmpty) {
                        authorizationVisible.visibility = View.GONE
                        if (authorizationPasswordEditText.transformationMethod == null)
                            viewModel.changePasswordVisible(true)
                    } else {
                        authorizationVisible.visibility = View.VISIBLE
                    }
                })

            viewModel.authorizationState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<AuthorizationFragmentViewModel.AuthorizationResult> { result ->
                    when (result) {
                        is AuthorizationFragmentViewModel.AuthorizationResult.IncorrectEmail -> showError(
                            R.string.incorrect_mail,
                            AuthorizationFragmentViewModel.ErrorType.EmailError
                        )
                        is AuthorizationFragmentViewModel.AuthorizationResult.PasswordLessMixLength -> showError(
                            R.string.too_short_password,
                            AuthorizationFragmentViewModel.ErrorType.PasswordError
                        )
                        is AuthorizationFragmentViewModel.AuthorizationResult.NoUserFound -> showError(
                            R.string.no_user_with_mail,
                            AuthorizationFragmentViewModel.ErrorType.EmailError
                        )
                        is AuthorizationFragmentViewModel.AuthorizationResult.UserAlreadyExists -> showError(
                            R.string.user_already_exists,
                            AuthorizationFragmentViewModel.ErrorType.EmailError
                        )
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInFailure -> showError(
                            R.string.incorrect_password,
                            AuthorizationFragmentViewModel.ErrorType.PasswordError
                        )
                        is AuthorizationFragmentViewModel.AuthorizationResult.SignUpSuccess -> {
                            activity?.application?.let {
                                viewModel.logIn(
                                    result.email to result.password,
                                    it, true
                                )
                            }
                        }
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInSuccess -> {
                            AccountAuthenticator.addAccount(view.context, result.email)
                            authenticationState.value = CheckTokenIsCorrectStatus.Correct to false
                            activity?.startMainActivityClearTop(extras = bundleOf("IS_AFTER_SIGN_UP" to result.afterSignUp))
                        }
                    }
                })

            signUpOnClick =
                { viewModel.signUp(authorizationEmailEditText.text.toString() to authorizationPasswordEditText.text.toString()) }
            authorizationLogInButton.setOnClickListener {
                activity?.application?.let {
                    viewModel.logIn(
                        authorizationEmailEditText.text.toString() to authorizationPasswordEditText.text.toString(),
                        it, false
                    )
                }
            }
            authorizationSignUpButton.setOnClickListener { signUpOnClick.invoke() }
            authorizationVisible.setOnClickListener {
                viewModel.changePasswordVisible(
                    authorizationPasswordEditText.transformationMethod == null
                )
            }
        }

        if (savedInstanceState == null) {
            authorizationFragmentViewModel?.authorizationState?.value = null
        }

        authorizationPasswordEditText.addTextChangedListener(this)

        OneShotPreDrawListener.add(authorizationBottomSection) {
            initErrorSnackbar()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        authorizationFragmentViewModel?.setPasswordEmpty(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


    override fun onDestroyView() {
        super.onDestroyView()
        contentView.removeOnLayoutChangeListener(onViewSizeChangeListener)
    }

    override fun onViewSizeChanged(view: View) {

        val viewHeight =
            (authorizationCenterLinearLayout.height - authorizationCenterLinearLayout.getChildAt(0).marginTop) + authorizationBottomSection.height + resources.getDimension(
                R.dimen.toolbarHeight
            )

        if (viewHeight > getScreenHeight()) {
            if (!buttonHidden) {
                authorizationCenterLinearLayout.getChildAt(0).updateLayoutParams<LinearLayout.LayoutParams> {
                    topMargin = resources.getDimension(R.dimen.toolbarHeight).toInt()
                }
                authorizationSignUpButton.visibility = View.INVISIBLE
                authorizationSignUpButton.isEnabled = false
                authorizationForgotPassword.text = resources.getString(R.string.create_new_account)
                authorizationForgotPassword.visibility = View.VISIBLE
                authorizationForgotPassword.isEnabled = true
                authorizationForgotPassword.setOnClickListener { signUpOnClick.invoke() }
                buttonHidden = true
            }
        } else {
            if (buttonHidden) {
                authorizationCenterLinearLayout.getChildAt(0).updateLayoutParams<LinearLayout.LayoutParams> {
                    topMargin = 0
                }
                authorizationSignUpButton.visibility = View.VISIBLE
                authorizationSignUpButton.isEnabled = true
                authorizationForgotPassword.text = resources.getString(R.string.forgot_password)
                authorizationForgotPassword.visibility = View.INVISIBLE
                authorizationForgotPassword.isEnabled = false
                authorizationForgotPassword.setOnClickListener {}
                buttonHidden = false
            }
        }

    }

    private fun showError(resId: Int, errorType: AuthorizationFragmentViewModel.ErrorType) {
        if((authorizationErrorSnackbar as TextView).text != resources.getString(resId) || errorJob?.isCompleted == true) {
            hideError()
            when (errorType) {
                AuthorizationFragmentViewModel.ErrorType.EmailError -> {
                    authorizationEmailEditText.hasError = true
                    authorizationEmailEditText.requestFocus()
                }
                AuthorizationFragmentViewModel.ErrorType.PasswordError -> {
                    authorizationPasswordEditText.hasError = true
                    authorizationPasswordEditText.requestFocus()
                }
            }
            (authorizationErrorSnackbar as TextView).text = resources.getString(resId)
            initErrorSnackbar()
            authorizationBottomSection.animate()
                .translationY(0F)
                .setDuration(resources.getInteger(R.integer.snackbar_error_anim_duration).toLong())
                .setInterpolator(FastOutSlowInInterpolator()).start()
            errorJob?.cancel()
            errorJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                delay(resources.getInteger(R.integer.authorization_error_duration).toLong())
                hideError()
            }
        }
    }

    private fun hideError() {
        authorizationEmailEditText.hasError = false
        authorizationPasswordEditText.hasError = false
        authorizationBottomSection.animate()
            .translationY(authorizationErrorSnackbar.measuredHeight.toFloat())
            .setDuration(resources.getInteger(R.integer.snackbar_error_anim_duration).toLong())
            .setInterpolator(FastOutSlowInInterpolator()).start()
    }

    private fun initErrorSnackbar() {
        authorizationBottomSection.translationY = authorizationErrorSnackbar.height.toFloat()
    }

    private fun getScreenHeight(): Int {
        return authorizationRelativeLayout.height
    }


}