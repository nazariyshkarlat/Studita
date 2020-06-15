package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.users.UsersDataStoreFactory
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UsersResponseData
import com.example.studita.domain.repository.UsersRepository

class UsersRepositoryImpl(private val usersDataStoreFactory: UsersDataStoreFactory) : UsersRepository {
    override suspend fun getUsers(userId: Int, friendOfUserId: Int?, perPage: Int, pageNumber: Int, sortBy: UsersRepository.SortBy?, startsWith: String?): Pair<Int, UsersResponseData?> {
        val pair = usersDataStoreFactory.create().tryGetUsers(userId, friendOfUserId, perPage, pageNumber, sortBy?.toRaw(), startsWith)
        val friendsRequest = pair.second
        return pair.first to friendsRequest?.let{UsersResponseData(friendsRequest.usersCount, ArrayList(friendsRequest.users.map{it.toBusinessEntity()}))}
    }

    override suspend fun checkIsMyFriend(myId: Int, userId: Int): Pair<Int, Boolean?> {
        return usersDataStoreFactory.create().tryCheckIsMyFriend(myId, userId)
    }

    override suspend fun addFriend(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create().tryAddFriend(friendActionRequestData.toRawEntity())
    }

    override suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create().tryRemoveFriend(friendActionRequestData.toRawEntity())
    }

    override suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create().tryAcceptFriendship(friendActionRequestData.toRawEntity())
    }

    override suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create().tryRejectFriendship(friendActionRequestData.toRawEntity())
    }

}