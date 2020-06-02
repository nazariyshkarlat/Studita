package com.example.studita.data.repository.datasource.privacy_settings

import com.example.studita.data.entity.PrivacySettingsEntity
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.net.PrivacySettingsService
import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class PrivacySettingsDataStoreImpl(private val connectionManager: ConnectionManager, private val privacySettingsService: PrivacySettingsService): PrivacySettingsDataStore{
    override suspend fun tryGetPrivacySettings(userIdToken: UserIdToken): Pair<Int, PrivacySettingsEntity?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = privacySettingsService.getPrivacySettings(userIdToken)
                return result.code() to result.body()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryEditPrivacySettings(privacySettingsRequest: PrivacySettingsRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                return privacySettingsService.editPrivacySettings(privacySettingsRequest).code()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

}