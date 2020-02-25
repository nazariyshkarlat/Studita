package com.example.studita.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.studita.R
import com.example.studita.presentation.extensions.dpToPx
import com.example.studita.presentation.extensions.setOnViewSizeChangeListener
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.listeners.OnViewSizeChangeListener
import com.example.studita.presentation.view_model.AuthorizationFragmentViewModel
import com.example.studita.presentation.view_model.MainMenuActivityViewModel
import kotlinx.android.synthetic.main.authorization_layout.*

class AuthorizationFragment : NavigatableFragment(R.layout.authorization_layout), TextWatcher,
    OnViewSizeChangeListener {

    private var mainActivityViewModel: MainMenuActivityViewModel? = null
    private var authorizationFragmentViewModel: AuthorizationFragmentViewModel? = null
    private lateinit var signUpOnClick: () -> Unit
    private val set = ConstraintSet()
    private var secondBorder = 0
    private var firstBorder = 0
    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener
    lateinit var contentView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalLayoutListener = authorizationConstraintLayout.setOnViewSizeChangeListener(this)
        contentView = authorizationConstraintLayout

        mainActivityViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)
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
                    authorizationPasswordEditText.setSelection(authorizationPasswordEditText.text.length)
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
                        AuthorizationFragmentViewModel.AuthorizationResult.INCORRECT_EMAIL -> showError(R.string.incorrect_mail, true)
                        AuthorizationFragmentViewModel.AuthorizationResult.PASSWORD_LESS_6 -> showError(R.string.to_short_password, false)
                        AuthorizationFragmentViewModel.AuthorizationResult.NO_USER_FOUND -> showError(R.string.no_user_with_mail, true)
                        AuthorizationFragmentViewModel.AuthorizationResult.USER_ALREADY_EXISTS -> showError(R.string.user_already_exists, true)
                        AuthorizationFragmentViewModel.AuthorizationResult.LOG_IN_FAILURE -> showError(R.string.incorrect_password, false)
                        AuthorizationFragmentViewModel.AuthorizationResult.SIGN_UP_SUCCESS -> Toast.makeText(context, resources.getString(R.string.sign_up_success), Toast.LENGTH_LONG).show()
                        AuthorizationFragmentViewModel.AuthorizationResult.LOG_IN_SUCCESS -> Toast.makeText(context, resources.getString(R.string.log_in_success), Toast.LENGTH_LONG).show()
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

        calculateBorders()

        set.clone(view as ConstraintLayout)

        if(currentHeight < firstBorder){
            set.setVerticalBias(R.id.authorizationCenterLinearLayout, 0F)
            if(currentHeight < secondBorder) {
                set.connect(
                    R.id.authorizationErrorText,
                    ConstraintSet.TOP,
                    R.id.authorizationBottomLinearLayout,
                    ConstraintSet.BOTTOM
                )
                set.clear(R.id.authorizationErrorText, ConstraintSet.BOTTOM)
            }else{
                set.connect(
                    R.id.authorizationErrorText,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                set.setVerticalBias(R.id.authorizationErrorText, 1F)
            }
            set.connect(R.id.authorizationBottomLinearLayout, ConstraintSet.TOP, R.id.authorizationCenterLinearLayout, ConstraintSet.BOTTOM)
            set.applyTo(view)
            mainActivityViewModel?.let {
                setCenterLayoutTopPadding(it.toolbarHeight + 8.dpToPx())
            }
            authorizationForgotPassword.text = resources.getText(R.string.create_new_account)
            authorizationForgotPassword.setOnClickListener{signUpOnClick.invoke()}
            authorizationSignUpButton.visibility = View.GONE
        }else{
            set.connect(
                R.id.authorizationBottomLinearLayout,
                ConstraintSet.TOP,
                R.id.authorizationCenterLinearLayout,
                ConstraintSet.BOTTOM
            )
            set.connect(R.id.authorizationErrorText, ConstraintSet.BOTTOM, R.id.authorizationSignUpButton, ConstraintSet.TOP)
            set.setVerticalBias(R.id.authorizationCenterLinearLayout, 0.5F)
            set.setVerticalBias(R.id.authorizationErrorText, 1F)
            set.applyTo(view)
            setCenterLayoutTopPadding(0)
            authorizationForgotPassword.text = resources.getText(R.string.forgot_password)
            authorizationForgotPassword.setOnClickListener{}
            authorizationSignUpButton.visibility = View.VISIBLE
        }
    }

    private fun setCenterLayoutTopPadding(padding: Int){
        authorizationCenterLinearLayout.setPadding(
            authorizationCenterLinearLayout.paddingLeft,
            padding,
            authorizationCenterLinearLayout.paddingRight,
            authorizationCenterLinearLayout.paddingBottom)
    }

    private fun calculateBorders(){
        if((secondBorder == 0) and (mainActivityViewModel?.toolbarHeight != 0) and (view?.height != 0)) {
            secondBorder =
                authorizationCenterLinearLayout.height + authorizationBottomLinearLayout.height + (mainActivityViewModel?.toolbarHeight
                    ?: 0) + 16.dpToPx() + authorizationErrorText.height
            firstBorder = secondBorder + authorizationSignUpButton.height + 16.dpToPx()
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