package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.FriendData
import com.example.studita.presentation.model.FriendsRecyclerUiModel

class FriendsUiModelMapper : Mapper<FriendData, FriendsRecyclerUiModel.FriendItemUiModel>{
    override fun map(source: FriendData): FriendsRecyclerUiModel.FriendItemUiModel = FriendsRecyclerUiModel.FriendItemUiModel(source.friendId, source.friendName, source.avatarLink)

}