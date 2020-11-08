package com.studita.domain.interactor.privacy_settings

import com.studita.domain.entity.EditDuelsExceptionsRequestData
import com.studita.domain.entity.PrivacySettingsRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.*
import com.studita.domain.repository.PrivacySettingsRepository
import com.studita.domain.service.SyncPrivacySettings
import kotlinx.coroutines.delay

class PrivacySettingsInteractorImpl(
    private val repository: PrivacySettingsRepository,
    private val syncPrivacySettings: SyncPrivacySettings
) : PrivacySettingsInteractor {

    private val retryDelay = 1000L

    override suspend fun getPrivacySettings(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): PrivacySettingsStatus =
        try {
            val result = repository.getPrivacySettings(userIdTokenData)
            when (result.first) {
                200 -> PrivacySettingsStatus.Success(result.second!!)
                else -> PrivacySettingsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        PrivacySettingsStatus.NoConnection
                    } else
                        PrivacySettingsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getPrivacySettings(userIdTokenData, retryCount - 1)
                }
            } else
                PrivacySettingsStatus.Failure
        }

    override suspend fun editPrivacySettings(
        privacySettingsRequestData: PrivacySettingsRequestData,
        retryCount: Int
    ): EditPrivacySettingsStatus =
        try {
            when (repository.editPrivacySettings(privacySettingsRequestData)) {
                200 -> EditPrivacySettingsStatus.Success
                else -> EditPrivacySettingsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncPrivacySettings.scheduleEditPrivacySettings(
                            privacySettingsRequestData
                        )
                        EditPrivacySettingsStatus.NoConnection
                    }else
                        EditPrivacySettingsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    editPrivacySettings(privacySettingsRequestData, retryCount - 1)
                }
            } else
                EditPrivacySettingsStatus.Failure
        }

    override suspend fun getPrivacyDuelsExceptionsList(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int,
        retryCount: Int
    ): PrivacySettingsDuelsExceptionsStatus =
        try {
            val result =
                repository.getPrivacyDuelsExceptionsList(userIdTokenData, perPage, pageNumber)
            when (result.first) {
                200 -> if (result.second!!.isNotEmpty()) PrivacySettingsDuelsExceptionsStatus.Success(
                    result.second!!
                ) else PrivacySettingsDuelsExceptionsStatus.NoUsersFound
                else -> PrivacySettingsDuelsExceptionsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        PrivacySettingsDuelsExceptionsStatus.NoConnection
                    } else
                        PrivacySettingsDuelsExceptionsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getPrivacyDuelsExceptionsList(
                        userIdTokenData,
                        perPage,
                        pageNumber,
                        retryCount - 1
                    )
                }
            } else
                PrivacySettingsDuelsExceptionsStatus.Failure
        }

    override suspend fun editDuelsExceptions(
        editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData,
        retryCount: Int
    ): EditDuelsExceptionsStatus =
        try {
            when (repository.editDuelsExceptions(editDuelsExceptionsRequestData)) {
                200 -> EditDuelsExceptionsStatus.Success
                else -> EditDuelsExceptionsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                when {
                    e is NetworkConnectionException -> {
                        syncPrivacySettings.scheduleEditDuelsExceptions(
                            editDuelsExceptionsRequestData
                        )
                        EditDuelsExceptionsStatus.NoConnection
                    }
                    retryCount == 0 -> EditDuelsExceptionsStatus.ServiceUnavailable
                    else -> {
                        editDuelsExceptions(editDuelsExceptionsRequestData, retryCount - 1)
                    }
                }
            } else
                EditDuelsExceptionsStatus.Failure
        }
}