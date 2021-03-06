package com.studita.domain.interactor.user_statistics

import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.UserStatisticsRowData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.UserStatisticsStatus
import com.studita.domain.repository.UserStatisticsRepository
import kotlinx.coroutines.delay

class UserStatisticsInteractorImpl(private val repository: UserStatisticsRepository) :
    UserStatisticsInteractor {

    private val retryDelay = 1000L

    override suspend fun getUserStatistics(
        userId: Int,
        retryCount: Int
    ): UserStatisticsStatus =
        try {
            val result = repository.getUserStatistics(userId)
            when (result.first) {
                200 -> UserStatisticsStatus.Success(result.second)
                else -> UserStatisticsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        UserStatisticsStatus.NoConnection
                    } else
                        UserStatisticsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getUserStatistics(userId, retryCount - 1)
                }
            } else
                UserStatisticsStatus.Failure
        }

    override suspend fun saveUserStatistics(
        idTokenData: UserIdTokenData?,
        userStatisticsRowData: UserStatisticsRowData
    ) {
        if (idTokenData == null)
            repository.saveUserStatistics(userStatisticsRowData)
    }

    override suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>? {
        return repository.getUserStatisticsRecords()
    }

    override suspend fun clearUserStaticsRecords() {
        repository.clearUserStatisticsRecords()
    }


}