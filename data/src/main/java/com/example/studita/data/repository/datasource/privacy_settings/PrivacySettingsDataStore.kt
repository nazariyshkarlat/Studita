package com.example.studita.data.repository.datasource.privacy_settings

import com.example.studita.data.entity.PrivacySettingsEntity
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.UserIdToken

interface PrivacySettingsDataStore{

    suspend fun tryGetPrivacySettings(userIdToken: UserIdToken) : Pair<Int, PrivacySettingsEntity?>

    suspend fun tryEditPrivacySettings(privacySettingsRequest: PrivacySettingsRequest): Int
}