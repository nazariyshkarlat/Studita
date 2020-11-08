package com.studita.domain.interactor.subscribe_email

import com.studita.domain.entity.SubscribeEmailResultData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.SubscribeEmailResultStatus

interface SubscribeEmailInteractor {

    suspend fun subscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 3
    ): SubscribeEmailResultStatus

    suspend fun unsubscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 3
    ): SubscribeEmailResultStatus

    suspend fun saveSyncedResult(status: SubscribeEmailResultStatus)
    fun getSyncedResult(): SubscribeEmailResultData?
}