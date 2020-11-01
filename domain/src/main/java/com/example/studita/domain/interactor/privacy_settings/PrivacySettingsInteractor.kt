package com.example.studita.domain.interactor.privacy_settings

import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.EditDuelsExceptionsStatus
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus

interface PrivacySettingsInteractor {

    suspend fun getPrivacySettings(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 3
    ): PrivacySettingsStatus

    suspend fun editPrivacySettings(
        privacySettingsRequestData: PrivacySettingsRequestData,
        retryCount: Int = 3
    ): EditPrivacySettingsStatus

    suspend fun getPrivacyDuelsExceptionsList(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int,
        retryCount: Int = 3
    ): PrivacySettingsDuelsExceptionsStatus

    suspend fun editDuelsExceptions(
        editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData,
        retryCount: Int = 3
    ): EditDuelsExceptionsStatus
}