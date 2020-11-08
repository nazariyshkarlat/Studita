package com.studita.domain.repository

import com.studita.domain.entity.SubscribeEmailResultData
import com.studita.domain.entity.UserIdTokenData

interface SubscribeEmailRepository {

    suspend fun subscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?>

    suspend fun unsubscribe(userIdTokenData: UserIdTokenData): Pair<Int, SubscribeEmailResultData?>

    suspend fun saveSyncedResult(subscribeEmailResultData: SubscribeEmailResultData)

    fun getSyncedResult(): SubscribeEmailResultData?

}