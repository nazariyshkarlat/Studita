package com.example.studita.data.repository.datasource.users

import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.data.entity.UsersResponse

interface UsersDataStore{

    suspend fun tryGetUsers(userId: Int, friendOfUserId: Int?, perPage: Int, pageNumber: Int, sortBy: String?, startsWith: String?) : Pair<Int, UsersResponse?>

    suspend fun tryCheckIsMyFriend(myId: Int, userId: Int) : Pair<Int, Boolean?>

    suspend fun tryAddFriend(friendActionRequest: FriendActionRequest): Int

    suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int
}