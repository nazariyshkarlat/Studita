package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.model.HomeRecyclerUiModel

class HomeUserDataUiModelMapper : Mapper<UserDataData, HomeRecyclerUiModel.HomeUserDataUiModel>{
    override fun map(source: UserDataData)= HomeRecyclerUiModel.HomeUserDataUiModel(source.currentLevel, source.currentLevelXP, streakDays = source.streakDays)


}