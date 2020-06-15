package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.domain.entity.EditDuelsExceptionsData
import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacyDuelsExceptionData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PrivacySettingsDuelsExceptionsViewModel : ViewModel(){

    private val privacySettingsInteractor = PrivacySettingsModule.getPrivacySettingsInteractorImpl()

    val privacySettingsDuelsExceptionsState = MutableLiveData<PrivacySettingsViewModel.DuelsExceptionsResultState>()
    val errorState = SingleLiveEvent<Int>()

    var recyclerItems: ArrayList<PrivacySettingsDuelsExceptionsRecyclerUiModel>? = null
    val editedDuelsExceptionsData = arrayListOf<EditDuelsExceptionsData>()

    val progressState = MutableLiveData<Boolean>()

    val perPage = 20
    var currentPageNumber = 1

    init {
        getPrivacySettingsDuelsExceptions(UserUtils.getUserIDTokenData()!!, false)
    }

     fun getPrivacySettingsDuelsExceptions(userIdTokenData: UserIdTokenData, newPage: Boolean){

        if(newPage)
            currentPageNumber++
        else
            currentPageNumber=1

        viewModelScope.launch{
            when(val result = privacySettingsInteractor.getPrivacyDuelsExceptionsList(userIdTokenData, perPage, currentPageNumber)){
                is PrivacySettingsDuelsExceptionsStatus.Failure -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsDuelsExceptionsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsDuelsExceptionsStatus.ServiceUnavailable -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsDuelsExceptionsStatus.NoUsersFound -> {
                    privacySettingsDuelsExceptionsState.postValue(
                        PrivacySettingsViewModel.DuelsExceptionsResultState.NoMoreResultsFound
                    )
                }
                is PrivacySettingsDuelsExceptionsStatus.Success -> {

                    progressState.postValue(false)

                    if(currentPageNumber == 1)
                        privacySettingsDuelsExceptionsState.postValue(
                            PrivacySettingsViewModel.DuelsExceptionsResultState.FirstResults(
                                result.privacySettingsDuelsExceptionsItems
                            ))
                    else
                        privacySettingsDuelsExceptionsState.postValue(
                            PrivacySettingsViewModel.DuelsExceptionsResultState.MoreResults(
                                result.privacySettingsDuelsExceptionsItems
                            ))
                }
            }
        }
    }

    fun editDuelsExceptions(){
        GlobalScope.launch{
            if(editedDuelsExceptionsData.isNotEmpty()) {
                privacySettingsInteractor.editDuelsExceptions(EditDuelsExceptionsRequestData(UserUtils.getUserIDTokenData()!!, editedDuelsExceptionsData))
            }
        }
    }


    fun getRecyclerItems(usersItems: List<PrivacyDuelsExceptionData>, progressUiModel: PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel): List<PrivacySettingsDuelsExceptionsRecyclerUiModel>{
        val adapterItems = ArrayList<PrivacySettingsDuelsExceptionsRecyclerUiModel>()
        adapterItems.addAll(usersItems.map { it.toUiModel() })
        adapterItems.add(progressUiModel)
        return adapterItems
    }


}