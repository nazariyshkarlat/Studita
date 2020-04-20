package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_data.UserDataDao
import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.repository.datasource.user_data.UserDataDataStore
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString

class DiskUserStatisticsDataStore(private val userStatisticsDao: UserStatisticsDao)
    : UserStatisticsDataStore {

    override suspend fun getUserStatisticsEntity(userTokenId: UserTokenId, time: UserStatisticsTime) = 200 to userStatisticsDao.getUserStatistics(time.timeToString())
}