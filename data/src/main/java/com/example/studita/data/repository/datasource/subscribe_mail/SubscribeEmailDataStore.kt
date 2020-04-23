package com.example.studita.data.repository.datasource.subscribe_mail

import com.example.studita.data.entity.SubscribeEmailResultEntity
import com.example.studita.data.entity.UserIdToken

interface SubscribeEmailDataStore{

    suspend fun trySubscribe(userIdToken: UserIdToken) : Pair<Int, SubscribeEmailResultEntity>

    suspend fun tryUnsubscribe(userIdToken: UserIdToken): Pair<Int, SubscribeEmailResultEntity>

}