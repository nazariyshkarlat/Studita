package com.studita.data.repository

import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreFactory
import com.studita.domain.entity.*
import com.studita.domain.repository.PrivacySettingsRepository

class PrivacySettingsRepositoryImpl(private val privacySettingsDataStoreFactory: PrivacySettingsDataStoreFactory) :
    PrivacySettingsRepository {
    override suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): Pair<Int, PrivacySettingsData?> {
        val pair = privacySettingsDataStoreFactory.create()
            .tryGetPrivacySettings(userIdTokenData.toRawEntity())
        return pair.first to pair.second?.toBusinessEntity()
    }

    override suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): Int {
        return privacySettingsDataStoreFactory.create()
            .tryEditPrivacySettings(privacySettingsRequestData.toRawEntity())
    }

    override suspend fun getPrivacyDuelsExceptionsList(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<PrivacyDuelsExceptionData>?> {
        val pair = privacySettingsDataStoreFactory.create()
            .tryGetPrivacyDuelsExceptionsList(userIdTokenData.toRawEntity(), perPage, pageNumber)
        return pair.first to pair.second?.map { it.toBusinessEntity() }
    }

    override suspend fun editDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData): Int {
        return privacySettingsDataStoreFactory.create()
            .tryEditDuelsExceptions(editDuelsExceptionsRequestData.toRawEntity())
    }

}