package com.example.studita.data.repository.datasource.privacy_settings

import com.example.studita.data.entity.*

interface PrivacySettingsDataStore {

    suspend fun tryGetPrivacySettings(userIdToken: UserIdToken): Pair<Int, PrivacySettingsEntity?>

    suspend fun tryEditPrivacySettings(privacySettingsRequest: PrivacySettingsRequest): Int

    suspend fun tryGetPrivacyDuelsExceptionsList(
        userIdToken: UserIdToken,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<PrivacyDuelsExceptionsEntity>?>

    suspend fun tryEditDuelsExceptions(editDuelsExceptionsRequest: EditDuelsExceptionsRequest): Int
}