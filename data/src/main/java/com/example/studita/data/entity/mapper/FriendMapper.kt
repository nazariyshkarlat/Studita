package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.FriendEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.FriendData

class FriendEntityMapper : Mapper<FriendEntity, FriendData>{
    override fun map(source: FriendEntity): FriendData = FriendData(source.friendId, source.friendName, source.avatarLink, DateTimeFormat().parse(source.dateTime))


}