package com.example.studita.data.repository.subscribe_mail

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity
import com.example.studita.data.entity.UserTokenId

interface SubscribeEmailDataStore{

    suspend fun trySubscribe(userTokenId: UserTokenId) : Pair<Int, String>

    suspend fun tryUnsubscribe(userTokenId: UserTokenId): Pair<Int, String>

}