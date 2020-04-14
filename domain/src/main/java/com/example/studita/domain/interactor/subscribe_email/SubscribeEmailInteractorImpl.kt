package com.example.studita.domain.interactor.subscribe_email

import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.SubscribeEmailStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.user_data.UserDataInteractor
import com.example.studita.domain.repository.SubscribeEmailRepository
import com.example.studita.domain.repository.UserDataRepository

class SubscribeEmailInteractorImpl(private val repository: SubscribeEmailRepository) : SubscribeEmailInteractor {
    override suspend fun subscribe(userTokenIdData: UserTokenIdData): SubscribeEmailStatus =
        try {
            val result = repository.subscribe(userTokenIdData)
            when (result.first) {
                200 -> SubscribeEmailStatus.Success(result.second)
                else -> SubscribeEmailStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                SubscribeEmailStatus.NoConnection
            else
                SubscribeEmailStatus.ServiceUnavailable
        }

    override suspend fun unsubscribe(userTokenIdData: UserTokenIdData): SubscribeEmailStatus =
        try {
            val result = repository.unsubscribe(userTokenIdData)
            when (result.first) {
                200 -> SubscribeEmailStatus.Success(result.second)
                else -> SubscribeEmailStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                SubscribeEmailStatus.NoConnection
            else
                SubscribeEmailStatus.ServiceUnavailable
        }
}