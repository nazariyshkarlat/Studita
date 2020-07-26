package com.example.studita.domain.interactor.users

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.HasFriendsStatus
import com.example.studita.domain.repository.UsersRepository

interface UsersInteractor {

    val defSortBy: UsersRepository.SortBy

    suspend fun getUsers(friendOfUserId: Int?, perPage: Int, pageNumber: Int, userId: Int, sortBy: UsersRepository.SortBy? = null, startsWith: String? = null, retryCount: Int = 30): GetUsersStatus

    suspend fun checkIsMyFriend(myId: Int, userId: Int, retryCount: Int = 30): IsMyFriendStatus

    suspend fun sendFriendship(friendActionRequestData: FriendActionRequestData, retryCount: Int = 30): FriendActionStatus

    suspend fun removeFriend(friendActionRequestData: FriendActionRequestData, retryCount: Int = 30): FriendActionStatus

    suspend fun cancelFriendship(friendActionRequestData: FriendActionRequestData, retryCount: Int = 30): FriendActionStatus

    suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData, retryCount: Int = 30): FriendActionStatus

    suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData, retryCount: Int = 30): FriendActionStatus

    suspend fun hasFriends(userId: Int, retryCount: Int = 30): HasFriendsStatus

    sealed class FriendActionState(open val userData: UserData){
        data class FriendshipRequestIsAccepted(override val userData: UserData): FriendActionState(userData)
        data class RemovedFromFriends(override val userData: UserData): FriendActionState(userData)
        data class FriendshipRequestIsCanceled(override val userData: UserData): FriendActionState(userData)
        data class FriendshipRequestIsRejected(override  val userData: UserData): FriendActionState(userData)
        data class FriendshipRequestIsSent(override val userData: UserData): FriendActionState(userData)
    }
}