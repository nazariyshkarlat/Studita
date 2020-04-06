package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.domain.entity.UserDataData

class UserDataDataMapper : Mapper<UserDataEntity, UserDataData>{
    override fun map(source: UserDataEntity) = UserDataData(source.userName, source.avatarLink, source.currentLevel, source.currentLevelXP, source.streakDays)

}