package com.example.studita.presentation.model

import com.example.studita.domain.entity.PrivacyDuelsExceptionData

sealed class PrivacySettingsDuelsExceptionsRecyclerUiModel {
    data class UserItemUiModel(
        val userId: Int,
        val userName: String,
        val avatarLink: String?,
        var isException: Boolean
    ) : PrivacySettingsDuelsExceptionsRecyclerUiModel()

    object ProgressUiModel : PrivacySettingsDuelsExceptionsRecyclerUiModel()
}

fun PrivacyDuelsExceptionData.toUiModel() =
    PrivacySettingsDuelsExceptionsRecyclerUiModel.UserItemUiModel(
        userId,
        userName,
        avatarLink,
        isException
    )