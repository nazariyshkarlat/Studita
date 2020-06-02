package com.example.studita.presentation.model

sealed class FriendsRecyclerUiModel{
    object SearchUiModel : FriendsRecyclerUiModel()
    data class FriendItemUiModel(
            val friendId: Int,
            val friendName: String,
            val avatarLink: String?
    ): FriendsRecyclerUiModel()
}