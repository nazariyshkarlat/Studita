package com.studita.domain.interactor.privacy_settings

import com.studita.domain.entity.EditDuelsExceptionsRequestData
import com.studita.domain.entity.PrivacySettingsRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.EditDuelsExceptionsStatus
import com.studita.domain.interactor.EditPrivacySettingsStatus
import com.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.studita.domain.interactor.PrivacySettingsStatus

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