package com.studita.data.repository.datasource.privacy_settings

import com.studita.data.entity.*
import com.studita.data.net.PrivacySettingsService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class PrivacySettingsDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val privacySettingsService: PrivacySettingsService
) : PrivacySettingsDataStore {
    override suspend fun tryGetPrivacySettings(userIdToken: UserIdToken): Pair<Int, PrivacySettingsEntity?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = privacySettingsService.getPrivacySettings(userIdToken)
                return result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
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
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryGetPrivacyDuelsExceptionsList(
        userIdToken: UserIdToken,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<PrivacyDuelsExceptionsEntity>?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = privacySettingsService.getPrivacyDuelsExceptionsList(
                    userIdToken,
                    perPage,
                    pageNumber
                )
                return result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryEditDuelsExceptions(editDuelsExceptionsRequest: EditDuelsExceptionsRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = privacySettingsService.editDuelsExceptions(editDuelsExceptionsRequest)
                return result.code()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

}