package com.example.studita.presentation.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.studita.App
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.utils.DeviceUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileMenuFragmentViewModel : ViewModel(){

    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()

    fun signOut(userIdTokenData: UserIdTokenData, application: Application){
        GlobalScope.launch {
            authorizationInteractor.signOut(SignOutRequestData(userIdTokenData, DeviceUtils.getDeviceId(application)))
        }
    }

}