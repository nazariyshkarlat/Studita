package com.example.studita.data.repository.subscribe_mail

import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class CloudSubscribeEmailDataStore(private val connectionManager: ConnectionManager, private val subscribeEmailService: SubscribeEmailService): SubscribeEmailDataStore{
    override suspend fun trySubscribe(userTokenId: UserTokenId): Pair<Int, String> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            val result = subscribeEmailService.subscribeEmailAsync(userTokenId).await()
            val body = result.body()!!
            result.code() to body["user_email"]!!
        }

    override suspend fun tryUnsubscribe(userTokenId: UserTokenId): Pair<Int, String> =
        if(connectionManager.isNetworkAbsent()){
            throw NetworkConnectionException()
        }else{
            val result = subscribeEmailService.unsubscribeEmailAsync(userTokenId).await()
            val body = result.body()!!
            result.code() to body["user_email"]!!
        }

}