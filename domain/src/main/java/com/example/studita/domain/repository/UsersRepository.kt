package com.example.studita.domain.repository

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.IsMyFriendData
import com.example.studita.domain.entity.UsersResponseData

interface UsersRepository {

    suspend fun getUsers(
        userId: Int,
        friendOfUserId: Int?,
        perPage: Int,
        pageNumber: Int,
        sortBy: SortBy?,
        startsWith: String?
    ): Pair<Int, UsersResponseData?>

    suspend fun checkIsMyFriend(myId: Int, userId: Int): Pair<Int, IsMyFriendData?>

    suspend fun sendFriendship(friendActionRequestData: FriendActionRequestData): Int

    suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): Int

    suspend fun cancelFriendship(friendActionRequestData: FriendActionRequestData): Int

    suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData): Int

    suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData): Int

    suspend fun hasFriends(userId: Int): Pair<Int, Boolean?>

    enum class SortBy {
        A_TO_Z,
        Z_TO_A,
        NEW_TO_OLD,
        OLD_TO_NEW
    }

    fun SortBy.toRaw() = when (this) {
        SortBy.A_TO_Z -> "A_to_Z"
        SortBy.Z_TO_A -> "Z_to_A"
        SortBy.NEW_TO_OLD -> "new_to_old"
        SortBy.OLD_TO_NEW -> "old_to_new"
    }
}