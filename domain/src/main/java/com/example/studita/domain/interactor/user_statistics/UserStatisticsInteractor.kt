package com.example.studita.domain.interactor.user_statistics

import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.UserStatisticsStatus

interface UserStatisticsInteractor {

    suspend fun getUserStatisticsInteractor(userId: String, userToken: String, time: UserStatisticsTime) : UserStatisticsStatus

}