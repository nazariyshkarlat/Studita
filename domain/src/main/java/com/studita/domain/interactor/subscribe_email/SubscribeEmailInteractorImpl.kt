package com.studita.domain.interactor.subscribe_email

import com.studita.domain.entity.SubscribeEmailResultData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.FriendActionStatus
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.repository.SubscribeEmailRepository
import com.studita.domain.service.SyncFriendship
import com.studita.domain.service.SyncSubscribeEmail
import kotlinx.coroutines.delay

class SubscribeEmailInteractorImpl(
    private val repository: SubscribeEmailRepository,
    private val syncSubscribeEmail: SyncSubscribeEmail
) : SubscribeEmailInteractor {

    val retryDelay = 1000L

    override suspend fun subscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): SubscribeEmailResultStatus =
        try {
            val result = repository.subscribe(userIdTokenData)
            when (result.first) {
                200 -> SubscribeEmailResultStatus.Success(result.second!!)
                else -> SubscribeEmailResultStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncSubscribeEmail.scheduleSubscribeEmail(true, userIdTokenData)
                        SubscribeEmailResultStatus.NoConnection
                    }else
                        SubscribeEmailResultStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    subscribe(userIdTokenData, retryCount - 1)
                }
            } else
                SubscribeEmailResultStatus.Failure
        }

    override suspend fun unsubscribe(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): SubscribeEmailResultStatus =
        try {
            val result = repository.unsubscribe(userIdTokenData)
            when (result.first) {
                200 -> SubscribeEmailResultStatus.Success(result.second!!)
                else -> SubscribeEmailResultStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncSubscribeEmail.scheduleSubscribeEmail(false, userIdTokenData)
                        SubscribeEmailResultStatus.NoConnection
                    }else
                        SubscribeEmailResultStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    unsubscribe(userIdTokenData, retryCount - 1)
                }
            } else
                SubscribeEmailResultStatus.Failure
        }

    override suspend fun saveSyncedResult(status: SubscribeEmailResultStatus) {
        if (status is SubscribeEmailResultStatus.Success) {
            repository.saveSyncedResult(status.result)
        }
    }

    override fun getSyncedResult(): SubscribeEmailResultData? = repository.getSyncedResult()
}