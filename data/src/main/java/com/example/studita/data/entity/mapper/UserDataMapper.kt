package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.UserDataData
import kotlin.collections.ArrayList

class UserDataDataMapper : Mapper<UserDataEntity, UserDataData>{

    override fun map(source: UserDataEntity) = UserDataData(source.userId, source.userName, source.userFullName, source.userPublicId, source.avatarLink, source.currentLevel, source.currentLevelXP, source.streakDays, source.isSubscribed, ArrayList(source.completedParts), DateTimeFormat().parse(source.streakDatetime), source.todayCompletedExercises)

}

class UserDataEntityMapper : Mapper<UserDataData, UserDataEntity>{

    override fun map(source: UserDataData) = UserDataEntity(source.userId, source.userName, source.userFullName, source.userPublicId, source.avatarLink, source.currentLevel, source.currentLevelXP, source.streakDays, source.isSubscribed, ArrayList(source.completedParts), DateTimeFormat().format(source.streakDatetime), source.todayCompletedExercises)

}