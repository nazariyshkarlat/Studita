package com.example.studita.presentation.fragments

import android.graphics.Interpolator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.studita.R
import com.example.studita.authenticator.AccountAuthenticator
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.listeners.OnViewSizeChangeListener
import com.example.studita.presentation.utils.setOnViewSizeChangeListener
import com.example.studita.presentation.view_model.AuthorizationFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import kotlinx.android.synthetic.main.authorization_layout.*


class AuthorizationFragment : NavigatableFragment(R.layout.authorization_layout), TextWatcher, OnViewSizeChangeListener{

    private var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null
    private var authorizationFragmentViewModel: AuthorizationFragmentViewModel? = null
    private lateinit var signUpOnClick: () -> Unit
    private lateinit var onViewSizeChangeListener: View.OnLayoutChangeListener
    lateinit var contentView: ViewGroup
    private var buttonHidden = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        onViewSizeChangeListener = view.setOnViewSizeChangeListener(this)
        contentView = view as ViewGroup

        toolbarFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ToolbarFragmentViewModel::class.java)
        }
        authorizationFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(AuthorizationFragmentViewModel::class.java)
        }
        authorizationFragmentViewModel?.let{viewModel->

            viewModel.passwordIsVisibleState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> {visible ->
                    val anim = context?.let { AnimatedVectorDrawableCompat.create(it, if (visible) R.drawable.eye_back_anim else R.drawable.eye_anim) }
                    authorizationVisible.setImageDrawable(anim)
                    anim?.start()
                    if(visible) {
                        authorizationPasswordEditText.transformationMethod = null
                    }else{
                        authorizationPasswordEditText.transformationMethod = PasswordTransformationMethod()
                    }
                    authorizationPasswordEditText.setSelection(authorizationPasswordEditText.text.toString().length)
                })

            viewModel.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })

            viewModel.passwordFieldIsEmptyState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> {isEmpty ->
                    if(isEmpty) {
                        authorizationVisible.visibility = View.GONE
                        if(authorizationPasswordEditText.transformationMethod == null)
                            viewModel.changePasswordVisible(true)
                    }else{
                        authorizationVisible.visibility = View.VISIBLE
                    }
                })

            viewModel.authorizationState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<AuthorizationFragmentViewModel.AuthorizationResult> {result ->
                    when(result){
                        is AuthorizationFragmentViewModel.AuthorizationResult.IncorrectEmail -> showError(R.string.incorrect_mail, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.PasswordLess6 -> showError(R.string.to_short_password, false)
                        is AuthorizationFragmentViewModel.AuthorizationResult.NoUserFound -> showError(R.string.no_user_with_mail, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.UserAlreadyExists -> showError(R.string.user_already_exists, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInFailure -> showError(R.string.incorrect_password, false)
                        is AuthorizationFragmentViewModel.AuthorizationResult.SignUpSuccess -> {
                            Toast.makeText(context, resources.getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
                            AccountAuthenticator.addAccount(view.context, result.email)
                            viewModel.logIn(result.email to result.password)
                        }
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInSuccess -> {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.log_in_success),
                                Toast.LENGTH_LONG
                            ).show()
                            MainActivity.needsRecreate = true
                            activity?.finish()
                        }
                        null ->{}
                        else -> Toast.makeText(context, resources.getString(R.string.server_failure), Toast.LENGTH_LONG).show()
                    }
                })

            signUpOnClick = {viewModel.signUp(authorizationEmailEditText.text.toString() to authorizationPasswordEditText.text.toString())}
            authorizationLogInButton.setOnClickListener {viewModel.logIn(authorizationEmailEditText.text.toString() to authorizationPasswordEditText.text.toString())}
            authorizationSignUpButton.setOnClickListener{signUpOnClick.invoke()}
            authorizationVisible.setOnClickListener {viewModel.changePasswordVisible(authorizationPasswordEditText.transformationMethod == null)}
        }

        if(savedInstanceState == null){
            authorizationFragmentViewModel?.authorizationState?.value = null
        }

        authorizationPasswordEditText.addTextChangedListener(this)
        authorizationEmailEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { if(authorizationEmailEditText.hasError) hideError()}
        })
    }


    override fun onBackClick(){
        super.onBackClick()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    override fun afterTextChanged(s: Editable?) {
        authorizationFragmentViewModel?.setPasswordEmpty(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(authorizationPasswordEditText.hasError)
            hideError()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        contentView.removeOnLayoutChangeListener(onViewSizeChangeListener)
    }

    override fun onViewSizeChanged(view: View) {

        val viewHeight = view.height

        if((viewHeight < getScreenHeight()*0.75)){
            if(!buttonHidden) {
                authorizationBottomSection.removeView(authorizationSignUpButton)
                authorizationForgotPassword.text = resources.getString(R.string.create_new_account)
                authorizationForgotPassword.setOnClickListener { signUpOnClick }
                buttonHidden = true
            }
        }else{
            if(buttonHidden) {
                authorizationBottomSection.addView(authorizationSignUpButton, 0)
                authorizationForgotPassword.text = resources.getString(R.string.forgot_password)
                authorizationForgotPassword.setOnClickListener {}
                buttonHidden = false
            }
        }

    }

    private fun showError(resId: Int, email: Boolean){
        if(email) {
            authorizationEmailEditText.hasError = true
            authorizationEmailEditText.requestFocus()
        }else {
            authorizationPasswordEditText.hasError = true
            authorizationPasswordEditText.requestFocus()
        }
        (authorizationErrorSnackbar as TextView).text = resources.getString(resId)
        authorizationBottomSection.animate().translationY(-resources.getDimension(R.dimen.errorSnackbarHeight)).setDuration(resources.getInteger(R.integer.snackbar_error_anim_duration).toLong()).setInterpolator(FastOutSlowInInterpolator()).start()
    }

    private fun hideError(){
        authorizationEmailEditText.hasError = false
        authorizationPasswordEditText.hasError = false
        authorizationBottomSection.animate().translationY(0F).setDuration(resources.getInteger(R.integer.snackbar_error_anim_duration).toLong()).setInterpolator(FastOutSlowInInterpolator()).start()
    }

    private fun getScreenHeight() : Int {
        val display = contentView.display
        val outMetrics = DisplayMetrics()
        display.getRealMetrics(outMetrics)
        return outMetrics.heightPixels
    }


}