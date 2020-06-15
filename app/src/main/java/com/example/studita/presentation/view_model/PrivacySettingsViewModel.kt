package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.domain.entity.*
import com.example.studita.domain.interactor.EditDuelsExceptionsStatus
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.presentation.model.toUserItemUiModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PrivacySettingsViewModel : ViewModel(){

    private val privacySettingsInteractor = PrivacySettingsModule.getPrivacySettingsInteractorImpl()

    val errorState = SingleLiveEvent<Int>()
    val privacySettingsStatus = MutableLiveData<PrivacySettingsData>()

    private var job: Job? = null

    init {
        UserUtils.getUserIDTokenData()?.let { getPrivacySettings(it) }
    }

    private fun getPrivacySettings(userIdTokenData: UserIdTokenData){
        viewModelScope.launch{
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
    sealed class DuelsExceptionsResultState{
        data class FirstResults(val results: List<PrivacyDuelsExceptionData>): DuelsExceptionsResultState()
        data class MoreResults(val results: List<PrivacyDuelsExceptionData>) : DuelsExceptionsResultState()
        object NoMoreResultsFound: DuelsExceptionsResultState()
    }

}