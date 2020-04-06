package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserStatisticsData

class UserStatisticsDataMapper : Mapper<UserStatisticsEntity, UserStatisticsData>{
    override fun map(source: UserStatisticsEntity) = UserStatisticsData(source.obtainedXP, source.obtained_time, source.obtained_lessons, source.obtained_trainings, source.obtained_achievements)

}