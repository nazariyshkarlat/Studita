package com.example.studita.domain.repository

import com.example.studita.domain.entity.SubscribeEmailResultData
import com.example.studita.domain.entity.UserIdTokenData

interface SubscribeEmailRepository {

    suspend fun subscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData>

    suspend fun unsubscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData>

    suspend fun saveSyncedResult(subscribeEmailResultData: SubscribeEmailResultData)

    fun getSyncedResult() : SubscribeEmailResultData?

}