package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.HasFriendsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class PrivacySettingsViewModel : ViewModel() {

    private val privacySettingsInteractor = PrivacySettingsModule.getPrivacySettingsInteractorImpl()
    private val usersInteractor = UsersModule.getUsersInteractorImpl()

    var hasFriends: Boolean = false

    val errorSnackbarEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<Boolean>()
    val privacySettingsStatus = MutableLiveData<PrivacySettingsData>()

    private var job: Job? = null

    init {
        getPrivacySettings(UserUtils.getUserIDTokenData()!!)
    }

    fun getPrivacySettings(userIdTokenData: UserIdTokenData) {
        viewModelScope.launch(Dispatchers.Main) {
            val hasFriends = async { hasFriends() }
            when (val result = privacySettingsInteractor.getPrivacySettings(userIdTokenData)) {
                is PrivacySettingsStatus.NoConnection -> errorEvent.value = true
                is PrivacySettingsStatus.ServiceUnavailable -> errorEvent.value = false
                is PrivacySettingsStatus.Success -> {
                    this@PrivacySettingsViewModel.hasFriends =
                        hasFriends.await() is HasFriendsStatus.HasFriends
                    privacySettingsStatus.value = result.privacySettingsData
                }
            }
        }
    }

    fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData) {
        job = GlobalScope.launchExt(job) {
            val result = privacySettingsInteractor.editPrivacySettings(privacySettingsRequestData)

            if(result == EditPrivacySettingsStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
    }

    private suspend fun hasFriends() = usersInteractor.hasFriends(PrefsUtils.getUserId()!!)
}