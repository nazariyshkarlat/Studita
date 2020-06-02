package com.example.studita.domain.interactor.privacy_settings

import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.repository.PrivacySettingsRepository

class PrivacySettingsInteractorImpl(private val repository: PrivacySettingsRepository) : PrivacySettingsInteractor{
    override suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData) =
            try {
                val result = repository.getPrivacySettings(userIdTokenData)
                when (result.first) {
                    200 -> PrivacySettingsStatus.Success(result.second!!)
                    else -> PrivacySettingsStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    PrivacySettingsStatus.NoConnection
                }else
                    PrivacySettingsStatus.ServiceUnavailable
            }

    override suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData) =
            try {
                when (repository.editPrivacySettings(privacySettingsRequestData)) {
                    200 -> EditPrivacySettingsStatus.Success
                    else -> EditPrivacySettingsStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    EditPrivacySettingsStatus.NoConnection
                }else
                    EditPrivacySettingsStatus.ServiceUnavailable
            }
}