package com.studita.data.repository.datasource.subscribe_mail

import com.studita.data.entity.SubscribeEmailResultEntity
import com.studita.data.entity.UserIdToken
import com.studita.data.net.SubscribeEmailService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class SubscribeEmailDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val subscribeEmailService: SubscribeEmailService
) : SubscribeEmailDataStore {
    override suspend fun trySubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = subscribeEmailService.subscribeEmail(userIdToken)
                val body = result.body()!!
                result.code() to SubscribeEmailResultEntity(true, body["user_email"])
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    override suspend fun tryUnsubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = subscribeEmailService.unsubscribeEmail(userIdToken)
                result.code() to result.body()
                    ?.let { SubscribeEmailResultEntity(false, it["user_email"]) }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

}