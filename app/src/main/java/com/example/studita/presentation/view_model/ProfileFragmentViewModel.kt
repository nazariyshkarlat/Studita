package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.UsersModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.*
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class ProfileFragmentViewModel : ViewModel() {

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()

    var addToFriendsJob: Job? = null
    var getFriendsJob: Job? = null

    val userDataState = MutableLiveData<UserDataStatus>()

    val friendsState = MutableLiveData<GetUsersStatus>()
    val isMyFriendState = MutableLiveData<IsMyFriendStatus.Success>()

    fun getProfileData(myId: Int, friendOfUserId: Int) {
        viewModelScope.launch {
            val friendDataDeferred = async { getFriendData(friendOfUserId) }
            val friendsDeferred = async { getFriendsAsync(friendOfUserId) }
            val checkMyFriendDeferred = if (friendOfUserId != myId) async {
                checkIsMyFriend(
                    myId,
                    friendOfUserId
                )
            } else null
            val userDataStatus = friendDataDeferred.await()
            val getFriendsStatus = friendsDeferred.await()
            val checkMyFriendStatus = checkMyFriendDeferred?.await()
            if (userDataStatus is UserDataStatus.Success &&
                (getFriendsStatus is GetUsersStatus.Success || getFriendsStatus is GetUsersStatus.NoUsersFound) &&
                (checkMyFriendStatus == null || checkMyFriendStatus is IsMyFriendStatus.Success)
            ) {
                userDataState.postValue(userDataStatus)
                friendsState.postValue(getFriendsStatus)
                if (friendOfUserId != myId)
                    isMyFriendState.postValue(checkMyFriendStatus as IsMyFriendStatus.Success)
            }
        }
    }

    private suspend fun getFriendData(userId: Int): UserDataStatus {
        return userDataInteractor.getUserData(userId, false)
    }

    private suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): IsMyFriendStatus {
        return friendsInteractor.checkIsMyFriend(userId, anotherUserId)
    }

    private suspend fun getFriendsAsync(userId: Int): GetUsersStatus {
        return friendsInteractor.getUsers(
            userId,
            3,
            1,
            sortBy = friendsInteractor.defSortBy,
            userId = PrefsUtils.getUserId()!!
        )
    }

    fun acceptFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData) {
        val newValue = UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
            userData.apply { isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userId) })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.acceptFriendship(FriendActionRequestData(userIdToken, userData.userId))
            if (result == FriendActionStatus.Success)
                addFriendStatus.postValue(newValue)
        }
        UserUtils.isMyFriendLiveData.postValue(newValue)
    }

    fun removeFriend(userIdToken: UserIdTokenData, userData: UserData){
        val newValue = UsersInteractor.FriendActionState.RemovedFromFriends(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.removeFriend(FriendActionRequestData(userIdToken, userData.userId))
            if (result == FriendActionStatus.Success)
                addFriendStatus.postValue(newValue)
        }
        UserUtils.isMyFriendLiveData.postValue(newValue)
    }

    fun cancelFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData){
        val newValue = UsersInteractor.FriendActionState.FriendshipRequestIsCanceled(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.cancelFriendship(FriendActionRequestData(userIdToken, userData.userId))
            if (result == FriendActionStatus.Success)
                addFriendStatus.postValue(newValue)
        }
        UserUtils.isMyFriendLiveData.postValue(newValue)
    }

    fun sendFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData){
        val newValue = UsersInteractor.FriendActionState.FriendshipRequestIsSent(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.sendFriendship(FriendActionRequestData(userIdToken, userData.userId))
            if (result == FriendActionStatus.Success)
                addFriendStatus.postValue(newValue)
        }
        UserUtils.isMyFriendLiveData.postValue(newValue)
    }

    fun refreshFriends(changedUser: UserData){
        val friendsStateValue = friendsState.value
        if(changedUser.isMyFriendStatus is IsMyFriendStatus.Success.IsMyFriend){
            if(friendsStateValue is GetUsersStatus.NoUsersFound){
                friendsState.value = GetUsersStatus.Success(
                    UsersResponseData(1, arrayListOf(changedUser))
                )
            }else if(friendsStateValue is GetUsersStatus.Success){
                if(friendsStateValue.friendsResponseData.users.none{it.userId == changedUser.userId}) {
                    friendsState.value = friendsStateValue.apply {
                        friendsResponseData.usersCount++
                        friendsResponseData.users.add(0, changedUser)
                    }
                }
            }
        }else{
            if(friendsStateValue is GetUsersStatus.Success){
                if(friendsStateValue.friendsResponseData.usersCount == 1){
                    friendsState.value = GetUsersStatus.NoUsersFound
                }else {
                    friendsState.value = friendsStateValue.apply {
                        friendsResponseData.usersCount--
                        friendsResponseData.users.removeAll { it.userId == changedUser.userId}
                    }
                }
            }
        }
    }

}