package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.domain.entity.EditDuelsExceptionsData
import com.studita.domain.entity.EditDuelsExceptionsRequestData
import com.studita.domain.entity.PrivacyDuelsExceptionData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.EditDuelsExceptionsStatus
import com.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.studita.domain.interactor.privacy_settings.PrivacySettingsInteractor
import com.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.studita.presentation.model.toUiModel
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class PrivacySettingsDuelsExceptionsViewModel : ViewModel() {

    private val privacySettingsInteractor = GlobalContext.get().get<PrivacySettingsInteractor>()

    val privacySettingsDuelsExceptionsState =
        MutableLiveData<Pair<Boolean, DuelsExceptionsResultState>>()
    val privacySettingsEditDuelsExceptionsState = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()

    var recyclerItems: ArrayList<PrivacySettingsDuelsExceptionsRecyclerUiModel>? = null
    val editedDuelsExceptionsData = arrayListOf<EditDuelsExceptionsData>()

    val progressState = MutableLiveData<Boolean>()

    val perPage = 20
    var currentPageNumber = 1

    var duelsExceptionsJob: Job? = null

    init {
        getPrivacySettingsDuelsExceptions(UserUtils.getUserIDTokenData()!!, false)
    }

    fun getPrivacySettingsDuelsExceptions(userIdTokenData: UserIdTokenData, newPage: Boolean) {

        if (newPage)
            currentPageNumber++
        else
            currentPageNumber = 1

        duelsExceptionsJob = viewModelScope.launchExt(duelsExceptionsJob) {
            when (val result = privacySettingsInteractor.getPrivacyDuelsExceptionsList(
                userIdTokenData,
                perPage,
                currentPageNumber
            )) {
                is PrivacySettingsDuelsExceptionsStatus.NoConnection -> errorState.value = R.string.no_connection
                is PrivacySettingsDuelsExceptionsStatus.ServiceUnavailable -> errorState.value = R.string.no_connection
                is PrivacySettingsDuelsExceptionsStatus.NoUsersFound -> {
                    privacySettingsDuelsExceptionsState.value =
                        false to DuelsExceptionsResultState.NoMoreResultsFound
                }
                is PrivacySettingsDuelsExceptionsStatus.Success -> {

                    val duelsExceptionsResultState = if (currentPageNumber == 1)
                        DuelsExceptionsResultState.FirstResults(
                            result.privacySettingsDuelsExceptionsItems
                        ) else DuelsExceptionsResultState.MoreResults(
                        result.privacySettingsDuelsExceptionsItems
                    )

                    progressState.value = false

                    privacySettingsDuelsExceptionsState.value =
                        canBeMoreItems(
                            duelsExceptionsResultState
                        ) to duelsExceptionsResultState
                }
            }
        }
    }

    private fun canBeMoreItems(duelsExceptionsResultState: DuelsExceptionsResultState) =
        (((duelsExceptionsResultState is DuelsExceptionsResultState.FirstResults) && duelsExceptionsResultState.results.size == perPage) ||
                ((duelsExceptionsResultState is DuelsExceptionsResultState.MoreResults) && duelsExceptionsResultState.results.size == perPage))


    fun editDuelsExceptions() {
        GlobalScope.launch {
            if (editedDuelsExceptionsData.isNotEmpty()) {
                val result = privacySettingsInteractor.editDuelsExceptions(
                    EditDuelsExceptionsRequestData(
                        UserUtils.getUserIDTokenData()!!,
                        editedDuelsExceptionsData
                    )
                )

                if(result is EditDuelsExceptionsStatus.Success)
                    privacySettingsEditDuelsExceptionsState.value = true
                else if(result is EditDuelsExceptionsStatus.ServiceUnavailable)
                    privacySettingsEditDuelsExceptionsState.value = false
            }
        }
    }

    fun duelsExceptionsRequestIsPending() = duelsExceptionsJob?.isActive == true

    fun getRecyclerItems(
        usersItems: List<PrivacyDuelsExceptionData>,
        progressUiModel: PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel? = null
    ): List<PrivacySettingsDuelsExceptionsRecyclerUiModel> {
        val adapterItems = ArrayList<PrivacySettingsDuelsExceptionsRecyclerUiModel>()
        adapterItems.addAll(usersItems.map { it.toUiModel() })
        if (progressUiModel != null)
            adapterItems.add(progressUiModel)
        return adapterItems
    }


    sealed class DuelsExceptionsResultState {
        data class FirstResults(val results: List<PrivacyDuelsExceptionData>) :
            DuelsExceptionsResultState()

        data class MoreResults(val results: List<PrivacyDuelsExceptionData>) :
            DuelsExceptionsResultState()

        object NoMoreResultsFound : DuelsExceptionsResultState()
    }


}