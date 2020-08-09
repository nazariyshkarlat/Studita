package com.example.studita.domain.interactor.subscribe_email

import com.example.studita.domain.entity.SubscribeEmailResultData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.SubscribeEmailResultStatus

interface SubscribeEmailInteractor {

    suspend fun subscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 30
    ): SubscribeEmailResultStatus

    suspend fun unsubscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 30
    ): SubscribeEmailResultStatus

    suspend fun saveSyncedResult(status: SubscribeEmailResultStatus)
    fun getSyncedResult(): SubscribeEmailResultData?
}