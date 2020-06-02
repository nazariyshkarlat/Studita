package com.example.studita.domain.repository

import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData

interface PrivacySettingsRepository {

    suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): Pair<Int, PrivacySettingsData?>

    suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): Int
}