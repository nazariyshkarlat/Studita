package com.example.studita.domain.interactor.user_statistics

import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.UserStatisticsRowData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserStatisticsStatus

interface UserStatisticsInteractor {

    suspend fun getUserStatistics(userId: Int) : UserStatisticsStatus

    suspend fun saveUserStatistics(idTokenData: UserIdTokenData?, userStatisticsRowData: UserStatisticsRowData)

    suspend fun getUserStatisticsRecords() : List<UserStatisticsRowData>?

}