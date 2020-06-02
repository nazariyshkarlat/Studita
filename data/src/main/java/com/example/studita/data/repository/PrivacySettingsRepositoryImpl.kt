package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.PrivacySettingsEntityMapper
import com.example.studita.data.entity.mapper.PrivacySettingsRequestMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreFactory
import com.example.studita.domain.entity.PrivacySettingsData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.PrivacySettingsRepository

class PrivacySettingsRepositoryImpl(private val privacySettingsDataStoreFactory: PrivacySettingsDataStoreFactory,
                                    private val userIdTokenMapper: UserIdTokenMapper,
                                    private val privacySettingsEntityMapper: PrivacySettingsEntityMapper,
                                    private val privacySettingsRequestMapper: PrivacySettingsRequestMapper) : PrivacySettingsRepository{
    override suspend fun getPrivacySettings(userIdTokenData: UserIdTokenData): Pair<Int, PrivacySettingsData?> {
        val pair =  privacySettingsDataStoreFactory.create().tryGetPrivacySettings(userIdTokenMapper.map(userIdTokenData))
        return pair.first to pair.second?.let { privacySettingsEntityMapper.map(it) }
    }

    override suspend fun editPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData): Int {
        return privacySettingsDataStoreFactory.create().tryEditPrivacySettings(privacySettingsRequestMapper.map(privacySettingsRequestData))
    }

}