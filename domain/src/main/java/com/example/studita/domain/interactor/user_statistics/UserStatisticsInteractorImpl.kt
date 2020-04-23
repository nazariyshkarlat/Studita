package com.example.studita.domain.interactor.user_statistics

import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsInteractorImpl(private val repository: UserStatisticsRepository) : UserStatisticsInteractor{
    override suspend fun getUserStatisticsInteractor(
        userIdTokenData: UserIdTokenData
    )=
        try {
            val result = repository.getUserStatistics(userIdTokenData)
            when (result.first) {
                200 -> UserStatisticsStatus.Success(result.second)
                else -> UserStatisticsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                UserStatisticsStatus.NoConnection
            else
                UserStatisticsStatus.ServiceUnavailable
        }

}