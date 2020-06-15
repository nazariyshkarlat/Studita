package com.example.studita.domain.interactor.privacy_settings

import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.*
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

    override suspend fun getPrivacyDuelsExceptionsList(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): PrivacySettingsDuelsExceptionsStatus =
        try {
            val result = repository.getPrivacyDuelsExceptionsList(userIdTokenData, perPage, pageNumber)
            when (result.first) {
                200 -> if(result.second!!.isNotEmpty()) PrivacySettingsDuelsExceptionsStatus.Success(result.second!!) else PrivacySettingsDuelsExceptionsStatus.NoUsersFound
                else -> PrivacySettingsDuelsExceptionsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException) {
                PrivacySettingsDuelsExceptionsStatus.NoConnection
            }else
                PrivacySettingsDuelsExceptionsStatus.ServiceUnavailable
        }

    override suspend fun editDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData)=
        try {
            when (repository.editDuelsExceptions(editDuelsExceptionsRequestData)) {
                200 -> EditDuelsExceptionsStatus.Success
                else -> EditDuelsExceptionsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException) {
                EditDuelsExceptionsStatus.NoConnection
            }else
                EditDuelsExceptionsStatus.ServiceUnavailable
        }
}