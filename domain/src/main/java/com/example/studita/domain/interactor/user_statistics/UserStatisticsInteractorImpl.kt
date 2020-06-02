package com.example.studita.domain.interactor.user_statistics

import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.UserStatisticsRowData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsInteractorImpl(private val repository: UserStatisticsRepository) : UserStatisticsInteractor {
    override suspend fun getUserStatistics(
        userId: Int
    ) =
        try {
            val result = repository.getUserStatistics(userId)
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

    override suspend fun saveUserStatistics(idTokenData: UserIdTokenData?, userStatisticsRowData: UserStatisticsRowData) {
        if(idTokenData == null)
            repository.saveUserStatistics(userStatisticsRowData)
    }

    override suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>? {
        val stat = repository.getUserStatisticsRecords()
        return stat
    }


}