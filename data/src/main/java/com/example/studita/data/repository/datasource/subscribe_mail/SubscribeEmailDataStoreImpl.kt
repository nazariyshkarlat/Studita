package com.example.studita.data.repository.datasource.subscribe_mail

import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class SubscribeEmailDataStoreImpl(private val connectionManager: ConnectionManager, private val subscribeEmailService: SubscribeEmailService): SubscribeEmailDataStore{
    override suspend fun trySubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            val result = subscribeEmailService.subscribeEmailAsync(userIdToken).await()
            val body = result.body()!!
            result.code() to SubscribeEmailResultEntity(true, body["user_email"])
        }

    override suspend fun tryUnsubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            val result = subscribeEmailService.unsubscribeEmailAsync(userIdToken).await()
            val body = result.body()!!
            result.code() to SubscribeEmailResultEntity(false, body["user_email"])
        }

}