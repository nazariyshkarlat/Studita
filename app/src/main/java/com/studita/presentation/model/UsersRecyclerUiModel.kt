package com.studita.presentation.model

import com.studita.domain.entity.UserData
import com.studita.domain.interactor.IsMyFriendStatus

sealed class UsersRecyclerUiModel {
    object SearchUiModel : UsersRecyclerUiModel()
    data class UserItemUiModel(
        val userId: Int,
        val userName: String,
        val avatarLink: String?,
        var isMyFriendStatus: IsMyFriendStatus
    ) : UsersRecyclerUiModel()

    object ProgressUiModel : UsersRecyclerUiModel()
    class TextItemUiModel(val text: String) : UsersRecyclerUiModel()
}

fun UserData.toUserItemUiModel() =
    UsersRecyclerUiModel.UserItemUiModel(userId, userName, avatarLink, isMyFriendStatus)