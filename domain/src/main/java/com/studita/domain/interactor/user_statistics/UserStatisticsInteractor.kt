package com.studita.domain.interactor.user_statistics

import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.UserStatisticsRowData
import com.studita.domain.interactor.UserStatisticsStatus

interface UserStatisticsInteractor {

    suspend fun getUserStatistics(userId: Int, retryCount: Int = 3): UserStatisticsStatus

    suspend fun saveUserStatistics(
        idTokenData: UserIdTokenData?,
        userStatisticsRowData: UserStatisticsRowData
    )

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>?

    suspend fun clearUserStaticsRecords()

}