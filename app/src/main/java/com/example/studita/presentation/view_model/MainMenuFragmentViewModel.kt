package com.example.studita.presentation.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.R
import java.lang.UnsupportedOperationException

class MainMenuFragmentViewModel : ViewModel(){

    val signUpMethodState = SingleLiveEvent<SignUpMehod>()

    fun onSignUpLogInClick(viewId: Int){
        when(viewId) {
            R.id.mainMenuWithGoogleButton -> signUpMethodState.postValue(SignUpMehod.WITH_GOOGLE)
            R.id.mainMenuUseEmailButton -> signUpMethodState.postValue(SignUpMehod.USE_EMAIL)
            else -> throw UnsupportedOperationException("unknown view id")
        }
    }

    enum class SignUpMehod{
        WITH_GOOGLE,
        USE_EMAIL
    }
}