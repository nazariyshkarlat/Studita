package com.example.studita.domain.interactor.user_statistics

import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.UserStatisticsStatus

interface UserStatisticsInteractor {

    suspend fun getUserStatisticsInteractor(userTokenIdData: UserTokenIdData, time: UserStatisticsTime) : UserStatisticsStatus

}