package com.example.studita.domain.interactor.subscribe_email

import com.example.studita.domain.entity.SubscribeEmailResultData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.repository.SubscribeEmailRepository
import com.example.studita.domain.service.SyncSubscribeEmail

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
                when {
                    e is NetworkConnectionException -> {
                        syncSubscribeEmail.scheduleSubscribeEmail(true, userIdTokenData)
                        SubscribeEmailResultStatus.NoConnection
                    }
                    retryCount == 0 -> SubscribeEmailResultStatus.ServiceUnavailable
                    else -> {
                        subscribe(userIdTokenData, retryCount - 1)
                    }
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
                when {
                    e is NetworkConnectionException -> {
                        syncSubscribeEmail.scheduleSubscribeEmail(false, userIdTokenData)
                        SubscribeEmailResultStatus.NoConnection
                    }
                    retryCount == 0 -> SubscribeEmailResultStatus.ServiceUnavailable
                    else -> {
                        subscribe(userIdTokenData, retryCount - 1)
                    }
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