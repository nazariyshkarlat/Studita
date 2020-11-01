package com.example.studita.presentation.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.utils.DeviceUtils
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileMenuFragmentViewModel : ViewModel() {

    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    val localUserDataState = MutableLiveData<UserDataData>()

    init {
        if(!PrefsUtils.isOfflineModeEnabled())
            getUserDataLocal()
    }

    fun signOut(userIdTokenData: UserIdTokenData, application: Application) {
        GlobalScope.launch(Dispatchers.Main) {
            authorizationInteractor.signOut(
                SignOutRequestData(
                    userIdTokenData,
                    DeviceUtils.getDeviceId(application)
                )
            )
        }
    }

    private fun getUserDataLocal(){
        viewModelScope.launch(Dispatchers.Main) {
            val userDataStatus = userDataInteractor.getUserData(
                PrefsUtils.getUserId()!!, true, true)
            if(userDataStatus is UserDataStatus.Success)
                localUserDataState.value = userDataStatus.result
        }
    }

}