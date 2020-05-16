package com.example.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.domain.entity.UserIdTokenData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileMenuFragmentViewModel : ViewModel(){

    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()

    fun signOut(userIdTokenData: UserIdTokenData){
        GlobalScope.launch {
            authorizationInteractor.signOut(userIdTokenData)
        }
    }



}