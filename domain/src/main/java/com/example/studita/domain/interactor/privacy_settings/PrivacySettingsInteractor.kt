package com.example.studita.domain.interactor.privacy_settings

import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.EditPrivacySettingsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus

interface PrivacySettingsInteractor {

    suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): PrivacySettingsStatus

    suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): EditPrivacySettingsStatus
}