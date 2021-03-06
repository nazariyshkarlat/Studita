package com.studita.data.repository.datasource.users

import com.studita.data.entity.FriendActionRequest
import com.studita.data.entity.IsMyFriendEntity
import com.studita.data.entity.UsersResponse

interface UsersDataStore {

    suspend fun tryGetUsers(
        userId: Int,
        friendOfUserId: Int?,
        perPage: Int,
        pageNumber: Int,
        sortBy: String?,
        startsWith: String?
    ): Pair<Int, UsersResponse?>

    suspend fun tryCheckIsMyFriend(myId: Int, userId: Int): Pair<Int, IsMyFriendEntity?>

    suspend fun trySendFriendship(friendActionRequest: FriendActionRequest): Int

    suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int

    suspend fun tryAcceptFriendship(friendActionRequest: FriendActionRequest): Int

    suspend fun tryRejectFriendship(friendActionRequest: FriendActionRequest): Int

    suspend fun tryCancelFriendship(friendActionRequest: FriendActionRequest): Int

    suspend fun tryCheckHasFriends(userId: Int): Pair<Int, Boolean?>
}