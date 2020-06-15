package com.example.studita.domain.interactor.users

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.repository.UsersRepository

interface UsersInteractor {

    val defSortBy: UsersRepository.SortBy

    suspend fun getUsers(friendOfUserId: Int?, perPage: Int, pageNumber: Int, userId: Int, sortBy: UsersRepository.SortBy? = null, startsWith: String? = null): GetUsersStatus

    suspend fun checkIsMyFriend(myId: Int, userId: Int): CheckIsMyFriendStatus

    suspend fun addFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus

    suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus

    suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData): FriendActionStatus

    suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData): FriendActionStatus
}