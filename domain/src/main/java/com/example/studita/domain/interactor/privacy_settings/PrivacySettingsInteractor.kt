package com.example.studita.domain.interactor.privacy_settings

import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacyDuelsExceptionData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.EditDuelsExceptionsStatus
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus

interface PrivacySettingsInteractor {

    suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): PrivacySettingsStatus

    suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): EditPrivacySettingsStatus

    suspend fun getPrivacyDuelsExceptionsList(userIdTokenData: UserIdTokenData, perPage: Int, pageNumber: Int) : PrivacySettingsDuelsExceptionsStatus

    suspend fun editDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData): EditDuelsExceptionsStatus
}