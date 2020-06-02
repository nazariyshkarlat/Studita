package com.example.studita.data.repository.datasource.subscribe_mail

import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class SubscribeEmailDataStoreImpl(private val connectionManager: ConnectionManager, private val subscribeEmailService: SubscribeEmailService): SubscribeEmailDataStore{
    override suspend fun trySubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            try {
                val result = subscribeEmailService.subscribeEmail(userIdToken)
                val body = result.body()!!
                result.code() to SubscribeEmailResultEntity(true, body["user_email"])
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }

    override suspend fun tryUnsubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity?> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            try {
                val result = subscribeEmailService.unsubscribeEmail(userIdToken)
                result.code() to result.body()?.let{SubscribeEmailResultEntity(false, it["user_email"])}
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }

}