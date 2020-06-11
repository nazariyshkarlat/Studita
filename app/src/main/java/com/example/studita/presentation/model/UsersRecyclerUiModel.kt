package com.example.studita.presentation.model

import com.example.studita.domain.entity.UserData

sealed class UsersRecyclerUiModel{
    object SearchUiModel : UsersRecyclerUiModel()
    data class UserItemUiModel(
        val userId: Int,
        val userName: String,
        val avatarLink: String?,
        var isMyFriend: Boolean): UsersRecyclerUiModel()
    object ProgressUiModel : UsersRecyclerUiModel()
    class TextItemUiModel(val text: String): UsersRecyclerUiModel()
}

fun UserData.toUserItemUiModel() = UsersRecyclerUiModel.UserItemUiModel(userId, userName, avatarLink, isMyFriend)