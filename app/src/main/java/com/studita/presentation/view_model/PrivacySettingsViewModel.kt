package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.domain.entity.PrivacySettingsData
import com.studita.domain.entity.PrivacySettingsRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.EditPrivacySettingsStatus
import com.studita.domain.interactor.HasFriendsStatus
import com.studita.domain.interactor.PrivacySettingsStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.privacy_settings.PrivacySettingsInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import kotlinx.coroutines.*
import org.koin.core.context.GlobalContext

class PrivacySettingsViewModel : ViewModel() {

    private val privacySettingsInteractor = GlobalContext.get().get<PrivacySettingsInteractor>()
    private val usersInteractor = GlobalContext.get().get<UsersInteractor>()

    var hasFriends: Boolean = false

    val errorSnackbarEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<Boolean>()
    val privacySettingsStatus = MutableLiveData<PrivacySettingsData>()

    init {
        getPrivacySettings(UserUtils.getUserIDTokenData()!!)
    }

    fun getPrivacySettings(userIdTokenData: UserIdTokenData) {
        viewModelScope.launch(Dispatchers.Main) {

            if(App.userDataDeferred.isCompleted && App.userDataDeferred.await() !is UserDataStatus.Success)
                App.authenticate(UserUtils.getUserIDTokenData(), true)

            val hasFriends = async { hasFriends() }
            when (val result = privacySettingsInteractor.getPrivacySettings(userIdTokenData)) {
                is PrivacySettingsStatus.NoConnection -> errorEvent.value = true
                is PrivacySettingsStatus.ServiceUnavailable -> errorEvent.value = false
                is PrivacySettingsStatus.Success -> {

                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
                            this@PrivacySettingsViewModel.hasFriends =
                                hasFriends.await() is HasFriendsStatus.HasFriends
                            privacySettingsStatus.value = result.privacySettingsData
                        }
                        is UserDataStatus.NoConnection -> {
                            errorEvent.value = true
                        }
                        else -> {
                            errorEvent.value = false
                        }
                    }
                }
            }
        }
    }

    fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = privacySettingsInteractor.editPrivacySettings(privacySettingsRequestData)

            if(result == EditPrivacySettingsStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
    }

    private suspend fun hasFriends() = usersInteractor.hasFriends(PrefsUtils.getUserId()!!)
}