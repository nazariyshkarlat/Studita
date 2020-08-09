package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.HasFriendsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PrivacySettingsViewModel : ViewModel() {

    private val privacySettingsInteractor = PrivacySettingsModule.getPrivacySettingsInteractorImpl()
    private val usersInteractor = UsersModule.getUsersInteractorImpl()

    var hasFriends: Boolean = false

    val errorState = SingleLiveEvent<Int>()
    val privacySettingsStatus = MutableLiveData<PrivacySettingsData>()

    private var job: Job? = null

    init {
        UserUtils.getUserIDTokenData()?.let { getPrivacySettings(it) }
    }

    private fun getPrivacySettings(userIdTokenData: UserIdTokenData) {
        viewModelScope.launch {
            val hasFriends = async { hasFriends() }
            when (val result = privacySettingsInteractor.getPrivacySettings(userIdTokenData)) {
                is PrivacySettingsStatus.Failure -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.ServiceUnavailable -> errorState.postValue(R.string.no_connection)
                is PrivacySettingsStatus.Success -> {
                    this@PrivacySettingsViewModel.hasFriends =
                        hasFriends.await() is HasFriendsStatus.HasFriends
                    privacySettingsStatus.postValue(result.privacySettingsData)
                }
            }
        }
    }

    fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData) {
        job = GlobalScope.launchExt(job) {
            privacySettingsInteractor.editPrivacySettings(privacySettingsRequestData)
        }
    }

    private suspend fun hasFriends() = usersInteractor.hasFriends(PrefsUtils.getUserId()!!)
}