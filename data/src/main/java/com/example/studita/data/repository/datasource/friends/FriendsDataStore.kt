package com.example.studita.data.repository.datasource.friends

import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.FriendEntity
import com.example.studita.data.entity.FriendsResponse
import com.example.studita.domain.entity.FriendsResponseData

interface FriendsDataStore{

    suspend fun tryGetFriends(userId: Int, startIndex: Int, endIndex: Int) : Pair<Int, FriendsResponse?>

    suspend fun tryCheckIsMyFriend(userId: Int, anotherUserId: Int) : Pair<Int, Boolean?>

    suspend fun tryAddFriend(friendActionRequest: FriendActionRequest): Int

    suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int
}