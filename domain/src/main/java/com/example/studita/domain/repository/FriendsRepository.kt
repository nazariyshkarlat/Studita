package com.example.studita.domain.repository

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.FriendsResponseData
import com.example.studita.domain.entity.UserIdTokenData

interface FriendsRepository {

    suspend fun getFriends(userId: Int, startIndex: Int, endIndex: Int): Pair<Int, FriendsResponseData?>

    suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int) : Pair<Int, Boolean?>

    suspend fun addFriend(friendActionRequestData: FriendActionRequestData): Int

    suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): Int
}