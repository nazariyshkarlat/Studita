package com.example.studita.presentation.model

import com.example.studita.domain.entity.PrivacyDuelsExceptionData
import com.example.studita.domain.entity.UserData

sealed class PrivacySettingsDuelsExceptionsRecyclerUiModel{
    data class UserItemUiModel(
        val userId: Int,
        val userName: String,
        val avatarLink: String?,
        var isException: Boolean): PrivacySettingsDuelsExceptionsRecyclerUiModel()
    object ProgressUiModel : PrivacySettingsDuelsExceptionsRecyclerUiModel()
}

fun PrivacyDuelsExceptionData.toShapeUiModel() = PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel(userId, userName, avatarLink, isException)