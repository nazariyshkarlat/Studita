package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class PrivacySettingsViewModel : ViewModel(){

    val errorState = SingleLiveEvent<Int>()

    private val privacySettingsInteractor = PrivacySettingsModule.getPrivacySettingsInteractorImpl()
    private var job: Job? = null

    val privacySettingsStatus = MutableLiveData<PrivacySettingsData>()

    init {
        UserUtils.getUserIDTokenData()?.let { getPrivacySettings(it) }
    }

    private fun getPrivacySettings(userIdTokenData: UserIdTokenData){
        job = viewModelScope.launchExt(job){
            when(val result = privacySettingsInteractor.getPrivacySettings(userIdTokenData)){
                is PrivacySettingsStatus.Failure -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.ServiceUnavailable -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.Success -> {
                    privacySettingsStatus.postValue(result.privacySettingsData)
                }
            }
        }
    }

    fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData){
        job = GlobalScope.launchExt(job){
            privacySettingsInteractor.editPrivacySettings(privacySettingsRequestData)
        }
    }

}