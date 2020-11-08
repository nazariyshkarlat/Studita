package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.di.data.UserDataModule
import com.studita.di.data.UsersModule
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.UserData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.UsersResponseData
import com.studita.domain.interactor.FriendActionStatus
import com.studita.domain.interactor.GetUsersStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.*

class ProfileFragmentViewModel(private val myId: Int, private val profileId: Int) : ViewModel() {

    private val friendsInteractor = UsersModule.getUsersInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    val addFriendStatus = SingleLiveEvent<UsersInteractor.FriendActionState>()

    var addToFriendsJob: Job? = null

    val errorSnackbarEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<Boolean>()
    var errorState = false

    val userDataState = MutableLiveData<UserDataStatus>()

    val progressState = MutableLiveData<Boolean>()
    private var getProfileDataJob: Job? = null

    val friendsState = MutableLiveData<GetUsersStatus>()
    val isMyFriendState = MutableLiveData<IsMyFriendStatus.Success>()

    init {
        getProfileData()
    }

    fun getProfileData(isUpdate: Boolean = false) {
        if(!isUpdate)
            progressState.value = true

        errorState = true
        getProfileDataJob = viewModelScope.launchExt(getProfileDataJob) {
            val friendDataDeferred = async { getUserData(profileId, myId == profileId) }
            val friendsDeferred = async { getFriendsAsync(profileId) }
            val checkMyFriendDeferred = if (profileId != myId) async {
                checkIsMyFriend(
                    myId,
                    profileId
                )
            } else null
            val userDataStatus = friendDataDeferred.await()
            val getFriendsStatus = friendsDeferred.await()
            val checkMyFriendStatus = checkMyFriendDeferred?.await()
            when {
                userDataStatus is UserDataStatus.Success &&
                        (getFriendsStatus is GetUsersStatus.Success || getFriendsStatus is GetUsersStatus.NoUsersFound) &&
                        (checkMyFriendStatus == null || checkMyFriendStatus is IsMyFriendStatus.Success) -> {
                    userDataState.value = userDataStatus
                    friendsState.value = getFriendsStatus
                    if(!isUpdate)
                        progressState.value = false
                    if (profileId != myId)
                        isMyFriendState.value = checkMyFriendStatus as IsMyFriendStatus.Success
                }
                userDataStatus is UserDataStatus.NoConnection || getFriendsStatus is GetUsersStatus.NoConnection || checkMyFriendStatus is IsMyFriendStatus.NoConnection -> {
                    errorState = true
                    errorEvent.value = true
                }
                else -> {
                    errorState = true
                    errorEvent.value = false
                }
            }
        }
    }

    private suspend fun getUserData(userId: Int, isMyUserData: Boolean): UserDataStatus {
        return userDataInteractor.getUserData(userId, false, isMyUserData)
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
            val result = friendsInteractor.acceptFriendship(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result == FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result == FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun removeFriend(userIdToken: UserIdTokenData, userData: UserData) {
        val newValue = UsersInteractor.FriendActionState.RemovedFromFriends(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.removeFriend(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result == FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result == FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun cancelFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData) {
        val newValue = UsersInteractor.FriendActionState.FriendshipRequestIsCanceled(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.cancelFriendship(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result == FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result == FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun sendFriendshipRequest(userIdToken: UserIdTokenData, userData: UserData) {
        val newValue = UsersInteractor.FriendActionState.FriendshipRequestIsSent(
            userData.apply {
                isMyFriendStatus = IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
            })

        addToFriendsJob = GlobalScope.launchExt(addToFriendsJob) {
            val result = friendsInteractor.sendFriendship(
                FriendActionRequestData(
                    userIdToken,
                    userData.userId
                )
            )
            if (result == FriendActionStatus.Success)
                addFriendStatus.value = newValue
            else if(result == FriendActionStatus.ServiceUnavailable)
                errorSnackbarEvent.value = true
        }
        UserUtils.isMyFriendLiveData.value = newValue
    }

    fun refreshFriends(changedUser: UserData) {
        val friendsStateValue = friendsState.value
        if (changedUser.isMyFriendStatus is IsMyFriendStatus.Success.IsMyFriend) {
            if (friendsStateValue is GetUsersStatus.NoUsersFound) {
                friendsState.value = GetUsersStatus.Success(
                    UsersResponseData(1, arrayListOf(changedUser))
                )
            } else if (friendsStateValue is GetUsersStatus.Success) {
                if (friendsStateValue.friendsResponseData.users.none { it.userId == changedUser.userId }) {
                    friendsState.value = friendsStateValue.apply {
                        friendsResponseData.usersCount++
                        friendsResponseData.users.add(0, changedUser)
                    }
                }
            }
        } else {
            if (friendsStateValue is GetUsersStatus.Success) {
                if (friendsStateValue.friendsResponseData.usersCount == 1) {
                    friendsState.value = GetUsersStatus.NoUsersFound
                } else {
                    friendsState.value = friendsStateValue.apply {
                        friendsResponseData.usersCount--
                        friendsResponseData.users.removeAll { it.userId == changedUser.userId }
                    }
                }
            }
        }
    }

}