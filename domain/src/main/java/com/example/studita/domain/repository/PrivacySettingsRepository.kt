package com.example.studita.domain.repository

import com.example.studita.domain.entity.*

interface PrivacySettingsRepository {

    suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): Pair<Int, PrivacySettingsData?>

    suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): Int

    suspend fun getPrivacyDuelsExceptionsList(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<PrivacyDuelsExceptionData>?>

    suspend fun editDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData): Int
}