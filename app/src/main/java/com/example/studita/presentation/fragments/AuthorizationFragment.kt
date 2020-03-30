package com.example.studita.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.studita.R
import com.example.studita.authenticator.AccountAuthenticator
import com.example.studita.presentation.utils.dpToPx
import com.example.studita.presentation.utils.setOnViewSizeChangeListener
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.listeners.OnViewSizeChangeListener
import com.example.studita.presentation.view_model.AuthorizationFragmentViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import kotlinx.android.synthetic.main.authorization_layout.*


class AuthorizationFragment : NavigatableFragment(R.layout.authorization_layout), TextWatcher,
    OnViewSizeChangeListener {

    private var toolbarFragmentViewModel: ToolbarFragmentViewModel? = null
    private var authorizationFragmentViewModel: AuthorizationFragmentViewModel? = null
    private lateinit var signUpOnClick: () -> Unit
    private var secondBorder = 0
    private var centerLinearLayoutTopMargin = 0
    private var firstBorder = 0
    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener
    lateinit var contentView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        globalLayoutListener = authorizationRelativeLayout.setOnViewSizeChangeListener(this)
        contentView = authorizationRelativeLayout

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
                    hideError()
                    when(result){
                        is AuthorizationFragmentViewModel.AuthorizationResult.IncorrectEmail -> showError(R.string.incorrect_mail, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.PasswordLess6 -> showError(R.string.to_short_password, false)
                        is AuthorizationFragmentViewModel.AuthorizationResult.NoUserFound -> showError(R.string.no_user_with_mail, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.UserAlreadyExists -> showError(R.string.user_already_exists, true)
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInFailure -> showError(R.string.incorrect_password, false)
                        is AuthorizationFragmentViewModel.AuthorizationResult.SignUpSuccess -> {
                            Toast.makeText(context, resources.getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
                            AccountAuthenticator.addAccount(view.context, result.email)
                        }
                        is AuthorizationFragmentViewModel.AuthorizationResult.LogInSuccess -> Toast.makeText(context, resources.getString(R.string.log_in_success), Toast.LENGTH_LONG).show()
                        null ->{}
                        else -> Toast.makeText(context, resources.getString(R.string.server_error), Toast.LENGTH_LONG).show()
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

        centerLinearLayoutTopMargin = resources.getDimension(R.dimen.toolbarHeight).toInt() + 8.dpToPx()
        calculateBorders()
    }


    override fun onBackClick(){
        super.onBackClick()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    override fun afterTextChanged(s: Editable?) {
        authorizationFragmentViewModel?.setPasswordEmpty(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


    override fun onDestroyView() {
        super.onDestroyView()
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onViewSizeChanged(view: View) {

        val currentHeight = view.height

        val errorTextParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val centerLayoutParams = authorizationCenterLinearLayout.layoutParams as RelativeLayout.LayoutParams

        errorTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

        if(currentHeight < firstBorder){
            centerLayoutParams.setMargins(0, centerLinearLayoutTopMargin, 0 , 0)
            centerLayoutParams.removeRule(RelativeLayout.CENTER_VERTICAL)
            centerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            if(currentHeight < secondBorder) {
                errorTextParams.addRule(RelativeLayout.BELOW, R.id.authorizationCenterLinearLayout)
            }else{
                errorTextParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            authorizationForgotPassword.text = resources.getText(R.string.create_new_account)
            authorizationForgotPassword.setOnClickListener{signUpOnClick.invoke()}
            authorizationSignUpButton.visibility = View.GONE
        }else{
            centerLayoutParams.setMargins(0, 0, 0 , 0)
            centerLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP)
            centerLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
            errorTextParams.addRule(RelativeLayout.ABOVE, R.id.authorizationSignUpButton)
            authorizationForgotPassword.text = resources.getText(R.string.forgot_password)
            authorizationForgotPassword.setOnClickListener{}
            authorizationSignUpButton.visibility = View.VISIBLE
        }

        authorizationCenterLinearLayout.layoutParams = centerLayoutParams
        authorizationErrorText.layoutParams = errorTextParams
    }

    private fun calculateBorders(){
        OneShotPreDrawListener.add(contentView) {
            secondBorder =
                authorizationCenterLinearLayout.height + authorizationErrorText.height + centerLinearLayoutTopMargin + 8.dpToPx()
            firstBorder = secondBorder + authorizationSignUpButton.height + (authorizationSignUpButton.layoutParams as RelativeLayout.LayoutParams).bottomMargin
        }
    }

    private fun showError(resId: Int, email: Boolean){
        if(email) {
            authorizationEmailEditText.isError = true
            authorizationEmailEditText.requestFocus()
        }else {
            authorizationPasswordEditText.isError = true
            authorizationPasswordEditText.requestFocus()
        }
        authorizationErrorText.text = resources.getString(resId)
    }

    private fun hideError(){
        authorizationEmailEditText.isError = false
        authorizationPasswordEditText.isError = false
        authorizationErrorText.text = null
    }


}